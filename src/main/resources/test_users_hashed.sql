-- Utilisateurs de test avec mots de passe hachés (BCrypt)

-- Supprimer les anciens utilisateurs
DELETE FROM Utilisateurs WHERE email IN ('admin@colorrun.com', 'jean@test.com', 'sophie@test.com', 'pierre@test.com');

-- Utilisateur ADMIN (mot de passe: admin123)
INSERT INTO Utilisateurs (nom, prenom, email, mot_de_passe, role, date_inscription)
VALUES ('Admin', 'Super', 'admin@colorrun.com', '$2a$10$8K9wE2nLtRgHyZy45A9.9eBDCcHoD1CdmQ1HL9x4tU5Zp8jN6mO.S', 'admin', CURRENT_TIMESTAMP);

-- Utilisateur PARTICIPANT (mot de passe: password123)
INSERT INTO Utilisateurs (nom, prenom, email, mot_de_passe, role, date_inscription)
VALUES ('Dupont', 'Jean', 'jean@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.fqOdOOaXJhiY5n7BqXb2R4A2fL9Dse', 'participant', CURRENT_TIMESTAMP);

-- Utilisateur ORGANISATEUR (mot de passe: password123)
INSERT INTO Utilisateurs (nom, prenom, email, mot_de_passe, role, date_inscription)
VALUES ('Martin', 'Sophie', 'sophie@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.fqOdOOaXJhiY5n7BqXb2R4A2fL9Dse', 'organisateur', CURRENT_TIMESTAMP);

-- Autre utilisateur PARTICIPANT (mot de passe: password123)
INSERT INTO Utilisateurs (nom, prenom, email, mot_de_passe, role, date_inscription)
VALUES ('Bernard', 'Pierre', 'pierre@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.fqOdOOaXJhiY5n7BqXb2R4A2fL9Dse', 'participant', CURRENT_TIMESTAMP); 