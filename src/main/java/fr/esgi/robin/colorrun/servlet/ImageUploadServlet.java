package fr.esgi.robin.colorrun.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@WebServlet("/upload-image")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 1024 * 1024 * 5,   // 5 MB
    maxRequestSize = 1024 * 1024 * 10 // 10 MB
)
public class ImageUploadServlet extends HttpServlet {
    
    private static final String UPLOAD_DIRECTORY = "course-images";
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Récupérer le fichier uploadé
            Part filePart = request.getPart("image");
            
            if (filePart == null || filePart.getSize() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Aucun fichier sélectionné\"}");
                return;
            }
            
            String fileName = filePart.getSubmittedFileName();
            if (fileName == null || fileName.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Nom de fichier invalide\"}");
                return;
            }
            
            // Vérifier l'extension
            String fileExtension = getFileExtension(fileName).toLowerCase();
            if (!isAllowedExtension(fileExtension)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Type de fichier non autorisé. Formats acceptés: JPG, PNG, GIF, WEBP\"}");
                return;
            }
            
            // Créer le répertoire de destination s'il n'existe pas
            String realPath = getServletContext().getRealPath("/");
            Path uploadPath = Paths.get(realPath, "resources", "images", UPLOAD_DIRECTORY);
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Générer un nom unique pour le fichier
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            Path targetPath = uploadPath.resolve(uniqueFileName);
            
            // Sauvegarder le fichier
            try (InputStream inputStream = filePart.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            
            // Construire l'URL pour accéder à l'image
            String imageUrl = request.getContextPath() + "/resources/images/" + UPLOAD_DIRECTORY + "/" + uniqueFileName;
            
            // Retourner la réponse JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\": true, \"imageUrl\": \"" + imageUrl + "\"}");
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'upload d'image: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Erreur lors de l'upload: " + e.getMessage() + "\"}");
        }
    }
    
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex);
        }
        return "";
    }
    
    private boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equals(extension)) {
                return true;
            }
        }
        return false;
    }
} 