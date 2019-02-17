package com.soapboxrace.jaxb;

public class ModernRegisterResponse {
    private String message;

    public ModernRegisterResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
