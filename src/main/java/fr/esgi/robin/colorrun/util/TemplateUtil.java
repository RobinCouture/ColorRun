package fr.esgi.robin.colorrun.util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.util.Map;

public class TemplateUtil {
    public static void processTemplate(String templateName, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        TemplateEngine templateEngine = (TemplateEngine) req.getServletContext().getAttribute("templateEngine");
        JakartaServletWebApplication application = (JakartaServletWebApplication) req.getServletContext()
                .getAttribute("thymeleaf.application");

        WebContext context = new WebContext(application.buildExchange(req, resp));
        context.setVariable("contextPath", req.getContextPath());

        resp.setContentType("text/html;charset=UTF-8");
        templateEngine.process(templateName, context, resp.getWriter());
    }

    public static void processTemplate(String templateName, HttpServletRequest req, HttpServletResponse resp,
            Map<String, Object> additionalVariables) throws ServletException, IOException {
        TemplateEngine templateEngine = (TemplateEngine) req.getServletContext().getAttribute("templateEngine");
        JakartaServletWebApplication application = (JakartaServletWebApplication) req.getServletContext()
                .getAttribute("thymeleaf.application");

        WebContext context = new WebContext(application.buildExchange(req, resp));
        context.setVariable("contextPath", req.getContextPath());

        if (additionalVariables != null)
            additionalVariables.forEach(context::setVariable);

        resp.setContentType("text/html;charset=UTF-8");
        templateEngine.process(templateName, context, resp.getWriter());
    }
}
