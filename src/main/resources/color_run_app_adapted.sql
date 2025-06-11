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

-- Table : Pages Statiques
CREATE TABLE IF NOT EXISTS PagesStatiques (
    id_page INT AUTO_INCREMENT PRIMARY KEY,
    titre_page VARCHAR(100) NOT NULL,
    contenu TEXT NOT NULL,
    date_derniere_modif DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Index pour optimiser les recherches
CREATE INDEX IF NOT EXISTS idx_email ON Utilisateurs(email);
CREATE INDEX IF NOT EXISTS idx_course_inscriptions ON Inscriptions(id_course);
CREATE INDEX IF NOT EXISTS idx_utilisateur_discussion ON FilsDiscussion(id_utilisateur);
CREATE INDEX IF NOT EXISTS idx_statut_demandes ON DemandesOrganisateur(statut);

-- Données initiales avec MERGE pour éviter les doublons
MERGE INTO Utilisateurs (id_utilisateur, nom, prenom, email, mot_de_passe, role)
KEY(email)
VALUES (1, 'Admin', 'ColorRun', 'admin@colorun.com', 'hashed_password', 'admin');

-- Ajouter des courses d'exemple avec MERGE pour éviter les doublons
MERGE INTO Courses (id_course, nom_course, description, date_heure, lieu, distance, prix, nb_max_participants, cause_soutenue, organisateur_id)
KEY(id_course)
VALUES 
(1, 'ColorRun Paris', 'Une course colorée au cœur de la capitale française. Venez vivre une expérience unique dans les rues de Paris avec des milliers de participants !', '2024-06-15 10:00:00', 'Paris, France', 5.0, 25.0, 500, 'Soutien aux enfants malades', 1),
(2, 'ColorRun Lyon', 'Découvrez la ville des lumières sous un nouveau jour avec notre course colorée à travers Lyon. Un parcours magique vous attend !', '2024-07-20 09:30:00', 'Lyon, France', 3.5, 20.0, 300, 'Protection de l''environnement', 1),
(3, 'ColorRun Marseille', 'Course au bord de la Méditerranée avec une vue imprenable sur la mer. Courez dans un cadre idyllique !', '2024-08-10 08:00:00', 'Marseille, France', 7.0, 30.0, 400, 'Aide aux personnes âgées', 1);