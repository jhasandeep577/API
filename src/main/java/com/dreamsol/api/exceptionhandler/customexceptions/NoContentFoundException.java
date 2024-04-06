package com.dreamsol.api.exceptionhandler.customexceptions;

public class NoContentFoundException extends RuntimeException {
    @Override
    public String getMessage() {
        return "No Content Found";
    }
}
