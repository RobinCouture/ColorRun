package fr.esgi.robin.colorrun.servlet;

import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.repository.UtilisateurRepository;
import fr.esgi.robin.colorrun.repository.impl.UtilisateurRepositoryImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final UtilisateurRepository userRepository = new UtilisateurRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nom = req.getParameter("nom");
        String prenom = req.getParameter("prenom");
        String email = req.getParameter("email");
        String motDePasse = req.getParameter("motDePasse");

        Utilisateur userExist = userRepository.findByEmail(email);
        if (userExist != null) {
            req.setAttribute("errorMessage", "Cet email est déjà utilisé");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }

        Utilisateur newUser = new Utilisateur(null, nom, prenom, email, motDePasse, null, new Date().toInstant());
        userRepository.create(newUser);

        resp.sendRedirect("/login");
    }
}
