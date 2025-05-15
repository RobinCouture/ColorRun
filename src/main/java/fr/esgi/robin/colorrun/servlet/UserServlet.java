package fr.esgi.robin.colorrun.servlet;

import fr.esgi.robin.colorrun.repository.UtilisateurRepository;
import fr.esgi.robin.colorrun.repository.impl.UtilisateurRepositoryImpl;
import fr.esgi.robin.colorrun.util.TemplateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "userServlet", value = "/users")
public class UserServlet extends HttpServlet {
    private String message;

    private final UtilisateurRepository utilisateurRepository = new UtilisateurRepositoryImpl();

    @Override
    public void init() throws ServletException {
        message = "Liste des utilisateurs";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateUtil.processTemplate("userList", req, resp, Map.of("utilisateurs", utilisateurRepository.findAll()));
    }
}
