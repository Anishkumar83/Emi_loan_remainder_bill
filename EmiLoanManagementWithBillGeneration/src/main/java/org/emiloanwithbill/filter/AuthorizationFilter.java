package org.emiloanwithbill.filter;

import org.emiloanwithbill.util.TokenService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;


        if (req.getRequestURI().contains("/login")) {
            chain.doFilter(request, response);
            return;
        }


        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Token");
            return;
        }

        String token = auth.substring(7);


        AsyncContext asyncContext = request.startAsync();
        asyncContext.setTimeout(3000);


        TokenService.validateTokenAsync(token)
                .thenAccept(valid -> {
                    try {
                        HttpServletResponse asyncRes =
                                (HttpServletResponse) asyncContext.getResponse();

                        if (!valid) {
                            asyncRes.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                        } else {
                            chain.doFilter(asyncContext.getRequest(), asyncRes);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        asyncContext.complete();
                    }
                });
    }
}
