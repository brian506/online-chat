package org.notification.domain.dto;

public record FcmSendRequest(String token, String title, String body) {
}
