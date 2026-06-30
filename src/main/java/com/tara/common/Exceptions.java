package com.tara.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class Exceptions {

    @Getter
    public static class TaraException extends RuntimeException {
        private final String errorCode;
        private final HttpStatus httpStatus;
        public TaraException(String message, String errorCode, HttpStatus httpStatus) {
            super(message);
            this.errorCode = errorCode;
            this.httpStatus = httpStatus;
        }
        public static TaraException badRequest(String message) {
            return new TaraException(message, "BAD_REQUEST", HttpStatus.BAD_REQUEST);
        }
    }

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String resource, String id) {
            super(resource + " not found: " + id);
        }
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) { super(message); }
    }

    @Getter
    public static class ExternalApiException extends RuntimeException {
        private final String apiName;
        public ExternalApiException(String apiName, String message) {
            super(message);
            this.apiName = apiName;
        }
        public ExternalApiException(String apiName, String message, Throwable cause) {
            super(message, cause);
            this.apiName = apiName;
        }
    }
}
