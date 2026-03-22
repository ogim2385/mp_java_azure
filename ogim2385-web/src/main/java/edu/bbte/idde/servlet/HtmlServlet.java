package edu.bbte.idde.servlet;

import edu.bbte.idde.data.exception.DataAccessException;
import edu.bbte.idde.exception.ServletErrorException;
import edu.bbte.idde.service.OrderService;
import edu.bbte.idde.service.OrderServiceImpl;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

public class HtmlServlet extends HttpServlet {
    private transient OrderService orderService;
    private transient Template template;
    private final Logger log = LoggerFactory.getLogger(HtmlServlet.class);

    @Override
    public void init() {
        orderService = new OrderServiceImpl();

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setClassLoaderForTemplateLoading(
                Thread.currentThread().getContextClassLoader(),
                "templates"
        );
        cfg.setDefaultEncoding("UTF-8");

        try {
            template = cfg.getTemplate("orders.ftlh");
        } catch (IOException e) {
            log.error("Error initializing HTMLServlet", e);
            throw new ServletErrorException("Error initializing HTMLServlet");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/html;charset=UTF-8");

        try (PrintWriter writer = resp.getWriter()) {
            var data = new java.util.HashMap<String, Object>();
            data.put("orders", orderService.getAllOrders());
            data.put("request", req);

            template.process(data, writer);
        } catch (IOException | freemarker.template.TemplateException e) {
            log.error("Template error", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (DataAccessException e) {
            log.error("Error while retrieving orders", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}