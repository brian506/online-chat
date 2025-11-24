package org.whisky.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
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

    // 리스트 URL (블렌디드 몰트 예시)
    private static final String BASE_LIST_URL = "https://www.masterofmalt.com/country-style/scotch/blended-malt-whisky/";

    public void runCrawler() {
        log.info("🔧 실행 중인 크롬(9222 포트)에 연결 시도 중...");

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        WebDriver driver = new ChromeDriver(options);

        // 타임아웃 설정
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(10));

        int currentCount = 0;
        int pageNum = 1; // 2페이지까지 했으면 여기를 3으로 바꿔서 시작해도 됨
        Random random = new Random();
        Set<String> visitedUrlsInPage = new HashSet<>();

        try {
            log.info("🚀 [God Mode] 위스키 크롤링 시작 (보안 감지 기능 탑재)...");

            while (currentCount < TARGET_COUNT) {
                String currentListUrl = (pageNum == 1) ? BASE_LIST_URL : BASE_LIST_URL + pageNum + "/";
                log.info(">>> [페이지 이동] {} 페이지: {}", pageNum, currentListUrl);

                try {
                    driver.get(currentListUrl);
                } catch (TimeoutException e) {
                    log.warn("⏳ 로딩 타임아웃 -> 멈추고 계속 진행");
                    ((JavascriptExecutor) driver).executeScript("window.stop();");
                } catch (Exception e) {
                    log.warn("⚠️ 페이지 이동 오류: {}", e.getMessage());
                }

                // [보안 체크] 리스트 페이지 진입 시 차단됐는지 확인
                checkAndSolveSecurity(driver);

                scrollDown(driver);

                Document listDoc = Jsoup.parse(driver.getPageSource());
                Elements productLinks = listDoc.select("a[href*='/whiskies/']");

                if (productLinks.isEmpty()) {
                    log.warn("⚠️ {}페이지 상품 로딩 실패. (보안 체크 혹은 끝)", pageNum);
                    // 한 번 더 기회를 줌 (혹시 로딩이 덜 됐을까봐)
                    Thread.sleep(3000);
                    listDoc = Jsoup.parse(driver.getPageSource());
                    productLinks = listDoc.select("a[href*='/whiskies/']");

                    if (productLinks.isEmpty()) {
                        log.error("❌ 진짜 데이터 없음. 크롤링 종료.");
                        break;
                    }
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
                    // 밴 당하지 않게 딜레이를 좀 더 줌
                    Thread.sleep(1000 + random.nextInt(500));
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

    /**
     * [신규] 보안 페이지(Vercel, Cloudflare) 감지 및 대기 로직
     */
    private void checkAndSolveSecurity(WebDriver driver) throws InterruptedException {
        String title = driver.getTitle();
        String source = driver.getPageSource();

        // 차단 페이지의 특징적인 키워드들
        if (title.contains("Security Checkpoint") || title.contains("Just a moment") ||
                title.contains("Vercel") || source.contains("Verify you are human")) {

            log.error("👮🚨 [보안 차단 감지] Vercel/Cloudflare가 떴습니다! 브라우저를 확인하세요!");
            log.error("👉 직접 브라우저에서 '사람입니다' 체크박스를 클릭하거나 문제를 푸세요.");
            log.error("⏳ 45초 동안 대기합니다... (해결되면 자동으로 넘어갑니다)");

            // 사용자가 풀 시간을 줌 (45초)
            for (int i = 0; i < 9; i++) {
                Thread.sleep(5000);
                log.info("... 대기 중 ({}초 경과) ...", (i + 1) * 5);

                // 중간에 풀렸는지 체크
                if (!driver.getTitle().contains("Security") && !driver.getTitle().contains("Vercel")) {
                    log.info("✅ 보안 해제 감지됨! 크롤링 재개합니다.");
                    return;
                }
            }
            log.warn("⚠️ 대기 시간 초과. 다음 로직으로 강제 진행합니다.");
        }
    }

    private void scrollDown(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            for (int i = 0; i < 3; i++) {
                try {
                    js.executeScript("window.scrollBy(0, 800)");
                } catch (Exception e) {}
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Transactional
    protected boolean crawlAndSaveDetail(WebDriver driver, String url, int index) {
        try {
            try {
                driver.get(url);
            } catch (TimeoutException e) {
                ((JavascriptExecutor) driver).executeScript("window.stop();");
            }

            // [보안 체크] 상세 페이지 진입 시에도 차단 확인
            checkAndSolveSecurity(driver);

            Thread.sleep(800);

            Document doc = Jsoup.parse(driver.getPageSource());
            ObjectMapper mapper = new ObjectMapper();

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

            if ("Unknown".equals(name)) {
                name = doc.title().replace("| Master of Malt", "").trim();
            }

            // [추가] 보안 페이지 타이틀이 이름으로 들어오는 것 방지
            if (name.contains("Security Checkpoint") || name.contains("Just a moment") || name.contains("Vercel")) {
                log.warn("⛔ 이름이 보안 페이지 타이틀입니다. 저장을 건너뜁니다: {}", url);
                return false;
            }

            if (!StringUtils.hasText(name) || name.length() < 2) return false;

            if (whiskyRepository.existsByName(name)) {
                log.info("PASS (중복): {}", name);
                return false;
            }

            // ... (이하 스펙 추출 로직은 완벽하므로 그대로 유지) ...
            Map<String, String> breadcrumbInfo = analyzeBreadcrumbs(doc);
            String country = breadcrumbInfo.getOrDefault("country", "");
            String type = breadcrumbInfo.getOrDefault("type", "");

            if (!StringUtils.hasText(country)) country = extractValueFromDom(doc, "Country", "Origin", "Region");
            if (!StringUtils.hasText(type)) type = extractValueFromDom(doc, "Style", "Category", "Whisky Style");

            String rawBottler = extractValueFromDom(doc, "Bottler", "Brand", "Distillery");
            String bottler = refineBottler(rawBottler, name);

            Double abv = extractAbvRobust(doc);
            Integer age = extractAgeRobust(name, doc);

            country = normalizeCountry(country);
            if (!StringUtils.hasText(country)) country = "Unknown";
            if (!StringUtils.hasText(type)) type = "Whisky";

            String nose = extractTastingNote(doc, "Nose");
            String palate = extractTastingNote(doc, "Palate");
            String finish = extractTastingNote(doc, "Finish");

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
            log.info("[저장] #{} {} (Age:{}, ABV:{}%)", index, name, age, abv);

            return true;

        } catch (Exception e) {
            log.error("상세 페이지 처리 에러: {}", e.getMessage());
            return false;
        }
    }

    // ... (Helper Methods: extractAbvRobust, extractAgeRobust 등등은 기존 코드 그대로 두세요) ...
    // 아래 메서드들이 없으면 컴파일 에러 나니 꼭 이전에 드린 코드의 Helper Method 부분을 유지하세요!

    private Double extractAbvRobust(Document doc) {
        String val = extractValueFromDom(doc, "Alcohol", "ABV", "Volume");
        Double parsed = parseAbv(val);
        if (parsed != null) return parsed;
        Elements containers = doc.select(".product-details, .product-box-wide, #ContentPlaceHolder1_ctl00_ctl00_wdDetails_lblDetails");
        String text = containers.hasText() ? containers.text() : doc.body().text();
        Pattern p = Pattern.compile("(\\d{1,2}(\\.\\d{1,2})?)\\s?%");
        Matcher m = p.matcher(text);
        while (m.find()) {
            try {
                double found = Double.parseDouble(m.group(1));
                if (found > 30 && found < 80) return found;
            } catch (Exception e) {}
        }
        return null;
    }

    private Integer extractAgeRobust(String name, Document doc) {
        Pattern titlePattern = Pattern.compile("(\\d{1,2})\\s?(Year|yo|Year Old|Y.O)", Pattern.CASE_INSENSITIVE);
        Matcher m = titlePattern.matcher(name);
        if (m.find()) return Integer.parseInt(m.group(1));
        String ageStr = extractValueFromDom(doc, "Age");
        return parseAge(ageStr);
    }

    private String refineBottler(String extracted, String name) {
        String clean = (extracted == null) ? "" : extracted.trim();
        Set<String> stopWords = Set.of("The", "A", "Whisky", "Whiskey", "Blended", "Single", "Living", "Scope");
        if (clean.isEmpty() || stopWords.contains(clean) || clean.length() < 2) {
            String[] parts = name.split(" ");
            if (parts.length > 0) {
                if (parts[0].equalsIgnoreCase("The") && parts.length > 1) return parts[1];
                return parts[0];
            }
        }
        return clean;
    }

    private String extractValueFromDom(Document doc, String... keywords) {
        for (String key : keywords) {
            Elements labels = doc.select("th:contains(" + key + "), strong:contains(" + key + "), b:contains(" + key + "), span:contains(" + key + ")");
            for (Element label : labels) {
                if (label.text().length() > key.length() + 8) continue;
                Element next = label.nextElementSibling();
                if (next != null && StringUtils.hasText(next.text())) return next.text().trim();
                if (label.parent() != null) {
                    Element parentNext = label.parent().nextElementSibling();
                    if (parentNext != null && StringUtils.hasText(parentNext.text())) {
                        String val = parentNext.text().trim();
                        if (val.length() < 50) return val;
                    }
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
            if (el.text().trim().startsWith(keyword)) return cleanNoteText(el.text(), keyword);
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