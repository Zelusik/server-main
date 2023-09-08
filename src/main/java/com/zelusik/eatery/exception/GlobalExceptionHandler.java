package com.zelusik.eatery.exception;

import com.zelusik.eatery.constant.exception.ValidationErrorCode;
import com.zelusik.eatery.dto.exception.ErrorResponse;
import com.zelusik.eatery.dto.exception.ValidationErrorDetails;
import com.zelusik.eatery.dto.exception.ValidationErrorResponse;
import com.zelusik.eatery.exception.kakao.KakaoServerException;
import com.zelusik.eatery.exception.util.ViolationMessageResolver;
import com.zelusik.eatery.log.LogUtils;
import io.micrometer.core.lang.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * <p>{@code @ExceptionHandler} method를 통해 Application에서 발생하는 모든 exception들을 처리하는 class.
 *
 * <p>이 class는 Spring MVC exception을 처리하는 {@link ResponseEntityExceptionHandler}을 상속받아 구현되었음.
 * 이 때문에 Spring MVC에서 발생할 수 있는 기본 exception들을 전부 처리하며,
 * 일부 exception의 경우 응답 형태를 재가공하여 client에게 응답한다.
 *
 * @see ResponseEntityExceptionHandler
 * @see org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(KakaoServerException.class)
    public ResponseEntity<ErrorResponse> handleKakaoServerException(KakaoServerException ex) {
        log.error("[{}] Kakao Server Exception: {}", LogUtils.getLogTraceId(), ExceptionUtils.getExceptionStackTrace(ex));

        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        log.error("[{}] Custom Exception: {}:", LogUtils.getLogTraceId(), ExceptionUtils.getExceptionStackTrace(ex));

        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        log.error("[{}] Validation Exception: {}", LogUtils.getLogTraceId(), ExceptionUtils.getExceptionStackTrace(ex));

        List<ValidationErrorDetails> errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ValidationErrorDetails(
                        ValidationErrorCode.getErrorCode(fieldError.getCode()),
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                )).toList();

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ValidationErrorResponse(
                        ExceptionType.METHOD_ARGUMENT_NOT_VALID.getCode(),
                        ExceptionType.METHOD_ARGUMENT_NOT_VALID.getMessage(),
                        errorDetails
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("[{}] Validation Exception: {}", LogUtils.getLogTraceId(), ExceptionUtils.getExceptionStackTrace(ex));

        List<ValidationErrorDetails> errorDetails = ex.getConstraintViolations().stream()
                .map(violation -> {
                    ViolationMessageResolver resolver = new ViolationMessageResolver(violation);
                    return new ValidationErrorDetails(resolver.getErrorCode(), resolver.getFieldName(), resolver.getMessage());
                }).toList();

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ValidationErrorResponse(
                        ExceptionType.CONSTRAINT_VIOLATION.getCode(),
                        ExceptionType.CONSTRAINT_VIOLATION.getMessage(),
                        errorDetails)
                );
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(@NonNull Exception ex, @Nullable Object body, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        log.error("[{}] Spring MVC Basic Exception: {}", LogUtils.getLogTraceId(), ExceptionUtils.getExceptionStackTrace(ex));

        ExceptionType exceptionType = ExceptionType.from(ex.getClass()).orElse(ExceptionType.UNHANDLED);
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(
                        exceptionType.getCode(),
                        exceptionType.getMessage() + " " + ex.getMessage())
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("[{}] UnHandled Exception: {}", LogUtils.getLogTraceId(), ExceptionUtils.getExceptionStackTrace(ex));

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        ExceptionType.UNHANDLED.getCode(),
                        ExceptionType.UNHANDLED.getMessage() + " " + ex.getMessage()
                ));
    }
}