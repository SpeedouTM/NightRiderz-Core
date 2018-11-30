package com.soapboxrace.jaxb;

public class TSRevokeRequest {
    private String ticket;

    public TSRevokeRequest(String ticket) {
        this.ticket = ticket;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
