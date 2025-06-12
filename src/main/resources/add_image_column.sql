-- Ajout de la colonne IMAGE_URL à la table COURSES
ALTER TABLE COURSES ADD COLUMN IMAGE_URL VARCHAR(500);

-- Exemples d'images pour les courses existantes
UPDATE COURSES SET IMAGE_URL = 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80' WHERE NOM_COURSE LIKE '%Paris%';
UPDATE COURSES SET IMAGE_URL = 'https://images.unsplash.com/photo-1544737151-6e4ed999de69?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80' WHERE NOM_COURSE LIKE '%Lyon%';
UPDATE COURSES SET IMAGE_URL = 'https://images.unsplash.com/photo-1502680390469-be75c86b636f?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80' WHERE IMAGE_URL IS NULL; 