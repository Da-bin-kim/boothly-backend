package com.boothly.backend.common.exception;

import com.boothly.backend.common.exception.salepost.InvalidSalePostStateException;
import com.boothly.backend.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn(
                "비즈니스 예외 발생: code={}, message={}",
                errorCode.getCode(),
                e.getMessage()
        );
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode));
    }
    @ExceptionHandler(InvalidSalePostStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidSalePostState(
            InvalidSalePostStateException e
    ) {
        log.error("판매글 데이터 정합성 오류", e);
        ErrorCode errorCode = ErrorCode.SALE_POST_INVALID_STATE;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(
            IllegalArgumentException e
    ) {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode, e.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e
    ){
        ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;
        String message = errorCode.getMessage();

        if (!e.getBindingResult().getFieldErrors().isEmpty()) {
            FieldError fieldError = e.getBindingResult().getFieldErrors().get(0);
            message = fieldError.getField() + ": " + fieldError.getDefaultMessage();
        }

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode, message));
    }
}
