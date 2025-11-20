package org.common.exception.custom;

import jakarta.annotation.Nullable;
import org.common.exception.ExceptionBase;
import org.springframework.http.HttpStatus;

public class S3UploadException extends ExceptionBase {

    public S3UploadException(@Nullable String message){
        this.errorMessage = message;
    }
    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
