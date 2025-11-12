package org.common.exception.custom;

import jakarta.annotation.Nullable;
import org.common.exception.ExceptionBase;
import org.springframework.http.HttpStatus;

public class AuthenticationException extends ExceptionBase {

    public AuthenticationException(@Nullable String messages){
        this.errorMessage = messages;
    }
    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
