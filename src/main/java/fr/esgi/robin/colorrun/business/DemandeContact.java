package fr.esgi.robin.colorrun.business;

import java.time.Instant;

public class DemandeContact {
    private int id;
    private String nom;
    private String email;
    private String sujet;
    private String message;
    private Instant dateCreation;
    private boolean traite;
    private String reponse;
    private Instant dateTraitement;

    // Constructeurs
    public DemandeContact() {
        this.dateCreation = Instant.now();
        this.traite = false;
    }

    public DemandeContact(String nom, String email, String sujet, String message) {
        this();
        this.nom = nom;
        this.email = email;
        this.sujet = sujet;
        this.message = message;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSujet() { return sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Instant getDateCreation() { return dateCreation; }
    public void setDateCreation(Instant dateCreation) { this.dateCreation = dateCreation; }

    public boolean isTraite() { return traite; }
    public void setTraite(boolean traite) { this.traite = traite; }

    public String getReponse() { return reponse; }
    public void setReponse(String reponse) { this.reponse = reponse; }

    public Instant getDateTraitement() { return dateTraitement; }
    public void setDateTraitement(Instant dateTraitement) { this.dateTraitement = dateTraitement; }

    public String getStatutDisplay() {
        return traite ? "Traité" : "En attente";
    }
} 