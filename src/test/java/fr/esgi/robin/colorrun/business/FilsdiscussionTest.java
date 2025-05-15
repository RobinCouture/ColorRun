package fr.esgi.robin.colorrun.business;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class FilsdiscussionTest {

    @Test
    void constructorShouldInitializeAllFieldsCorrectly() {
        Courses course = new Courses();
        Utilisateur utilisateur = new Utilisateur();
        Instant now = Instant.now();
        Filsdiscussion filsdiscussion = new Filsdiscussion(1, course, utilisateur, "Message content", now);

        assertEquals(1, filsdiscussion.getId());
        assertEquals(course, filsdiscussion.getCourse());
        assertEquals(utilisateur, filsdiscussion.getUtilisateur());
        assertEquals("Message content", filsdiscussion.getContenu());
        assertEquals(now, filsdiscussion.getDateEnvoi());
    }

    @Test
    void settersShouldUpdateFieldsCorrectly() {
        Filsdiscussion filsdiscussion = new Filsdiscussion();
        Courses course = new Courses();
        Utilisateur utilisateur = new Utilisateur();
        Instant now = Instant.now();

        filsdiscussion.setId(2);
        filsdiscussion.setCourse(course);
        filsdiscussion.setUtilisateur(utilisateur);
        filsdiscussion.setContenu("Updated content");
        filsdiscussion.setDateEnvoi(now);

        assertEquals(2, filsdiscussion.getId());
        assertEquals(course, filsdiscussion.getCourse());
        assertEquals(utilisateur, filsdiscussion.getUtilisateur());
        assertEquals("Updated content", filsdiscussion.getContenu());
        assertEquals(now, filsdiscussion.getDateEnvoi());
    }

    @Test
    void constructorShouldHandleNullValues() {
        Filsdiscussion filsdiscussion = new Filsdiscussion(null, null, null, null, null);

        assertNull(filsdiscussion.getId());
        assertNull(filsdiscussion.getCourse());
        assertNull(filsdiscussion.getUtilisateur());
        assertNull(filsdiscussion.getContenu());
        assertNull(filsdiscussion.getDateEnvoi());
    }

    @Test
    void settingNullValuesShouldNotThrowExceptions() {
        Filsdiscussion filsdiscussion = new Filsdiscussion();

        assertDoesNotThrow(() -> {
            filsdiscussion.setCourse(null);
            filsdiscussion.setUtilisateur(null);
            filsdiscussion.setContenu(null);
            filsdiscussion.setDateEnvoi(null);
        });

        assertNull(filsdiscussion.getCourse());
        assertNull(filsdiscussion.getUtilisateur());
        assertNull(filsdiscussion.getContenu());
        assertNull(filsdiscussion.getDateEnvoi());
    }
}