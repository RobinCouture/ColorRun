package fr.esgi.robin.colorrun.servlet;

import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.repository.UtilisateurRepository;
import fr.esgi.robin.colorrun.repository.impl.UtilisateurRepositoryImpl;
import fr.esgi.robin.colorrun.util.PasswordUtils;
import fr.esgi.robin.colorrun.util.TemplateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "login", value = "/login")
public class LoginServlet extends HttpServlet {
    private final UtilisateurRepository utilisateurRepository = new UtilisateurRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateUtil.processTemplate("login", req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String motDePasse = req.getParameter("motDePasse");

        Utilisateur user = utilisateurRepository.findByEmail(email);

        if (user != null && PasswordUtils.checkPassword(motDePasse, user.getMotDePasse())) {
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            resp.sendRedirect(req.getContextPath() + "/hello-servlet");
        } else {
            TemplateUtil.processTemplate("login", req, resp, Map.of("errorMessage", "Email ou mot de passe incorrect"));
        }
    }
}
