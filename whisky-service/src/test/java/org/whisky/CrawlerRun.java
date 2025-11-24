package org.whisky;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.whisky.domain.service.WhiskyCrawler;

@SpringBootTest(classes = WhiskyApplication.class)
public class CrawlerRun {
    @Autowired
    private WhiskyCrawler crawler;

    @Test
    void manualRun() {
        System.out.println("====== 크롤러 수동 실행 시작 ======");
        crawler.runCrawler();
        System.out.println("====== 크롤러 수동 실행 끝 ======");
    }
}
