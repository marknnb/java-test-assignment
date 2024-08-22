package com.mendix.test.exception.handler;

import com.mendix.test.exception.*;
import com.mendix.test.model.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

import static com.mendix.test.exception.ExceptionErrorCodes.MNDX_BKP_010;
import static com.mendix.test.exception.ExceptionErrorCodes.MNDX_BKP_011;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@Order(HIGHEST_PRECEDENCE)
@Slf4j
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({CreateBackupException.class, CreateClientException.class, MinIOException.class, GenericException.class})
    public final @NotNull ResponseEntity<ErrorResponse> handleInternalServerException(@NotNull BaseException exception, @NotNull WebRequest request) {
        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .timestamp(new Date())
                        .errorCode(exception.getMessage())
                        .status(INTERNAL_SERVER_ERROR.value())
                        .clientUrl(request.getDescription(false))
                        .errorMessage(ExceptionErrorCodes.valueOf(exception.getMessage()).getMessage())
                        .build();
        return new ResponseEntity<>(errorResponse, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ClientNotFoundException.class, BackupNotFoundException.class})
    public final @NotNull ResponseEntity<ErrorResponse> handleNotFoundException(@NotNull BaseException exception, @NotNull WebRequest request) {
        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .timestamp(new Date())
                        .errorCode(exception.getMessage())
                        .status(NOT_FOUND.value())
                        .clientUrl(request.getDescription(false))
                        .errorMessage(ExceptionErrorCodes.valueOf(exception.getMessage()).getMessage())
                        .build();
        return new ResponseEntity<>(errorResponse, NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public final @NotNull ResponseEntity<ErrorResponse> handleConstraintViolationException(@NotNull final WebRequest request,
                                                                                           @NotNull final ConstraintViolationException exception) {
        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .timestamp(new Date())
                        .errorCode(exception.getMessage())
                        .status(BAD_REQUEST.value())
                        .clientUrl(request.getDescription(false))
                        .build();
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public final @NotNull ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(@NotNull final WebRequest request,
                                                                                                  @NotNull final MethodArgumentTypeMismatchException exception) {
        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .timestamp(new Date())
                        .errorCode(MNDX_BKP_011.toString())
                        .errorMessage(exception.getMessage())
                        .status(BAD_REQUEST.value())
                        .clientUrl(request.getDescription(false))
                        .build();
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NotNull MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, @NotNull WebRequest request) {
        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .timestamp(new Date())
                        .errorCode(MNDX_BKP_010.toString())
                        .errorMessage(exception.getLocalizedMessage())
                        .status(BAD_REQUEST.value())
                        .clientUrl(request.getDescription(false))
                        .build();
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }
}
