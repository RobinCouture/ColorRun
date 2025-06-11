-- Script pour créer des utilisateurs de test avec différents rôles
-- 2 utilisateurs par rôle (admin, organisateur, participant)

INSERT INTO UTILISATEURS (NOM, PRENOM, EMAIL, MOT_DE_PASSE, ROLE, DATE_INSCRIPTION) VALUES
-- 2 Admins
('Dupont', 'Marie', 'marie.dupont@admin.com', 'admin123', 'admin', CURRENT_TIMESTAMP),
('Martin', 'Pierre', 'pierre.martin@admin.com', 'admin123', 'admin', CURRENT_TIMESTAMP),

-- 2 Organisateurs
('Bernard', 'Sophie', 'sophie.bernard@organisateur.com', 'org123', 'organisateur', CURRENT_TIMESTAMP),
('Petit', 'Julien', 'julien.petit@organisateur.com', 'org123', 'organisateur', CURRENT_TIMESTAMP),

-- 2 Participants
('Moreau', 'Emma', 'emma.moreau@participant.com', 'user123', 'participant', CURRENT_TIMESTAMP),
('Robert', 'Lucas', 'lucas.robert@participant.com', 'user123', 'participant', CURRENT_TIMESTAMP),

-- Utilisateurs supplémentaires pour tester la pagination
('Durand', 'Léa', 'lea.durand@test.com', 'test123', 'participant', CURRENT_TIMESTAMP),
('Leroy', 'Hugo', 'hugo.leroy@test.com', 'test123', 'participant', CURRENT_TIMESTAMP),
('Moreau', 'Chloé', 'chloe.moreau@test.com', 'test123', 'organisateur', CURRENT_TIMESTAMP),
('Simon', 'Nathan', 'nathan.simon@test.com', 'test123', 'participant', CURRENT_TIMESTAMP),
('Michel', 'Camille', 'camille.michel@test.com', 'test123', 'participant', CURRENT_TIMESTAMP),
('Garcia', 'Mathis', 'mathis.garcia@test.com', 'test123', 'organisateur', CURRENT_TIMESTAMP);

-- Ajouter quelques messages de test dans le fils de discussion
INSERT INTO FilsDiscussion (contenu, id_utilisateur, id_course, date_envoi) VALUES
('Salut tout le monde ! Hâte de participer à cette course ColorRun Paris !', 2, 1, CURRENT_TIMESTAMP),
('Quelqu''un sait s''il faut apporter ses propres couleurs ?', 3, 1, CURRENT_TIMESTAMP),
('J''ai déjà fait Lyon l''année dernière, c''était fantastique ! 🏃‍♀️', 4, 2, CURRENT_TIMESTAMP),
('Question sur le niveau de difficulté pour Marseille ?', 2, 3, CURRENT_TIMESTAMP),
('Super organisation, merci aux organisateurs ! 👏', 4, 1, CURRENT_TIMESTAMP); 