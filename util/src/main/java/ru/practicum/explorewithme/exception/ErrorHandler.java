package ru.practicum.explorewithme.exception;

import lombok.Getter;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Objects;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND,
                "The required object was not found.",
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        String field = Objects.requireNonNull(e.getFieldError()).getField();
        Object value = e.getFieldValue(field);
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                field,
                e.getFieldError().getDefaultMessage(),
                value
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        return new ErrorResponse(
                HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                e.getMessage()
        );
    }

    @ExceptionHandler({javax.validation.ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class,
            IllegalArgumentException.class,
            MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectInputCausedExceptions(final Exception e) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse(
                HttpStatus.CONFLICT,
                "For the requested operation the conditions are not met.",
                e.getMessage()
        );
    }

    @Getter
    private static class ErrorResponse {
        private final String status;
        private final String reason;
        private final String message;
        private final LocalDateTime timestamp;

        public ErrorResponse(HttpStatus status, String reason, String message) {
            this.status = status.name();
            this.reason = reason;
            this.message = message;
            this.timestamp = LocalDateTime.now();
        }

        public ErrorResponse(HttpStatus status, String reason, String field,
                             String error, Object value) {
            this.status = status.name();
            this.reason = reason;
            this.message = String.format("Field: %s. Error: %s. Value: %s", field, error, value);
            this.timestamp = LocalDateTime.now();
        }
    }
}
