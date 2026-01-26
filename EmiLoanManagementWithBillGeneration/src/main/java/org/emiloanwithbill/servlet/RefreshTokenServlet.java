package org.emiloanwithbill.servlet;

import org.emiloanwithbill.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/auth/refresh")
public class RefreshTokenServlet extends HttpServlet {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(RefreshTokenServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        try {
            String authHeader = req.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing refresh token");
                return;
            }

            String refreshToken = authHeader.substring(7);

            var claims = JwtUtil.validateToken(refreshToken).getPayload();

            long userId = Long.parseLong(claims.getSubject());
            String username = claims.get("username", String.class);

            LOGGER.info("Refresh token validated for userId={}", userId);


            String newAccessToken = JwtUtil.generateAccessToken(
                    userId, username, null
            );

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            resp.getWriter().write(
                    "{ \"accessToken\": \"" + newAccessToken + "\" }"
            );

        } catch (Exception e) {
            LOGGER.error("Invalid refresh token: {}", e.getMessage());
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired refresh token");
        }
    }
}
