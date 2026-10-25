package org.example.backend.dto.output;

import lombok.Getter;
import lombok.Setter;
import org.example.backend.entities.Recensione;
import org.example.backend.entities.Utente;

import java.util.Date;

@Getter
@Setter
public class RecensioneDTO {

    private int id;
    private int idAutore;
    private String autore;
    private int valutazione;
    private String commento;
    private Date dataRecensione;

    public RecensioneDTO(Recensione recensione) {
        this.id = recensione.getId();
        this.idAutore = recensione.getAutore() != null ? recensione.getAutore().getId() : 0;
        this.autore = nomeAbbreviato(recensione.getAutore());
        this.valutazione = recensione.getValutazione();
        this.commento = recensione.getCommento();
        this.dataRecensione = recensione.getDataRecensione();
    }

    private static String nomeAbbreviato(Utente autore) {
        if (autore == null) {
            return "Anonimo";
        }
        String nome = autore.getNome() != null ? autore.getNome() : "";
        String cognome = autore.getCognome();
        if (cognome != null && !cognome.isBlank()) {
            return (nome + " " + cognome.charAt(0) + ".").trim();
        }
        return nome.isBlank() ? "Anonimo" : nome;
    }
}
