package org.whisky.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.whisky.domain.entity.Whisky;
import org.whisky.domain.entity.WhiskyMetaData;
import org.whisky.domain.repository.WhiskyRepository;
import org.springframework.util.StringUtils;


import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhiskyCrawler {

    private final WhiskyRepository whiskyRepository;
    private static final int TARGET_COUNT = 5000;

    // 리스트 URL
    private static final String BASE_LIST_URL = "https://www.masterofmalt.com/country-style/scotch/blended-whisky/";

    public void runCrawler() {
        log.info("🔧 실행 중인 크롬(9222 포트)에 연결 시도 중...");

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");

        // [중요] 타임아웃 설정 대폭 증가
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

        int currentCount = 0;
        int pageNum = 1;
        Random random = new Random();
        Set<String> visitedUrlsInPage = new HashSet<>();

        try {
            log.info("🚀 [God Mode] 위스키 크롤링 시작...");

            while (currentCount < TARGET_COUNT) {
                String currentListUrl = (pageNum == 1) ? BASE_LIST_URL : BASE_LIST_URL + pageNum + "/";
                log.info(">>> [페이지 이동] {} 페이지: {}", pageNum, currentListUrl);

                try {
                    driver.get(currentListUrl);
                    // [핵심] 페이지 로딩/리다이렉트 안정화를 위한 강제 대기
                    Thread.sleep(2500);
                    scrollDown(driver);
                } catch (Exception e) {
                    log.warn("⚠️ 페이지 이동/스크롤 중 경미한 오류 (진행함): {}", e.getMessage());
                }

                Document listDoc = Jsoup.parse(driver.getPageSource());
                Elements productLinks = listDoc.select("a[href*='/whiskies/']");

                if (productLinks.isEmpty()) {
                    log.warn("⚠️ {}페이지 상품 로딩 실패 혹은 끝. (잠시 대기 후 종료)", pageNum);
                    if (pageNum > 1) break;
                }

                log.info("🔍 {}페이지 링크 {}개 발견.", pageNum, productLinks.size());

                List<String> detailUrls = new ArrayList<>();
                visitedUrlsInPage.clear();

                for (Element link : productLinks) {
                    String href = link.attr("href");
                    if (!href.startsWith("http")) href = "https://www.masterofmalt.com" + href;

                    if (href.contains("#reviews") || href.contains("login") || href.contains("samples")) continue;
                    if (visitedUrlsInPage.contains(href)) continue;

                    visitedUrlsInPage.add(href);
                    detailUrls.add(href);
                }

                int savedInThisPage = 0;
                for (String detailUrl : detailUrls) {
                    if (currentCount >= TARGET_COUNT) break;

                    boolean isSaved = crawlAndSaveDetail(driver, detailUrl, currentCount + 1);
                    if (isSaved) {
                        currentCount++;
                        savedInThisPage++;
                    }
                    Thread.sleep(600 + random.nextInt(400)); // 매너 딜레이
                }

                log.info("📊 {}페이지 완료: {}개 저장됨. (누적: {})", pageNum, savedInThisPage, currentCount);
                pageNum++;
            }

        } catch (Exception e) {
            log.error("🔥 크롤링 중 치명적 오류: ", e);
        } finally {
            log.info("🏁 크롤링 종료. 총 {}개 처리.", currentCount);
        }
    }

    // [수정] 스크롤 안전 장치 추가
    private void scrollDown(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            for (int i = 0; i < 3; i++) {
                try {
                    js.executeScript("window.scrollBy(0, 1000)");
                } catch (Exception e) {
                    // 스크롤 실패는 무시하고 진행
                }
                Thread.sleep(800);
            }
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Transactional
    protected boolean crawlAndSaveDetail(WebDriver driver, String url, int index) {
        try {
            driver.get(url);
            Document doc = Jsoup.parse(driver.getPageSource());
            ObjectMapper mapper = new ObjectMapper();

            // 1. JSON-LD 파싱 (이름, 이미지, 가격)
            String name = "Unknown";
            String imageUrl = "";
            Double price = 0.0;

            Elements scriptTags = doc.select("script[type=application/ld+json]");
            for (Element script : scriptTags) {
                try {
                    JsonNode node = mapper.readTree(script.html());
                    List<JsonNode> candidates = new ArrayList<>();
                    if (node.isArray()) {
                        for (JsonNode item : node) candidates.add(item);
                    } else {
                        candidates.add(node);
                    }

                    for (JsonNode productNode : candidates) {
                        if (productNode.has("@type") && "Product".equals(productNode.get("@type").asText())) {
                            name = productNode.has("name") ? productNode.get("name").asText() : name;
                            imageUrl = productNode.has("image") ? productNode.get("image").asText() : imageUrl;

                            if (productNode.has("offers")) {
                                JsonNode offers = productNode.get("offers");
                                if (offers.isArray() && offers.size() > 0) {
                                    price = offers.get(0).get("price").asDouble();
                                } else if (offers.has("price")) {
                                    price = offers.get("price").asDouble();
                                }
                            }
                            break;
                        }
                    }
                } catch (Exception e) {}
            }

            // JSON 실패 시 HTML Title에서 백업
            if ("Unknown".equals(name)) {
                name = doc.title().replace("| Master of Malt", "").trim();
            }

            if (whiskyRepository.existsByName(name)) {
                log.info("PASS (중복): {}", name);
                return false;
            }

            // ============================================================
            // 2. [강력해진] 스펙 데이터 추출 (ABV, Age, Bottler 보완)
            // ============================================================

            // (1) Breadcrumb (Country, Type)
            Map<String, String> breadcrumbInfo = analyzeBreadcrumbs(doc);
            String country = breadcrumbInfo.getOrDefault("country", "");
            String type = breadcrumbInfo.getOrDefault("type", "");

            // (2) DOM 기반 보완
            if (!StringUtils.hasText(country)) country = extractValueFromDom(doc, "Country", "Origin", "Region");
            if (!StringUtils.hasText(type)) type = extractValueFromDom(doc, "Style", "Category", "Whisky Style");

            // (3) Bottler (브랜드) - "The" 같은 잡음 제거 로직 추가
            String rawBottler = extractValueFromDom(doc, "Bottler", "Brand", "Distillery");
            String bottler = refineBottler(rawBottler, name);

            // (4) [핵심] ABV (도수) - 본문 전체 스캔 방식
            Double abv = extractAbvRobust(doc);

            // (5) [핵심] Age (숙성년도) - 이름 우선 추출 방식
            Integer age = extractAgeRobust(name, doc);

            // 데이터 정제
            country = normalizeCountry(country);
            if (!StringUtils.hasText(country)) country = "Unknown";
            if (!StringUtils.hasText(type)) type = "Whisky";

            // 3. Tasting Note
            String nose = extractTastingNote(doc, "Nose");
            String palate = extractTastingNote(doc, "Palate");
            String finish = extractTastingNote(doc, "Finish");

            // 4. 저장
            WhiskyMetaData metadata = WhiskyMetaData.builder()
                    .type(type)
                    .country(country)
                    .bottler(bottler)
                    .price(price)
                    .age(age)
                    .abv(abv)
                    .build();

            Whisky whisky = Whisky.builder()
                    .name(name)
                    .imageUrl(imageUrl)
                    .nose(nose)
                    .palate(palate)
                    .finish(finish)
                    .metadata(metadata)
                    .build();

            whiskyRepository.save(whisky);
            // 로그에 상세 정보 출력하여 확인
            log.info("[저장] #{} {} (Age:{}, ABV:{}%, Brand:{})", index, name, age, abv, bottler);

            return true;

        } catch (Exception e) {
            log.error("상세 페이지 파싱 실패: {} / url: {}", e.getMessage(), url);
            return false;
        }
    }

    // ============================================================
    // Robust Extraction Methods (핵심 로직)
    // ============================================================

    /**
     * ABV 추출: 태그 의존성을 줄이고 본문 전체에서 % 숫자를 찾음
     */
    private Double extractAbvRobust(Document doc) {
        // 1. 태그 시도
        String val = extractValueFromDom(doc, "Alcohol", "ABV", "Volume");
        Double parsed = parseAbv(val);
        if (parsed != null) return parsed;

        // 2. 본문 스캔 (실패 시)
        Elements containers = doc.select(".product-details, .product-box-wide, #ContentPlaceHolder1_ctl00_ctl00_wdDetails_lblDetails");
        String text = containers.hasText() ? containers.text() : doc.body().text();

        // 40.5%, 43 % 등 찾기
        Pattern p = Pattern.compile("(\\d{1,2}(\\.\\d{1,2})?)\\s?%");
        Matcher m = p.matcher(text);

        while (m.find()) {
            try {
                double found = Double.parseDouble(m.group(1));
                // 위스키 도수 범위 (30~80) 체크로 오탐 방지
                if (found > 30 && found < 80) return found;
            } catch (Exception e) {}
        }
        return null;
    }

    /**
     * Age 추출: 이름에서 추출하는 것을 최우선으로 함
     */
    private Integer extractAgeRobust(String name, Document doc) {
        // 1. 이름에서 찾기 (제일 정확함) - 예: "Glenfiddich 12 Year Old"
        Pattern titlePattern = Pattern.compile("(\\d{1,2})\\s?(Year|yo|Year Old|Y.O)", Pattern.CASE_INSENSITIVE);
        Matcher m = titlePattern.matcher(name);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }

        // 2. 스펙 테이블에서 찾기
        String ageStr = extractValueFromDom(doc, "Age");
        return parseAge(ageStr);
    }

    /**
     * Bottler 정제: "The", "Whisky" 등이 잡히면 이름에서 다시 추출
     */
    private String refineBottler(String extracted, String name) {
        String clean = (extracted == null) ? "" : extracted.trim();
        Set<String> stopWords = Set.of("The", "A", "Whisky", "Whiskey", "Blended", "Single", "Living", "Scope");

        if (clean.isEmpty() || stopWords.contains(clean) || clean.length() < 2) {
            // 이름의 첫 단어 사용 (예: "Macallan 12" -> "Macallan")
            String[] parts = name.split(" ");
            if (parts.length > 0) {
                // "The Macallan" 인 경우 두 번째 단어까지 고려
                if (parts[0].equalsIgnoreCase("The") && parts.length > 1) {
                    return parts[1];
                }
                return parts[0];
            }
        }
        return clean;
    }

    /**
     * DOM 탐색: Label 옆 형제 or 부모의 텍스트 추출 (Grid 구조 대응)
     */
    private String extractValueFromDom(Document doc, String... keywords) {
        for (String key : keywords) {
            Elements labels = doc.select("th:contains(" + key + "), strong:contains(" + key + "), b:contains(" + key + "), span:contains(" + key + ")");

            for (Element label : labels) {
                if (label.text().length() > key.length() + 8) continue;

                // 1. 형제 요소
                Element next = label.nextElementSibling();
                if (next != null && StringUtils.hasText(next.text())) return next.text().trim();

                // 2. 부모의 형제 (Grid layout)
                if (label.parent() != null) {
                    Element parentNext = label.parent().nextElementSibling();
                    if (parentNext != null && StringUtils.hasText(parentNext.text())) {
                        String val = parentNext.text().trim();
                        if (val.length() < 50) return val;
                    }

                    // 3. 부모 내부 텍스트
                    String parentText = label.parent().text();
                    String val = parentText.replace(label.text(), "").replaceAll("^[:\\s]+", "").trim();
                    if (StringUtils.hasText(val) && val.length() < 50) return val;
                }
            }
        }
        return "";
    }

    private Map<String, String> analyzeBreadcrumbs(Document doc) {
        Map<String, String> result = new HashMap<>();
        Elements crumbs = doc.select(".breadcrumb li, .breadcrumbs li, #breadcrumbs a");

        for (Element crumb : crumbs) {
            String text = crumb.text().trim();
            if (text.isEmpty() || text.equalsIgnoreCase("Home") || text.equalsIgnoreCase("Whiskies")) continue;

            if (!result.containsKey("country")) {
                if (text.contains("Scotch")) result.put("country", "Scotland");
                else if (text.contains("American") || text.contains("Bourbon")) result.put("country", "USA");
                else if (text.contains("Japanese")) result.put("country", "Japan");
                else if (text.contains("Irish")) result.put("country", "Ireland");
                else if (text.contains("Canadian")) result.put("country", "Canada");
            }

            if (text.contains("Single Malt") || text.contains("Blended") || text.contains("Bourbon") ||
                    text.contains("Rye") || text.contains("Grain")) {
                result.put("type", text.replace(" Whisky", "").replace(" Whiskey", "").trim());
            }
        }
        return result;
    }

    private String normalizeCountry(String raw) {
        if (!StringUtils.hasText(raw)) return "Unknown";
        String lower = raw.toLowerCase();
        if (lower.contains("scotch") || lower.contains("scotland")) return "Scotland";
        if (lower.contains("america") || lower.contains("usa") || lower.contains("bourbon")) return "USA";
        if (lower.contains("japan")) return "Japan";
        if (lower.contains("irish") || lower.contains("ireland")) return "Ireland";
        return raw;
    }

    private String extractTastingNote(Document doc, String keyword) {
        Element byId = doc.selectFirst("[id$=" + keyword.toLowerCase() + "TastingNote]");
        if (byId != null) return cleanNoteText(byId.text(), keyword);

        Elements byText = doc.select("p:contains(" + keyword + "), div:contains(" + keyword + ")");
        for (Element el : byText) {
            if (el.text().trim().startsWith(keyword)) {
                return cleanNoteText(el.text(), keyword);
            }
        }
        return "";
    }

    private String cleanNoteText(String text, String keyword) {
        return text.replaceAll("(?i)^" + keyword + "\\s*[:\\-]?\\s*", "").trim();
    }

    private Integer parseAge(String raw) {
        if (!StringUtils.hasText(raw)) return null;
        try {
            String num = raw.replaceAll("[^0-9]", "");
            if (StringUtils.hasText(num)) return Integer.parseInt(num);
        } catch (Exception e) {}
        return null;
    }

    private Double parseAbv(String raw) {
        if (!StringUtils.hasText(raw)) return null;
        try {
            Matcher m = Pattern.compile("(\\d+(\\.\\d+)?)\\s?%?").matcher(raw);
            if (m.find()) return Double.parseDouble(m.group(1));
        } catch (Exception e) {}
        return null;
    }
}