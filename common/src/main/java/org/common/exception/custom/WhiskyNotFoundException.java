package org.common.exception.custom;

import jakarta.annotation.Nullable;
import org.common.exception.ExceptionBase;
import org.springframework.http.HttpStatus;

public class WhiskyNotFoundException extends ExceptionBase {

    public WhiskyNotFoundException(@Nullable String message){
        this.errorMessage = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
