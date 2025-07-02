package fr.esgi.robin.colorrun.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@WebServlet(name = "resourceServlet", urlPatterns = {"/ressources/*"})
public class ResourceServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Construire le chemin vers le fichier
        String resourcePath = "/WEB-INF/../ressources" + pathInfo;
        
        // Définir le type de contenu
        if (pathInfo.endsWith(".css")) {
            response.setContentType("text/css");
        } else if (pathInfo.endsWith(".js")) {
            response.setContentType("application/javascript");
        } else if (pathInfo.endsWith(".png")) {
            response.setContentType("image/png");
        } else if (pathInfo.endsWith(".jpg") || pathInfo.endsWith(".jpeg")) {
            response.setContentType("image/jpeg");
        }
        
        // Servir le fichier
        try (InputStream inputStream = getServletContext().getResourceAsStream(resourcePath);
             OutputStream outputStream = response.getOutputStream()) {
            
            if (inputStream == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }
}
