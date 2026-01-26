package org.emiloanwithbill.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.emiloanwithbill.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

@WebFilter(
        filterName = "AuthenticationFilter",
        urlPatterns = {
                "/customer",
                "/loan/*",
                "/loan/details/*",
                "/emi/*",
                "/emi/status"
        }
)
public class AuthenticationFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI();
        LOGGER.debug("AuthenticationFilter triggered for path: {}", path);


        if (path.startsWith("/auth/login") || path.startsWith("/auth/refresh")) {
            LOGGER.info("Skipping authentication for public endpoint: {}", path);
            chain.doFilter(req, res);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            LOGGER.warn("Unauthorized access attempt: Missing or invalid Authorization header");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        String token = header.substring(7);

        try {
            LOGGER.debug("Validating JWT token...");
            Jws<Claims> decoded = JwtUtil.validateToken(token);
            Claims claims = decoded.getPayload();


            long jwtUserId = Long.parseLong(claims.getSubject());
            String role = claims.get("role", String.class);

            LOGGER.info("JWT validated successfully for userId={} role={}", jwtUserId, role);


            HttpSession session = request.getSession(false);
            if (session == null) {
                LOGGER.warn("Session missing for token userId={}", jwtUserId);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session expired. Please login again.");
                return;
            }

            Object sessionUserObj = session.getAttribute("userId");
            if (sessionUserObj == null) {
                LOGGER.warn("Session is missing userId attribute");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid session.");
                return;
            }

            long sessionUserId = (long) sessionUserObj;

            if (jwtUserId != sessionUserId) {
                LOGGER.error("JWT userId={} does not match session userId={}", jwtUserId, sessionUserId);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token for current session.");
                return;
            }

            LOGGER.debug("Session validated successfully for userId {}", jwtUserId);


            request.setAttribute("userId", jwtUserId);
            request.setAttribute("role", role);

            chain.doFilter(req, res);

        } catch (Exception e) {
            LOGGER.error("Token validation failed: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
        }
    }
}
