package com.soapboxrace.jaxb;

public class JSONError {
    private String error;

    public JSONError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
