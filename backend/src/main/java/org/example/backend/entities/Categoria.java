package org.example.backend.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Categoria {
    CPU("CPU"),
    RAM("RAM"),
    SCHEDA_VIDEO("Schede video"),
    SCHEDA_MADRE("Schede madri"),
    ARCHIVIAZIONE("Archiviazione"),
    ALIMENTATORE("Alimentatori"),
    CASE("Case");

    private final String etichetta;

    Categoria(String etichetta) {
        this.etichetta = etichetta;
    }

    @JsonValue
    public String getEtichetta() {
        return etichetta;
    }

    @JsonCreator
    public static Categoria daTesto(String testo) {
        if (testo == null) {
            return null;
        }
        for (Categoria categoria : values()) {
            if (categoria.etichetta.equalsIgnoreCase(testo) || categoria.name().equalsIgnoreCase(testo)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Categoria non riconosciuta: " + testo);
    }
}
