package fr.esgi.robin.colorrun.business;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class PagesstatiqueTest {

    @Test
    void constructorShouldInitializeMandatoryFieldsCorrectly() {
        Pagesstatique page = new Pagesstatique(1, "Home");

        assertEquals(1, page.getId());
        assertEquals("Home", page.getTitrePage());
    }

    @Test
    void settersShouldUpdateOptionalFieldsCorrectly() {
        Pagesstatique page = new Pagesstatique(1, "Home");
        Instant now = Instant.now();

        page.setContenu("Welcome to the homepage.");
        page.setDateDerniereModif(now);

        assertEquals("Welcome to the homepage.", page.getContenu());
        assertEquals(now, page.getDateDerniereModif());
    }

    @Test
    void settingNullValuesShouldNotThrowExceptions() {
        Pagesstatique page = new Pagesstatique(1, "Home");

        assertDoesNotThrow(() -> {
            page.setContenu(null);
            page.setDateDerniereModif(null);
        });

        assertNull(page.getContenu());
        assertNull(page.getDateDerniereModif());
    }
}