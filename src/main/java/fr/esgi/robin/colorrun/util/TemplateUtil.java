package fr.esgi.robin.colorrun.util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.Map;

public class TemplateUtil {
    public static void processTemplate(String templateName, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateEngine templateEngine = (TemplateEngine) req.getServletContext().getAttribute("templateEngine");

        Context context = new Context();
        context.setVariable("contextPath", req.getContextPath());

        templateEngine.process(templateName, context, resp.getWriter());
    }

    public static void processTemplate(String templateName, HttpServletRequest req, HttpServletResponse resp, Map<String, Object> additionalVariables) throws ServletException, IOException {
        TemplateEngine templateEngine = (TemplateEngine) req.getServletContext().getAttribute("templateEngine");

        Context context = new Context();
        context.setVariable("contextPath", req.getContextPath());

        if (additionalVariables != null) additionalVariables.forEach(context::setVariable);

        templateEngine.process(templateName, context, resp.getWriter());
    }
}
