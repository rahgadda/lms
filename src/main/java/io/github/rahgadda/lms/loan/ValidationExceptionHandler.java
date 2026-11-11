package io.github.rahgadda.lms.loan;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ValidationExceptionHandler {

    public record FieldErrorResponse(String field, String message) {
    }

    public record ErrorResponse(int status, String error, List<FieldErrorResponse> fieldErrors) {
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldErrorResponse> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new FieldErrorResponse(
                        fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", fieldErrors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex) {
        MismatchedInputException mismatched = findCause(ex, MismatchedInputException.class);
        if (mismatched != null) {
            String field = mismatched.getPath().stream()
                    .map(Reference::getFieldName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("."));
            if (!field.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Validation failed",
                                List.of(new FieldErrorResponse(field, field + " has an invalid value"))));
            }
        }
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(), "Malformed or missing request body", List.of()));
    }

    private static <T extends Throwable> T findCause(Throwable ex, Class<T> type) {
        Throwable cause = ex;
        while (cause != null) {
            if (type.isInstance(cause)) {
                return type.cast(cause);
            }
            cause = cause.getCause();
        }
        return null;
    }
}
