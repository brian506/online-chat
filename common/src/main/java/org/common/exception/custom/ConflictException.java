package org.common.exception.custom;

import jakarta.annotation.Nullable;
import org.common.exception.ExceptionBase;
import org.springframework.http.HttpStatus;

public class ConflictException extends ExceptionBase {

    public ConflictException(@Nullable String message){
        this.errorMessage = message;
    }
    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}
