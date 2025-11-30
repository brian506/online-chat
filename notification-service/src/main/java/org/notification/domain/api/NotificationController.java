package org.notification.domain.api;

import lombok.RequiredArgsConstructor;
import org.common.utils.SuccessMessages;
import org.common.utils.SuccessResponse;
import org.notification.domain.service.NotificationService;
import org.notification.dto.response.NotificationResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 1. 알림 목록 조회 (커서 페이징)
    @GetMapping
    public ResponseEntity<?> getNotifications(
            @RequestParam(required = false) String cursorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorCreatedAt,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Slice<NotificationResponse> responses = notificationService.getMyNotifications(cursorId, cursorCreatedAt, pageable);
        SuccessResponse response = new SuccessResponse(true, SuccessMessages.NOTIFICATION_RETRIEVE_SUCCESS,responses);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 2. 알림 읽음 처리
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<?> getNotification(@PathVariable String notificationId
    ) {
        notificationService.readNotification(notificationId);
        SuccessResponse response = new SuccessResponse(true, SuccessMessages.NOTIFICATION_RETRIEVE_SUCCESS,null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 3. 알림 삭제
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable String notificationId
    ) {
        notificationService.deleteNotification(notificationId);
        SuccessResponse response = new SuccessResponse(true, SuccessMessages.NOTIFICATION_DELETE_SUCCESS,null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
