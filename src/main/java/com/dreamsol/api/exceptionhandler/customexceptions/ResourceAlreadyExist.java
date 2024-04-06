package com.dreamsol.api.exceptionhandler.customexceptions;

public class ResourceAlreadyExist extends RuntimeException {
    private String data;
    private String dataName;

    public ResourceAlreadyExist(String data, String DataName) {
        this.data = data;
        this.dataName = DataName;
    }

    public ResourceAlreadyExist(long data, String DataName) {
        this.data = Long.toString(data);
        this.dataName = DataName;
    }

    public String getData() {
        return this.data;
    }

    public String getDataName() {
        return dataName;
    }
}
