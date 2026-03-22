package edu.bbte.idde.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/view")
public class AuthFilter extends HttpFilter {
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        Boolean loggedIn = (Boolean) request.getSession().getAttribute("loggedIn");
        if (Boolean.TRUE.equals(loggedIn)) {
            chain.doFilter(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
}