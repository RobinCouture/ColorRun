package fr.esgi.robin.colorrun.business;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "FILSDISCUSSION")
public class Filsdiscussion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MESSAGE", nullable = false)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COURSE")
    private Courses course;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UTILISATEUR")
    private Utilisateur utilisateur;

    @Lob
    @Column(name = "CONTENU", nullable = false)
    private String contenu;

    @Column(name = "DATE_ENVOI")
    private Instant dateEnvoi;

    public Filsdiscussion() {
    }

    public Filsdiscussion(Integer id, Courses course, Utilisateur utilisateur, String contenu, Instant dateEnvoi) {
        this.id = id;
        this.course = course;
        this.utilisateur = utilisateur;
        this.contenu = contenu;
        this.dateEnvoi = dateEnvoi;
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

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Instant getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(Instant dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

}