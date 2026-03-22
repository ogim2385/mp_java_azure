package edu.bbte.idde.web;

import edu.bbte.idde.servlet.HtmlServlet;
import edu.bbte.idde.servlet.LoginServlet;
import edu.bbte.idde.servlet.LogoutServlet;
import edu.bbte.idde.servlet.OrderServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {
    public static void main(String[] args) throws Exception {
        String portValue = System.getenv("PORT");
        if (portValue == null || portValue.isBlank()) {
            portValue = System.getenv("WEBSITES_PORT");
        }
        if (portValue == null || portValue.isBlank()) {
            portValue = System.getProperty("PORT", "8080");
        }

        int port = Integer.parseInt(portValue);
        System.out.println("Starting server on port: " + port);

        Server server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        context.addServlet(new ServletHolder(new OrderServlet()), "/orders");
        context.addServlet(new ServletHolder(new LoginServlet()), "/login");
        context.addServlet(new ServletHolder(new LogoutServlet()), "/logout");
        context.addServlet(new ServletHolder(new HtmlServlet()), "/view");

        server.setHandler(context);

        server.start();
        server.join();
    }
}