package org.emiloanwithbill.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(LogoutServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        LOGGER.info("POST /logout");

        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
            LOGGER.info("User session invalidated");
        }

        LOGGER.info("Using stateless JWT → token will expire automatically");

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("Logged out successfully");
    }
}
