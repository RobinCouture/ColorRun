package fr.esgi.robin.colorrun.business;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "PAGESSTATIQUES")
public class Pagesstatique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PAGE", nullable = false)
    private Integer id;

    @Column(name = "TITRE_PAGE", nullable = false, length = 100)
    private String titrePage;

    @Lob
    @Column(name = "CONTENU", nullable = false)
    private String contenu;

    @Column(name = "DATE_DERNIERE_MODIF")
    private Instant dateDerniereModif;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitrePage() {
        return titrePage;
    }

    public void setTitrePage(String titrePage) {
        this.titrePage = titrePage;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Instant getDateDerniereModif() {
        return dateDerniereModif;
    }

    public void setDateDerniereModif(Instant dateDerniereModif) {
        this.dateDerniereModif = dateDerniereModif;
    }

}