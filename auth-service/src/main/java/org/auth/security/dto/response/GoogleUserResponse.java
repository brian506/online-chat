package org.auth.security.dto.response;

public record GoogleUserResponse( String id,
                                  String email,
                                  String name,
                                  String picture) {
}
