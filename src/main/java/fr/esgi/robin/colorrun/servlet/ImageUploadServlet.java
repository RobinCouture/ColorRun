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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@WebServlet("/upload-image")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 1024 * 1024 * 10,  // 10 MB
    maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class ImageUploadServlet extends HttpServlet {
    
    private static final String UPLOAD_DIR = "images/courses";
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        
        try {
            // Récupérer le fichier uploadé
            Part filePart = req.getPart("image");
            
            if (filePart == null || filePart.getSize() == 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Aucun fichier sélectionné\"}");
                return;
            }
            
            String fileName = getSubmittedFileName(filePart);
            
            // Vérifier l'extension
            if (!isValidImageFile(fileName)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Format de fichier non supporté. Utilisez JPG, PNG, GIF ou WebP\"}");
                return;
            }
            
            // Créer un nom unique pour éviter les conflits
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            
            // Créer le dossier de destination s'il n'existe pas
            String realPath = getServletContext().getRealPath("");
            Path uploadPath = Paths.get(realPath, "resources", UPLOAD_DIR);
            Files.createDirectories(uploadPath);
            
            // Sauvegarder le fichier
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(filePart.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Retourner l'URL relative de l'image
            String imageUrl = req.getContextPath() + "/resources/" + UPLOAD_DIR + "/" + uniqueFileName;
            
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"success\":true,\"imageUrl\":\"" + imageUrl + "\",\"fileName\":\"" + fileName + "\"}");
            
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Erreur lors de l'upload: " + e.getMessage() + "\"}");
        }
    }
    
    private String getSubmittedFileName(Part part) {
        String contentDisposition = part.getHeader("Content-Disposition");
        String[] items = contentDisposition.split(";");
        for (String item : items) {
            if (item.trim().startsWith("filename")) {
                return item.substring(item.indexOf('=') + 2, item.length() - 1);
            }
        }
        return "";
    }
    
    private boolean isValidImageFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        
        String lowerCaseFileName = fileName.toLowerCase();
        for (String extension : ALLOWED_EXTENSIONS) {
            if (lowerCaseFileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
} 