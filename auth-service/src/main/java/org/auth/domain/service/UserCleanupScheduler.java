package org.auth.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCleanupScheduler {

    private final UserService userService;

    @Scheduled(cron = "0 0 23 * * *")
    public void runCleanJob(){
        userService.deleteExpiredUsers();
        log.info("PENDING 상태의 사용자 삭제 완료");
    }
}
