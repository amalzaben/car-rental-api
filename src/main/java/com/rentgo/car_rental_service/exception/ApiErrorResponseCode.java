package com.rentgo.car_rental_service.exception;

public enum ApiErrorResponseCode {
    INTERNAL_SERVER_ERROR,
    RESOURCE_NOT_FOUND,
    CONFLICT,
    FORBIDDEN,
    BAD_REQUEST,
    INVALID_PAGINATION,
    UNAUTHORIZED, UNAUTHENTICATED, VALIDATION_ERROR
}
