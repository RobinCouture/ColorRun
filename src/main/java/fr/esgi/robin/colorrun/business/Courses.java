package fr.esgi.robin.colorrun.business;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "COURSES")
public class Courses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COURSE", nullable = false)
    private Integer id;

    @Column(name = "NOM_COURSE", nullable = false, length = 100)
    private String nomCourse;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "DATE_HEURE", nullable = false)
    private Instant dateHeure;

    @Column(name = "LIEU", nullable = false, length = 100)
    private String lieu;

    @Column(name = "DISTANCE", nullable = false)
    private Double distance;

    @Column(name = "PRIX", nullable = false)
    private Double prix;

    @Column(name = "NB_MAX_PARTICIPANTS", nullable = false)
    private Integer nbMaxParticipants;

    @Column(name = "CAUSE_SOUTENUE", length = 100)
    private String causeSoutenue;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANISATEUR_ID")
    private Utilisateur utilisateur;

    public Courses() {
    }

    public Courses(Integer id, String nomCourse, String description, Instant dateHeure, String lieu, Double distance, Double prix, Integer nbMaxParticipants, String causeSoutenue, Utilisateur utilisateur) {
        this.id = id;
        this.nomCourse = nomCourse;
        this.description = description;
        this.dateHeure = dateHeure;
        this.lieu = lieu;
        this.distance = distance;
        this.prix = prix;
        this.nbMaxParticipants = nbMaxParticipants;
        this.causeSoutenue = causeSoutenue;
        this.utilisateur = utilisateur;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomCourse() {
        return nomCourse;
    }

    public void setNomCourse(String nomCourse) {
        this.nomCourse = nomCourse;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(Instant dateHeure) {
        this.dateHeure = dateHeure;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public Integer getNbMaxParticipants() {
        return nbMaxParticipants;
    }

    public void setNbMaxParticipants(Integer nbMaxParticipants) {
        this.nbMaxParticipants = nbMaxParticipants;
    }

    public String getCauseSoutenue() {
        return causeSoutenue;
    }

    public void setCauseSoutenue(String causeSoutenue) {
        this.causeSoutenue = causeSoutenue;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

}