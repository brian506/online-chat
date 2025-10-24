package org.auth.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.auth.domain.entity.LoginStatus;
import org.auth.domain.entity.User;
import org.auth.domain.repository.UserRepository;
import org.auth.domain.repository.redis.TokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    // PENDING -> ACTIVE 로 변경 배치 활성화
    public void activateUsersBatch(List<UUID> userIds){
        if(userIds.isEmpty() || userIds == null){
            return;
        }
        try{
            long updatedCount = userRepository.activatePendingUsers(
                    userIds,
                    LoginStatus.ACTIVE,
                    LoginStatus.PENDING
            );
            log.info("배치 활성화 완료. {}명의 사용자 상태를 ACTIVE 로 업데이트",updatedCount);
            if(updatedCount != userIds.size()){
                log.warn("배치 활성화 개수 불일치: {}개의 ID를 받았지만 {}명의 사용자만 업데이트",userIds.size(),updatedCount);
            }
        } catch (Exception e){
            log.warn("배치 실패");
        }
    }

    // 유효시간이 지난 사용자 삭제
    public void deleteExpiredUsers(){
        List<User> expired = userRepository.findExpiredPendingUsers(LocalDateTime.now());
        if(!expired.isEmpty()){
            log.info("{}명의 유효시간이 지난 사용자 삭제",expired.size());
            userRepository.deleteAll(expired);
        }
    }

    // 로그아웃
    public void logout(final String userId){
        tokenRepository.deleteByUserId(userId);
    }

}
