package fr.esgi.robin.colorrun;

import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.database.DatabaseConfig;
import fr.esgi.robin.colorrun.repository.UtilisateurRepository;
import fr.esgi.robin.colorrun.repository.impl.UtilisateurRepositoryImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "user-list", value = "/user-list")
public class UserListServlet extends HttpServlet {
    private String message = "User List Servlet Initialized";

    public void init() {
        System.out.println(message);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + message + "</h1>");

        UtilisateurRepository userRepo = new UtilisateurRepositoryImpl();
        List<Utilisateur> users = userRepo.findAll();
        out.println("<h1>List of Users</h1>");
        out.println("<table border='1'>");
        out.println("<tr><th>ID</th><th>Name</th><th>First Name</th><th>Email</th><th>Password</th></tr>");
        for (Utilisateur user : users) {
            out.println("<tr>");
            out.println("<td>" + user.getId() + "</td>");
            out.println("<td>" + user.getNom() + "</td>");
            out.println("<td>" + user.getPrenom() + "</td>");
            out.println("<td>" + user.getEmail() + "</td>");
            out.println("<td>" + user.getMotDePasse() + "</td>");
        }

        out.println("</html></body>");
    }
}
