package fr.esgi.robin.colorrun.business;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class InscriptionTest {

    @Test
    void constructorShouldInitializeAllFieldsCorrectly() {
        Courses course = new Courses();
        Utilisateur utilisateur = new Utilisateur();
        Instant now = Instant.now();
        Inscription inscription = new Inscription(1, course, utilisateur, "D123", "QR123", now);

        assertEquals(1, inscription.getId());
        assertEquals(course, inscription.getCourse());
        assertEquals(utilisateur, inscription.getUtilisateur());
        assertEquals("D123", inscription.getDossard());
        assertEquals("QR123", inscription.getQrCode());
        assertEquals(now, inscription.getDateInscription());
    }

    @Test
    void settersShouldUpdateFieldsCorrectly() {
        Inscription inscription = new Inscription();
        Courses course = new Courses();
        Utilisateur utilisateur = new Utilisateur();
        Instant now = Instant.now();

        inscription.setId(2);
        inscription.setCourse(course);
        inscription.setUtilisateur(utilisateur);
        inscription.setDossard("D456");
        inscription.setQrCode("QR456");
        inscription.setDateInscription(now);

        assertEquals(2, inscription.getId());
        assertEquals(course, inscription.getCourse());
        assertEquals(utilisateur, inscription.getUtilisateur());
        assertEquals("D456", inscription.getDossard());
        assertEquals("QR456", inscription.getQrCode());
        assertEquals(now, inscription.getDateInscription());
    }

    @Test
    void constructorShouldHandleNullValues() {
        Inscription inscription = new Inscription(null, null, null, null, null, null);

        assertNull(inscription.getId());
        assertNull(inscription.getCourse());
        assertNull(inscription.getUtilisateur());
        assertNull(inscription.getDossard());
        assertNull(inscription.getQrCode());
        assertNull(inscription.getDateInscription());
    }

    @Test
    void settingNullValuesShouldNotThrowExceptions() {
        Inscription inscription = new Inscription();

        assertDoesNotThrow(() -> {
            inscription.setCourse(null);
            inscription.setUtilisateur(null);
            inscription.setDossard(null);
            inscription.setQrCode(null);
            inscription.setDateInscription(null);
        });

        assertNull(inscription.getCourse());
        assertNull(inscription.getUtilisateur());
        assertNull(inscription.getDossard());
        assertNull(inscription.getQrCode());
        assertNull(inscription.getDateInscription());
    }
}