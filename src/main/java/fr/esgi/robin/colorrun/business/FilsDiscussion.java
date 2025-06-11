package fr.esgi.robin.colorrun.business;

import java.time.Instant;

public class FilsDiscussion {
    private Integer idMessage;
    private String contenu;
    private Instant dateEnvoi;
    private Utilisateur utilisateur;
    private Courses course;

    public FilsDiscussion() {}

    public FilsDiscussion(Integer idMessage, String contenu, Instant dateEnvoi, Utilisateur utilisateur, Courses course) {
        this.idMessage = idMessage;
        this.contenu = contenu;
        this.dateEnvoi = dateEnvoi;
        this.utilisateur = utilisateur;
        this.course = course;
    }

    // Getters et Setters
    public Integer getIdMessage() { return idMessage; }
    public void setIdMessage(Integer idMessage) { this.idMessage = idMessage; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public Instant getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(Instant dateEnvoi) { this.dateEnvoi = dateEnvoi; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    public Courses getCourse() { return course; }
    public void setCourse(Courses course) { this.course = course; }
}