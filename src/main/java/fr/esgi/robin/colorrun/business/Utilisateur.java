package fr.esgi.robin.colorrun.business;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "UTILISATEURS")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_UTILISATEUR", nullable = false)
    private Integer id;

    @Column(name = "NOM", nullable = false, length = 50)
    private String nom;

    @Column(name = "PRENOM", nullable = false, length = 50)
    private String prenom;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Column(name = "MOT_DE_PASSE", nullable = false)
    private String motDePasse;

    @Column(name = "PHOTO_PROFIL")
    private String photoProfil;

    @Column(name = "DATE_INSCRIPTION")
    private Instant dateInscription;

    @Column(name = "ROLE")
    private String roleString = "participant";

    @Column(name = "RESET_TOKEN")
    private String resetToken;

    @Column(name = "RESET_TOKEN_EXPIRATION")
    private Instant resetTokenExpiration;

    public Utilisateur() {
    }

    public Utilisateur(Integer id, String nom, String prenom, String email, String motDePasse, String photoProfil, Instant dateInscription) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.photoProfil = photoProfil;
        this.dateInscription = dateInscription;
        this.roleString = "participant";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getPhotoProfil() {
        return photoProfil;
    }

    public void setPhotoProfil(String photoProfil) {
        this.photoProfil = photoProfil;
    }

    public Instant getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(Instant dateInscription) {
        this.dateInscription = dateInscription;
    }

    public String getRoleString() {
        return roleString;
    }

    public void setRoleString(String roleString) {
        this.roleString = roleString;
    }

    public Role getRole() {
        return Role.fromString(this.roleString);
    }

    public void setRole(Role role) {
        this.roleString = role.getValue();
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(this.roleString);
    }

    public boolean isOrganisateur() {
        return "organisateur".equalsIgnoreCase(this.roleString);
    }

    public boolean canCreateCourse() {
        return isAdmin() || isOrganisateur();
    }

    public String getNomComplet() {
        return this.prenom + " " + this.nom;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public Instant getResetTokenExpiration() {
        return resetTokenExpiration;
    }

    public void setResetTokenExpiration(Instant resetTokenExpiration) {
        this.resetTokenExpiration = resetTokenExpiration;
    }

/*
 TODO [Reverse Engineering] create field to map the 'ROLE' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "ROLE", columnDefinition = "ENUM")
    private Object role;
*/
}