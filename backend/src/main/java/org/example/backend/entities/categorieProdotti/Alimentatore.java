package org.example.backend.entities.categorieProdotti;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.persistence.*;
import org.example.backend.entities.Prodotto;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "ALIMENTATORE")
public class Alimentatore extends Prodotto {
    @Column(name = "potenza_watt", nullable = true)
    private int potenzaWatt;

    @Column(name = "certificazione", nullable = true)
    private String certificazione;

    @Column(name = "modulare", nullable = true)
    private boolean modulare;

    @Column(name = "formato", nullable = true)
    private String formato;
}
