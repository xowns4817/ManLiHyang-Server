package com.project.manlihyang;


import com.project.manlihyang.common.ErrorResponse;
import com.project.manlihyang.user.exception.NoEmailException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestController
@RestControllerAdvice
public class RestExceptionHandler
{
    /*
    @ExceptionHandler({AvatarExtractServerErrorException.class, RequestFullException.class, ZKException.class})
    protected ResponseEntity<ErrorResponse> handleServerException(Exception re) {
        ErrorResponse errorResponse = getErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, re.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }*/

    @ExceptionHandler(value = NoEmailException.class)
    protected ResponseEntity<ErrorResponse> handleServerException(Exception re) {
        ErrorResponse errorResponse = getErrorResponse(HttpStatus.BAD_REQUEST, re.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(errorResponse);
    }

    private ErrorResponse getErrorResponse(HttpStatus httpStatus, String message) {
        return ErrorResponse.builder()
                .timeStamp(makeTimeStamp())
                .statusCode(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(message)
                .build();
    }

    private long makeTimeStamp() {
        return new Date().getTime();
    }
}