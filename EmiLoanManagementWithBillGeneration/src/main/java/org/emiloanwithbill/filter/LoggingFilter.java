package org.emiloanwithbill.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(filterName = "LoggingFilter", urlPatterns = "/*")
public class LoggingFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;

        LOGGER.info(" Request Received: {} {}", request.getMethod(), request.getRequestURI());

        long startTime = System.currentTimeMillis();

        chain.doFilter(req, res);

        long duration = System.currentTimeMillis() - startTime;


        LOGGER.info("Response Sent: {} {} | Time Taken: {} ms",
                request.getMethod(), request.getRequestURI(), duration);
    }
}
