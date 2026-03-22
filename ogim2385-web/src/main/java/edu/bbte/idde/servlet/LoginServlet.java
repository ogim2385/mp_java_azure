package edu.bbte.idde.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final String USER = "admin";
    private static final String PASS = "admin";
    private final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        resp.getWriter().println("""
                <form method='POST'>
                    <input name='username' placeholder='Username'><br>
                    <input name='password' type='password' placeholder='Password'><br>
                    <button type='submit'>Login</button>
                </form>
                """);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("Trying to sign in user: " + req.getParameter("username"));
        if (USER.equals(req.getParameter("username"))
                && PASS.equals(req.getParameter("password"))) {
            req.getSession().setAttribute("loggedIn", true);
            log.info(req.getParameter("username") + " logged in succesfully");
            resp.sendRedirect(req.getContextPath() + "/view");
        } else {
            log.error("Unsuccessful login");
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Wrong credentials");
        }
    }
}