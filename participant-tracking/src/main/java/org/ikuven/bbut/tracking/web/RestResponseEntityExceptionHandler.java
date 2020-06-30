package org.ikuven.bbut.tracking.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
 
    @ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;
        return handleExceptionInternal(ex, ErrorDto.of(status.value(), ex.getMessage()), new HttpHeaders(), status, request);
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    private static class ErrorDto {
        private int status;
        private String message;
    }
}
