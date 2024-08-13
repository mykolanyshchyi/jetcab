package com.jetcab.common.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;
import static org.apache.commons.text.WordUtils.capitalize;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
@RequiredArgsConstructor
public class RestControllerExceptionHandler {

    private final MessageSourceAccessor messageSource;

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNotFoundException(Exception ex) {
        return createErrorResponse(NOT_FOUND, ex);
    }

    @ExceptionHandler({ForbiddenException.class})
    @ResponseStatus(FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleForbiddenException(Exception ex) {
        return createErrorResponse(FORBIDDEN, ex);
    }

    @ExceptionHandler({ConflictException.class})
    @ResponseStatus(CONFLICT)
    public ResponseEntity<ErrorResponse> conflictException(Exception ex) {
        return createErrorResponse(CONFLICT, ex);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handle(BindException ex) {
        return createErrorResponse(ex);
    }

    protected ResponseEntity<ErrorResponse> createErrorResponse(BindException exception) {
        List<String> messages = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::getValidationErrorMessage)
                .toList();

        return ResponseEntity.status(BAD_REQUEST).body(new ErrorResponse(messages));
    }

    private String getValidationErrorMessage(FieldError error) {
        String field = error.getField();
        if (error.getDefaultMessage() != null) {
            String resolvedMessage = messageSource.getMessage(error.getDefaultMessage(), error.getDefaultMessage());
            return resolvedMessage.replaceAll("\\{field\\}", capitalize(join(splitByCharacterTypeCamelCase(field.substring(field.lastIndexOf('.') + 1)), " ")));
        }
        return null;
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(HttpStatus httpStatus, Exception exception) {
        String message = getMessage(exception);
        ErrorResponse errorResponse = new ErrorResponse(message);
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    private String getMessage(Exception exception) {
        String messageKey = exception.getMessage();
        return messageSource.getMessage(messageKey, messageKey);
    }
}

