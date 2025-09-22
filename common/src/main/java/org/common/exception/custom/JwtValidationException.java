package org.common.exception.custom;

import lombok.Getter;
import org.common.exception.ExceptionBase;
import org.springframework.http.HttpStatus;

@Getter
public class JwtValidationException extends ExceptionBase {

    public JwtValidationException(final String message){
        this.errorMessage = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
