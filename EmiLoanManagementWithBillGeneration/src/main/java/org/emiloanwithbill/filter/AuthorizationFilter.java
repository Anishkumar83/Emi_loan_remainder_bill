package org.emiloanwithbill.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(
        filterName = "AuthorizationFilter",
        urlPatterns = {
                "/customer",
                "/loan/*",
                "/loan/details"
        }
)
public class AuthorizationFilter implements Filter {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AuthorizationFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI();
        String role = (String) request.getAttribute("role");

        LOGGER.debug("AuthorizationFilter triggered for path: {} with role: {}", path, role);

        if (role == null) {
            LOGGER.warn("Authorization failed: No role attribute found in request (AuthenticationFilter missing?)");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

            if (!role.equals("ADMIN")) {
                LOGGER.warn("Unauthorized access attempt by role '{}' to ADMIN endpoint {}", role, path);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required");
                return;
            }



        LOGGER.debug("Access granted for role {} on {}", role, path);

        chain.doFilter(req, res);
    }
}
