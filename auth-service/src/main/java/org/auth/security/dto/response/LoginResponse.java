package org.auth.security.dto.response;



import org.auth.domain.entity.Role;

public record LoginResponse(Role role, String accessToken,String refreshToken) { }
