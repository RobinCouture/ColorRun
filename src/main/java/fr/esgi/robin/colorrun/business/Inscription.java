package fr.esgi.robin.colorrun.business;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "Inscriptions")
public class Inscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inscription", nullable = false)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_course")
    private Courses course;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur")
    private Utilisateur utilisateur;

    @Column(name = "dossard", nullable = false, length = 50)
    private String dossard;

    @Column(name = "qr_code")
    private String qrCode;

    @Column(name = "date_inscription")
    private Instant dateInscription;

    public Inscription() {
    }

    public Inscription(Integer id, Courses course, Utilisateur utilisateur, String dossard, String qrCode, Instant dateInscription) {
        this.id = id;
        this.course = course;
        this.utilisateur = utilisateur;
        this.dossard = dossard;
        this.qrCode = qrCode;
        this.dateInscription = dateInscription;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Courses getCourse() {
        return course;
    }

    public void setCourse(Courses course) {
        this.course = course;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getDossard() {
        return dossard;
    }

    public void setDossard(String dossard) {
        this.dossard = dossard;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Instant getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(Instant dateInscription) {
        this.dateInscription = dateInscription;
    }

}