package fr.esgi.robin.colorrun.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class WebConfig implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        context.setInitParameter("org.thymeleaf.servlet.resource.resolver", "org.thymeleaf.servlet.resource.ClassLoaderResourceResolver");
    }
}
