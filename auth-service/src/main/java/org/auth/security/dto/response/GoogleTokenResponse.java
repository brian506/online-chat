package org.auth.security.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


public record GoogleTokenResponse(@JsonProperty("token_type") String tokenType,
                                  @JsonProperty("access_token") String accessToken,
                                  @JsonProperty("scope") String scope,
                                  @JsonProperty("id_token") String idToken,
                                  @JsonProperty("expires_in") Integer expiresIn
                                  ) {
}
