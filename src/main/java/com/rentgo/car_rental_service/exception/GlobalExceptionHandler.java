package com.rentgo.car_rental_service.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 400 - CoreException (Bad Request / Business logic)
    @ExceptionHandler(CoreException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(CoreException ex) {
        log.warn("400 Bad Request: {}", ex.getMessage());
        // log to slack
        return buildErrorResponse(
                400,
                ex.getMessage(),
                ApiErrorResponseCode.BAD_REQUEST,
                "The request could not be processed due to invalid business logic."
        );
    }

    // 404 - Resource Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        log.warn("404 - {}", ex.getMessage());

        return buildErrorResponse(
                404,
                ex.getMessage(),
                ApiErrorResponseCode.RESOURCE_NOT_FOUND,
                "The resource you are trying to access does not exist, has been deleted, "
                        + "or is not available within the context of your request. "
                        + "Please verify the resource ID or parameters you have provided."
        );
    }

    // 409 - Conflict
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(ConflictException ex) {
        log.warn("409 Conflict: {}", ex.getMessage());
        return buildErrorResponse(
                409,
                ex.getMessage(),
                ApiErrorResponseCode.CONFLICT,
                "This action could not be completed due to a conflict in the current state."
        );
    }

    // 403 - Forbidden
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiErrorResponse> handleForbidden(ForbiddenException ex) {
        log.warn("403 Forbidden: {}", ex.getMessage());
        return buildErrorResponse(
               403,
                ex.getMessage(),
                ApiErrorResponseCode.FORBIDDEN,
                "You do not have permission to perform this action."
        );
    }

    //400 invalid pagination
    @ExceptionHandler(PaginationException.class)
    public ResponseEntity<ApiErrorResponse> handlePaginationException(PaginationException ex) {
        log.warn("Pagination error: {}", ex.getMessage());

        return buildErrorResponse(
                400,
                ex.getMessage(),
                ApiErrorResponseCode.INVALID_PAGINATION,
                "Please ensure the page number and page size are valid positive integers. " +
                        "Check that the requested page exists within the available range."
        );
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());

        String combinedMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return buildErrorResponse(
                400,
                "Validation failed for one or more fields.",
                ApiErrorResponseCode.VALIDATION_ERROR,
                combinedMessage
        );
    }


    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthenticatedException(UnauthenticatedException ex) {
        log.warn("Unauthenticated access attempt: {}", ex.getMessage());

        return buildErrorResponse(
                401,
                "You must be authenticated to access this resource.",
                ApiErrorResponseCode.UNAUTHENTICATED,
                ex.getMessage()
        );
    }



    // 500 - General Unhandled
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnhandled(Exception ex) {
        log.error("500 Internal Server Error: ", ex);
        return buildErrorResponse(
                500,
                "An unexpected error occurred",
                ApiErrorResponseCode.INTERNAL_SERVER_ERROR,
                "Please contact support if the problem persists."
        );
    }


    // response builder
    private ResponseEntity<ApiErrorResponse> buildErrorResponse(
            int status,
            String message,
            ApiErrorResponseCode code,
            String description
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                status,
                message,
                code,
                description,
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(response);
    }

}
