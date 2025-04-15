package fr.esgi.robin.colorrun.business;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "DEMANDESORGANISATEUR")
public class Demandesorganisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DEMANDE", nullable = false)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UTILISATEUR")
    private Utilisateur utilisateur;

    @Lob
    @Column(name = "MOTIVATION", nullable = false)
    private String motivation;

    @Column(name = "DATE_DEMANDE")
    private Instant dateDemande;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public Instant getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(Instant dateDemande) {
        this.dateDemande = dateDemande;
    }

/*
 TODO [Reverse Engineering] create field to map the 'STATUT' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "STATUT", columnDefinition = "ENUM")
    private Object statut;
*/
}