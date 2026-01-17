package org.emiloanwithbill.servlet;

import org.emiloanwithbill.dao.UserDao;
import org.emiloanwithbill.model.User;
import org.emiloanwithbill.util.JsonUtil;
import org.emiloanwithbill.util.PasswordUtil;
import org.emiloanwithbill.util.TokenService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/login", asyncSupported = true)
public class LoginServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = JsonUtil.getMapper().readValue(req.getReader(), User.class);
        User dbUser = userDao.findByUsername(user.getUsername());


        if (dbUser == null ||
                !PasswordUtil.verifyPassword(user.getPassword(), dbUser.getPassword())) {

            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
            return;
        }


        var asyncContext = req.startAsync();
        asyncContext.setTimeout(5000);

        TokenService.generateTokenAsync(dbUser.getUsername())
                .thenAccept(token -> {
                    try {
                        HttpServletResponse asyncResp =
                                (HttpServletResponse) asyncContext.getResponse();

                        asyncResp.setContentType("application/json");
                        asyncResp.setHeader("Authorization", "Bearer " + token);
                        asyncResp.getWriter()
                                .write("{\"message\":\"login successful\"}");

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        asyncContext.complete();
                    }
                });
    }
}
