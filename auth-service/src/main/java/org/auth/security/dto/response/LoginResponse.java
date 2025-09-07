package org.auth.security.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.auth.domain.entity.Role;
import org.springframework.http.ResponseCookie;


public record LoginResponse(Role role, String accessToken,String refreshToken) { }
