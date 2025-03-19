

-- Table : Utilisateurs
CREATE TABLE Utilisateurs (
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
CREATE TABLE Courses (
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
CREATE TABLE Inscriptions (
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
CREATE TABLE FilsDiscussion (
    id_message INT AUTO_INCREMENT PRIMARY KEY,
    id_course INT,
    id_utilisateur INT,
    contenu TEXT NOT NULL,
    date_envoi DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_course) REFERENCES Courses(id_course) ON DELETE CASCADE,
    FOREIGN KEY (id_utilisateur) REFERENCES Utilisateurs(id_utilisateur) ON DELETE CASCADE
);

-- Table : Demandes de Devenir Organisateur
CREATE TABLE DemandesOrganisateur (
    id_demande INT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur INT,
    motivation TEXT NOT NULL,
    statut ENUM('en attente', 'acceptée', 'refusée') DEFAULT 'en attente',
    date_demande DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_utilisateur) REFERENCES Utilisateurs(id_utilisateur) ON DELETE CASCADE
);

-- Table : Pages Statiques
CREATE TABLE PagesStatiques (
    id_page INT AUTO_INCREMENT PRIMARY KEY,
    titre_page VARCHAR(100) NOT NULL,
    contenu TEXT NOT NULL,
    date_derniere_modif DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Index pour optimiser les recherches
CREATE INDEX idx_email ON Utilisateurs(email);
CREATE INDEX idx_course_inscriptions ON Inscriptions(id_course);
CREATE INDEX idx_utilisateur_discussion ON FilsDiscussion(id_utilisateur);
CREATE INDEX idx_statut_demandes ON DemandesOrganisateur(statut);

-- Données initiales
INSERT INTO Utilisateurs (nom, prenom, email, mot_de_passe, role)
VALUES ('Admin', 'ColorRun', 'admin@colorun.com', 'hashed_password', 'admin');
