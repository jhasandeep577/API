package com.dreamsol.api.exceptionhandler.customexceptions;

public class ResourceNotFoundException extends RuntimeException {
    private int id;

    public ResourceNotFoundException(int id) {
        this.id = id;
    }

    public ResourceNotFoundException() {
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String getMessage() {
        return (id != 0) ? "Resource Not Found with ID : " + id : "Resource Not Found";
    }

}
