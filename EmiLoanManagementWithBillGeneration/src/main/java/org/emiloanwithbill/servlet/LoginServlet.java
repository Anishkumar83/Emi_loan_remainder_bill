package org.emiloanwithbill.servlet;

import org.emiloanwithbill.dao.UserDao;
import org.emiloanwithbill.model.User;
import org.emiloanwithbill.service.UserService;
import org.emiloanwithbill.service.serviceimplementation.UserServiceImpl;
import org.emiloanwithbill.util.JsonUtil;
import org.emiloanwithbill.util.JwtUtil;

import javax.servlet.http.*;
import java.io.IOException;

public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserServiceImpl(new UserDao());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {

            User loginReq = JsonUtil.getMapper().readValue(req.getReader(), User.class);


            User dbUser = userService.login(loginReq.getUsername(), loginReq.getPassword());

            if (dbUser == null) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid username or password");
                return;
            }

            long userId = dbUser.getId();
            String role = dbUser.getRole();
            String username = dbUser.getUsername();


            String accessToken = JwtUtil.generateAccessToken(userId, username, role);
            String refreshToken = JwtUtil.generateRefreshToken(userId, username);


            HttpSession session = req.getSession(true);
            session.setAttribute("userId", userId);
            session.setAttribute("role", role);
            session.setMaxInactiveInterval(3600);


            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            resp.getWriter().write(
                    "{\n" +
                            "  \"message\": \"Login successful\",\n" +
                            "  \"accessToken\": \"" + accessToken + "\",\n" +
                            "  \"refreshToken\": \"" + refreshToken + "\"\n" +
                            "}"
            );

        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request format");
        }
    }
}
