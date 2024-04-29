package com.example.sportsreservationsystembackend.rest.api.controlleradvice;

import com.example.sportsreservationsystembackend.exceptions.AccountDisabledException;
import com.example.sportsreservationsystembackend.exceptions.ApiCallException;
import com.example.sportsreservationsystembackend.exceptions.DuplicateReservationException;
import com.example.sportsreservationsystembackend.exceptions.EmailTakenException;
import com.example.sportsreservationsystembackend.exceptions.EventFullException;
import com.example.sportsreservationsystembackend.exceptions.FutureEventsException;
import com.example.sportsreservationsystembackend.exceptions.InsufficientFundsException;
import com.example.sportsreservationsystembackend.exceptions.InvalidTokenException;
import com.example.sportsreservationsystembackend.exceptions.LastAdminException;
import com.example.sportsreservationsystembackend.exceptions.OverlappingEventsException;
import com.example.sportsreservationsystembackend.exceptions.PasswordMissmatchException;
import com.example.sportsreservationsystembackend.exceptions.PastEventException;
import com.example.sportsreservationsystembackend.exceptions.ResourceNotFoundException;
import com.example.sportsreservationsystembackend.exceptions.ResourceNotOwnedException;
import com.example.sportsreservationsystembackend.exceptions.InvalidRefreshTokenException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * This class represents global exception handler for the whole application
 * It handles all exceptions thrown by the application and returns appropriate response
 * to the client
 *
 * @author Radim Stejskal
 */

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNoSuchElementFoundException(ResourceNotFoundException itemNotFoundException, WebRequest request) {
        log.error("Failed to find the requested element", itemNotFoundException);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(itemNotFoundException.getMessage());
    }

    @ExceptionHandler(EmailTakenException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleEmailTakenException(EmailTakenException emailTakenException, WebRequest request) {
        log.error("Failed to create user", emailTakenException);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(emailTakenException.getMessage());
    }

    @ExceptionHandler(ResourceNotOwnedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleResourceNotOwnedException(ResourceNotOwnedException resourceNotOwnedException, WebRequest request) {
        log.error("Failed to find the requested element", resourceNotOwnedException);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(resourceNotOwnedException.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDuplicateKeyException(DuplicateKeyException duplicateKeyException, WebRequest request) {
        log.error("Duplicate key error occurred", duplicateKeyException);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(duplicateKeyException.getMessage());
    }

    @ExceptionHandler(AccountDisabledException.class)
    @ResponseStatus(HttpStatus.LOCKED)
    public ResponseEntity<Object> handleAccountDisabledException(AccountDisabledException accountDisabledException, WebRequest request) {
        log.error("Account locked error occurred", accountDisabledException);
        return ResponseEntity.status(HttpStatus.LOCKED).body(accountDisabledException.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onConstraintValidationException(
            ConstraintViolationException e) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            error.getViolations().add(
                    new Violation(violation.getPropertyPath().toString(), violation.getMessage()));
        }
        return error;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            error.getViolations().add(
                    new Violation(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ApiCallException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleApiCallException(ApiCallException apiCallException, WebRequest request) {
        log.error("Unknown error occurred", apiCallException);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiCallException.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException illegalArgumentException, WebRequest request) {
        log.error("Illegal argument error occurred", illegalArgumentException);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(illegalArgumentException.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(Exception exception, WebRequest request) {
        log.error("Unknown error occurred", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }

    @ExceptionHandler(OverlappingEventsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleOverlappingEventsException(Exception exception) {
        log.error("Overlapping events", exception);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleUsernameNotFoundException(Exception exception) {
        log.error("Username not found", exception);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user credentials");
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleBadCredentialsException(Exception exception) {
        log.error("Bad credentials", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user credentials");
    }

    @ExceptionHandler(DisabledException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleAccountDisabledException(Exception exception) {
        log.error("Not verified", exception);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account has not been verified");
    }

    @ExceptionHandler(LockedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleAccountLockedException(Exception exception) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.WWW_AUTHENTICATE, "Account must be verified via link sent to email");
        log.error("Account locked", exception);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(headers).body("Account locked");
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleInvalidRegistrationTokenException(Exception exception) {
        log.error("Invalid registration token", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(DuplicateReservationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDuplicateReservationException(Exception exception) {
        log.error("Duplicate reservation", exception);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleInsufficientFundsException(Exception exception) {
        log.error("Insufficient funds", exception);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception.getMessage());
    }

    @ExceptionHandler(EventFullException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleEventFullException(Exception exception) {
        log.error("Event full", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(PastEventException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handlePastEventException(Exception exception) {
        log.error("Past event", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(LastAdminException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleLastAdminException(Exception exception) {
        log.error("Last admin", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleUsedRefreshTokenException(Exception exception) {
        log.error("Used refresh token", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(FutureEventsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleFutureEventsException(Exception exception) {
        log.error("Future events", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(PasswordMissmatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handlePasswordMissmatchException(Exception exception) {
        log.error("Password missmatch", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}

