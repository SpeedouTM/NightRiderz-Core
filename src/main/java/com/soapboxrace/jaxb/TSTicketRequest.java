package com.soapboxrace.jaxb;

import java.util.ArrayList;
import java.util.List;

public class TSTicketRequest {
    private List<Long> personas = new ArrayList<>();

    public List<Long> getPersonas() {
        return personas;
    }

    public void setPersonas(List<Long> personas) {
        this.personas = personas;
    }

    public void addPersona(Long persona) {
        this.personas.add(persona);
    }
}
