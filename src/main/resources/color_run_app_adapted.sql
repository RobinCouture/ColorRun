-- Table : Utilisateurs
CREATE TABLE IF NOT EXISTS Utilisateurs (
    id_utilisateur INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    role ENUM('participant', 'organisateur', 'admin') DEFAULT 'participant',
    photo_profil VARCHAR(255),
    date_inscription DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Table : Courses
CREATE TABLE IF NOT EXISTS Courses (
    id_course INT AUTO_INCREMENT PRIMARY KEY,
    nom_course VARCHAR(100) NOT NULL,
    description TEXT,
    date_heure DATETIME NOT NULL,
    lieu VARCHAR(100) NOT NULL,
    distance FLOAT NOT NULL,
    prix FLOAT NOT NULL,
    nb_max_participants INT NOT NULL,
    cause_soutenue VARCHAR(100),
    organisateur_id INT,
    FOREIGN KEY (organisateur_id) REFERENCES Utilisateurs(id_utilisateur) ON DELETE SET NULL
);

-- Table : Inscriptions
CREATE TABLE IF NOT EXISTS Inscriptions (
    id_inscription INT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur INT,
    id_course INT,
    dossard VARCHAR(50) NOT NULL,
    qr_code VARCHAR(255),
    date_inscription DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_utilisateur) REFERENCES Utilisateurs(id_utilisateur) ON DELETE CASCADE,
    FOREIGN KEY (id_course) REFERENCES Courses(id_course) ON DELETE CASCADE
);

-- Table : Fils de Discussion
CREATE TABLE IF NOT EXISTS FilsDiscussion (
    id_message INT AUTO_INCREMENT PRIMARY KEY,
    id_course INT,
    id_utilisateur INT,
    contenu TEXT NOT NULL,
    date_envoi DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_course) REFERENCES Courses(id_course) ON DELETE CASCADE,
    FOREIGN KEY (id_utilisateur) REFERENCES Utilisateurs(id_utilisateur) ON DELETE CASCADE
);

-- Table : Demandes de Devenir Organisateur
CREATE TABLE IF NOT EXISTS DemandesOrganisateur (
    id_demande INT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur INT,
    motivation TEXT NOT NULL,
    statut ENUM('en attente', 'acceptée', 'refusée') DEFAULT 'en attente',
    date_demande DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_utilisateur) REFERENCES Utilisateurs(id_utilisateur) ON DELETE CASCADE
);

-- Table : Demande de Contact
CREATE TABLE IF NOT EXISTS demandes_contact (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    sujet VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    date_creation TIMESTAMP NOT NULL,
    traite BOOLEAN DEFAULT FALSE,
    reponse TEXT,
    date_traitement TIMESTAMP NULL
);

-- Table : Pages Statiques
CREATE TABLE IF NOT EXISTS PagesStatiques (
    id_page INT AUTO_INCREMENT PRIMARY KEY,
    titre_page VARCHAR(100) NOT NULL,
    contenu TEXT NOT NULL,
    date_derniere_modif DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table : Images des Courses
CREATE TABLE IF NOT EXISTS IMAGECOURSE (
    IDIMAGE INT AUTO_INCREMENT PRIMARY KEY,
    IDCOURSE INT,
    PATH TEXT,
    NOM TEXT
);

-- Table : Token de Changement de Mot de Passe
CREATE TABLE IF NOT EXISTS TOKENCHANGEPASSWORD (
    IDTOKEN INT AUTO_INCREMENT PRIMARY KEY,
    IDUTILISATEUR INT,
    TOKEN VARCHAR(255) NOT NULL,
    DATE_CREATION DATETIME DEFAULT CURRENT_TIMESTAMP,
    DATE_EXPIRATION DATETIME NOT NULL
);

-- Index pour optimiser les recherches
CREATE INDEX IF NOT EXISTS idx_email ON Utilisateurs(email);
CREATE INDEX IF NOT EXISTS idx_course_inscriptions ON Inscriptions(id_course);
CREATE INDEX IF NOT EXISTS idx_utilisateur_discussion ON FilsDiscussion(id_utilisateur);
CREATE INDEX IF NOT EXISTS idx_statut_demandes ON DemandesOrganisateur(statut);

-- Données initiales - SYNTAXE H2 CORRECTE
-- Utiliser INSERT avec conditions pour éviter les doublons
INSERT INTO Utilisateurs (id_utilisateur, nom, prenom, email, mot_de_passe, role)
SELECT 1, 'Admin', 'ColorRun', 'admin@colorun.com', 'hashed_password', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM Utilisateurs WHERE email = 'admin@colorun.com');

-- Utilisateur test avec SYNTAXE H2
INSERT INTO Utilisateurs (nom, prenom, email, mot_de_passe, role, date_inscription)
SELECT 'Test', 'Admin', 'test@admin.com', 'admin123', 'admin', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM Utilisateurs WHERE email = 'test@admin.com');

-- Courses avec DATES FUTURES (2025-2026) - SANS SPÉCIFIER L'ID
INSERT INTO Courses (nom_course, description, date_heure, lieu, distance, prix, nb_max_participants, cause_soutenue, organisateur_id)
SELECT 'ColorRun Paris', 'Une course colorée au cœur de la capitale française. Venez vivre une expérience unique dans les rues de Paris avec des milliers de participants !', '2025-09-15 10:00:00', 'Paris, France', 5.0, 25.0, 500, 'Soutien aux enfants malades', 1
WHERE NOT EXISTS (SELECT 1 FROM Courses WHERE nom_course = 'ColorRun Paris');

INSERT INTO Courses (nom_course, description, date_heure, lieu, distance, prix, nb_max_participants, cause_soutenue, organisateur_id)
SELECT 'ColorRun Lyon', 'Découvrez la ville des lumières sous un nouveau jour avec notre course colorée à travers Lyon. Un parcours magique vous attend !', '2025-10-20 09:30:00', 'Lyon, France', 3.5, 20.0, 300, 'Protection de l''environnement', 1
WHERE NOT EXISTS (SELECT 1 FROM Courses WHERE nom_course = 'ColorRun Lyon');

INSERT INTO Courses (nom_course, description, date_heure, lieu, distance, prix, nb_max_participants, cause_soutenue, organisateur_id)
SELECT 'ColorRun Marseille', 'Course au bord de la Méditerranée avec une vue imprenable sur la mer. Courez dans un cadre idyllique !', '2025-11-10 08:00:00', 'Marseille, France', 7.0, 30.0, 400, 'Aide aux personnes âgées', 1
WHERE NOT EXISTS (SELECT 1 FROM Courses WHERE nom_course = 'ColorRun Marseille');

-- H2 : Forcer la réinitialisation de l'auto-increment à la prochaine valeur libre
ALTER TABLE Courses ALTER COLUMN id_course RESTART WITH (SELECT MAX(id_course) + 1 FROM Courses);

-- Reset the auto-increment sequence for IMAGECOURSE
ALTER TABLE IMAGECOURSE ALTER COLUMN IDIMAGE RESTART WITH (SELECT COALESCE(MAX(IDIMAGE), 0) + 1 FROM IMAGECOURSE);

-- Reset the auto-increment sequence for TOKENCHANGEPASSWORD
ALTER TABLE TOKENCHANGEPASSWORD ALTER COLUMN IDTOKEN RESTART WITH (SELECT COALESCE(MAX(IDTOKEN), 0) + 1 FROM TOKENCHANGEPASSWORD);
