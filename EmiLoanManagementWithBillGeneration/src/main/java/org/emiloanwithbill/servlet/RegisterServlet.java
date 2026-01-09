package org.emiloanwithbill.servlet;

import org.emiloanwithbill.dao.UserDao;
import org.emiloanwithbill.model.User;
import org.emiloanwithbill.service.UserService;
import org.emiloanwithbill.service.serviceimplementation.UserServiceImpl;
import org.emiloanwithbill.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER =
            LoggerFactory.getLogger(RegisterServlet.class);

    private final UserService userService;

    public RegisterServlet() {
        this.userService =
                new UserServiceImpl(new UserDao());
    }

    public RegisterServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp) {

        LOGGER.info("POST /register");

        try {
            User user =
                    JsonUtil.getMapper().readValue(req.getReader(), User.class);

            userService.register(user);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("User registered successfully");

        } catch (IOException e) {
            LOGGER.error("Error processing POST /register", e);
            try {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid request body");
            } catch (IOException ex) {
                LOGGER.error("Error sending error response", ex);
            }
        }
    }
}