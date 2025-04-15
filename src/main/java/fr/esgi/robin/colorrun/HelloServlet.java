package fr.esgi.robin.colorrun;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.database.DatabaseConfig;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "Hello World!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // Hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + message + "</h1>");

        System.setProperty("jdbc.drivers", "org.h2.Driver");

        try (Connection conn = DatabaseConfig.getConnection()) {
            out.println("<h1> connexion réussi </h1>");
            Object obj = conn.prepareStatement("SELECT * FROM UTILISATEURS").executeQuery();
            ResultSet rs = (ResultSet) obj;
            out.println("<h1> liste des utilisateurs </h1>");
            out.println("<table border='1'>");
            out.println("<tr><th>id</th><th>nom</th><th>prenom</th><th>email</th><th>mot de passe</th></tr>");

            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("ID_UTILISATEUR") + "</td>");
                out.println("<td>" + rs.getString("NOM") + "</td>");
                out.println("<td>" + rs.getString("PRENOM") + "</td>");
                out.println("<td>" + rs.getString("EMAIL") + "</td>");
                out.println("<td>" + rs.getString("MOT_DE_PASSE") + "</td>");
                out.println("</tr>");
            }
        } catch (SQLException e) {
            out.println("<h1> connexion échoué </h1>");
            out.println("<p>" + e.getMessage() + "</p>");
        }

        out.println("</body></html>");


    }

    public void destroy() {
    }
}