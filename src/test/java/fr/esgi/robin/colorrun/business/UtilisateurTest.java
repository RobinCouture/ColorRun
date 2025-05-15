package fr.esgi.robin.colorrun.business;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UtilisateurTest {

    @Test
    void constructorShouldInitializeAllFieldsCorrectly() {
        Instant now = Instant.now();
        Utilisateur utilisateur = new Utilisateur(1, "Doe", "John", "john.doe@example.com", "password123", "profile.jpg", now);

        assertEquals(1, utilisateur.getId());
        assertEquals("Doe", utilisateur.getNom());
        assertEquals("John", utilisateur.getPrenom());
        assertEquals("john.doe@example.com", utilisateur.getEmail());
        assertEquals("password123", utilisateur.getMotDePasse());
        assertEquals("profile.jpg", utilisateur.getPhotoProfil());
        assertEquals(now, utilisateur.getDateInscription());
    }

    @Test
    void settersShouldUpdateFieldsCorrectly() {
        Utilisateur utilisateur = new Utilisateur();
        Instant now = Instant.now();

        utilisateur.setId(2);
        utilisateur.setNom("Smith");
        utilisateur.setPrenom("Jane");
        utilisateur.setEmail("jane.smith@example.com");
        utilisateur.setMotDePasse("securepassword");
        utilisateur.setPhotoProfil("avatar.png");
        utilisateur.setDateInscription(now);

        assertEquals(2, utilisateur.getId());
        assertEquals("Smith", utilisateur.getNom());
        assertEquals("Jane", utilisateur.getPrenom());
        assertEquals("jane.smith@example.com", utilisateur.getEmail());
        assertEquals("securepassword", utilisateur.getMotDePasse());
        assertEquals("avatar.png", utilisateur.getPhotoProfil());
        assertEquals(now, utilisateur.getDateInscription());
    }

    @Test
    void constructorShouldHandleNullValues() {
        Utilisateur utilisateur = new Utilisateur(null, null, null, null, null, null, null);

        assertNull(utilisateur.getId());
        assertNull(utilisateur.getNom());
        assertNull(utilisateur.getPrenom());
        assertNull(utilisateur.getEmail());
        assertNull(utilisateur.getMotDePasse());
        assertNull(utilisateur.getPhotoProfil());
        assertNull(utilisateur.getDateInscription());
    }

    @Test
    void settingNullValuesShouldNotThrowExceptions() {
        Utilisateur utilisateur = new Utilisateur();

        assertDoesNotThrow(() -> {
            utilisateur.setNom(null);
            utilisateur.setPrenom(null);
            utilisateur.setEmail(null);
            utilisateur.setMotDePasse(null);
            utilisateur.setPhotoProfil(null);
            utilisateur.setDateInscription(null);
        });

        assertNull(utilisateur.getNom());
        assertNull(utilisateur.getPrenom());
        assertNull(utilisateur.getEmail());
        assertNull(utilisateur.getMotDePasse());
        assertNull(utilisateur.getPhotoProfil());
        assertNull(utilisateur.getDateInscription());
    }
}