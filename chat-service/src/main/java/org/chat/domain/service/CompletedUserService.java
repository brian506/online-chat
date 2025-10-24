package org.chat.domain.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CompletedUserService {

        private final Set<UUID> completedUserIds = ConcurrentHashMap.newKeySet();
        private final RestClient restClient;
        private final String AUTH_ENDPOINT = "/users/batch-status";

        @Value("${auth-service.url}")
        private String authURL;

        // 사용자 정보입력 완료시 set 에 사용자 추가
        public void addUserAsCompleted(String userId){
            completedUserIds.add(UUID.fromString(userId));
        }

        // 하루마다 auth 서비스로 전달
        @Scheduled(fixedDelay = 6000) //60000 * 60 * 24
        public void sendBatchUpdateToAuthService(){
            if(completedUserIds.isEmpty()){
                return;
            }
            // 새 추가 누락을 피하기 위해 빠르게 복사하고 원본 Set 비우기
            Set<UUID> batchToSend = new HashSet<>(completedUserIds);
            completedUserIds.removeAll(batchToSend);

            try{
                restClient.patch()
                        .uri(authURL+AUTH_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(batchToSend)
                        .retrieve()
                        .toBodilessEntity();
            }catch (Exception e){
                completedUserIds.addAll(batchToSend);
            }


        }
}
