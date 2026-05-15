package com.hotel.hotel_management.Exception;

public class BusinessValidationException extends IllegalArgumentException {

    public BusinessValidationException(String message) {
        super(message);
    }
}
