package fr.esgi.robin.colorrun.business;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CoursesTest {

    @Test
    void constructorShouldInitializeAllFieldsCorrectly() {
        Utilisateur utilisateur = new Utilisateur();
        Instant now = Instant.now();
        Courses course = new Courses(1, "Marathon", "A long race", now, "Paris", 42.195, 50.0, 100, "Charity", utilisateur);

        assertEquals(1, course.getId());
        assertEquals("Marathon", course.getNomCourse());
        assertEquals("A long race", course.getDescription());
        assertEquals(now, course.getDateHeure());
        assertEquals("Paris", course.getLieu());
        assertEquals(42.195, course.getDistance());
        assertEquals(50.0, course.getPrix());
        assertEquals(100, course.getNbMaxParticipants());
        assertEquals("Charity", course.getCauseSoutenue());
        assertEquals(utilisateur, course.getUtilisateur());
    }

    @Test
    void settersShouldUpdateFieldsCorrectly() {
        Courses course = new Courses();
        Utilisateur utilisateur = new Utilisateur();
        Instant now = Instant.now();

        course.setId(2);
        course.setNomCourse("Sprint");
        course.setDescription("A short race");
        course.setDateHeure(now);
        course.setLieu("Lyon");
        course.setDistance(5.0);
        course.setPrix(20.0);
        course.setNbMaxParticipants(50);
        course.setCauseSoutenue("Education");
        course.setUtilisateur(utilisateur);

        assertEquals(2, course.getId());
        assertEquals("Sprint", course.getNomCourse());
        assertEquals("A short race", course.getDescription());
        assertEquals(now, course.getDateHeure());
        assertEquals("Lyon", course.getLieu());
        assertEquals(5.0, course.getDistance());
        assertEquals(20.0, course.getPrix());
        assertEquals(50, course.getNbMaxParticipants());
        assertEquals("Education", course.getCauseSoutenue());
        assertEquals(utilisateur, course.getUtilisateur());
    }

    @Test
    void constructorShouldHandleNullValues() {
        Courses course = new Courses(null, null, null, null, null, null, null, null, null, null);

        assertNull(course.getId());
        assertNull(course.getNomCourse());
        assertNull(course.getDescription());
        assertNull(course.getDateHeure());
        assertNull(course.getLieu());
        assertNull(course.getDistance());
        assertNull(course.getPrix());
        assertNull(course.getNbMaxParticipants());
        assertNull(course.getCauseSoutenue());
        assertNull(course.getUtilisateur());
    }
}