package org.emiloanwithbill.servlet;

import org.emiloanwithbill.service.EmiService;
import org.emiloanwithbill.service.serviceimplementation.EmiServiceImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class EmiPaidServlet extends HttpServlet {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(EmiPaidServlet.class);

    private final EmiService emiService = new EmiServiceImplementation();

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        try {
            long userId = (long) req.getAttribute("userId");

            String emiIdParam = req.getParameter("emiId");
            String status = req.getParameter("status");

            if (emiIdParam == null || status == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Missing emiId or status");
                return;
            }

            long emiId = Long.parseLong(emiIdParam);

            LOGGER.info("User {} updating EMI {} to {}", userId, emiId, status);

            emiService.updateStatus(emiId, status);

            resp.getWriter().write("EMI status updated successfully");

        } catch (Exception e) {
            LOGGER.error("Error updating EMI", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to update EMI status");
        }
    }
}
