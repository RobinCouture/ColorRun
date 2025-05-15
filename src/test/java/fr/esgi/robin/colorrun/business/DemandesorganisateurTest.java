package fr.esgi.robin.colorrun.business;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class DemandesorganisateurTest {

    @Test
    void constructorShouldInitializeAllFieldsCorrectly() {
        Utilisateur utilisateur = new Utilisateur();
        Instant now = Instant.now();
        Demandesorganisateur demande = new Demandesorganisateur(1, utilisateur, "Motivation text", StatutDemande.ACCEPTE, now);

        assertEquals(1, demande.getId());
        assertEquals(utilisateur, demande.getUtilisateur());
        assertEquals("Motivation text", demande.getMotivation());
        assertEquals(StatutDemande.ACCEPTE, demande.getStatut());
        assertEquals(now, demande.getDateDemande());
    }

    @Test
    void settersShouldUpdateFieldsCorrectly() {
        Demandesorganisateur demande = new Demandesorganisateur();
        Utilisateur utilisateur = new Utilisateur();
        Instant now = Instant.now();

        demande.setId(2);
        demande.setUtilisateur(utilisateur);
        demande.setMotivation("Updated motivation");
        demande.setStatut(StatutDemande.REFUSE);
        demande.setDateDemande(now);

        assertEquals(2, demande.getId());
        assertEquals(utilisateur, demande.getUtilisateur());
        assertEquals("Updated motivation", demande.getMotivation());
        assertEquals(StatutDemande.REFUSE, demande.getStatut());
        assertEquals(now, demande.getDateDemande());
    }

    @Test
    void constructorShouldHandleNullValues() {
        Demandesorganisateur demande = new Demandesorganisateur(null, null, null, null, null);

        assertNull(demande.getId());
        assertNull(demande.getUtilisateur());
        assertNull(demande.getMotivation());
        assertNull(demande.getStatut());
        assertNull(demande.getDateDemande());
    }

    @Test
    void settingNullValuesShouldNotThrowExceptions() {
        Demandesorganisateur demande = new Demandesorganisateur();

        assertDoesNotThrow(() -> {
            demande.setUtilisateur(null);
            demande.setMotivation(null);
            demande.setStatut(null);
            demande.setDateDemande(null);
        });

        assertNull(demande.getUtilisateur());
        assertNull(demande.getMotivation());
        assertNull(demande.getStatut());
        assertNull(demande.getDateDemande());
    }
}