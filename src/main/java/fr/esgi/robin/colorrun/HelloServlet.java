package fr.esgi.robin.colorrun;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;

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
            //conn.prepareStatement("SELECT * FROM ").executeQuery();
        } catch (SQLException e) {
            out.println("<h1> connexion échoué </h1>");
            out.println("<p>" + e.getMessage() + "</p>");
        }

        out.println("</body></html>");


    }

    public void destroy() {
    }
}