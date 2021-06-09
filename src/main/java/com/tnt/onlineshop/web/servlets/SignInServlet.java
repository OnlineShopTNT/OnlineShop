package com.tnt.onlineshop.web.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tnt.onlineshop.entity.Session;
import com.tnt.onlineshop.entity.User;
import com.tnt.onlineshop.service.SessionService;
import com.tnt.onlineshop.service.UserService;
import com.tnt.onlineshop.util.ServiceLocator;
import org.json.JSONObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

public class SignInServlet extends HttpServlet {

    private final UserService userService = (UserService) ServiceLocator.gerServiceMap("userService");
    private final SessionService sessionService = (SessionService) ServiceLocator.gerServiceMap("sessionService");

//    public SignInServlet(UserService userService, SessionService sessionService) {
//        this.userService = userService;
//        this.sessionService = sessionService;
//    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String jsonStringResponse;
        Gson gson = new Gson();
        JsonObject credentialsJsonObject = gson.fromJson(request.getReader(), JsonObject.class);
        String email = credentialsJsonObject.get("email").getAsString();
        String password = credentialsJsonObject.get("password").getAsString();
        Optional<User> optionalUser = userService.signInCheck(email, password);
        if (optionalUser.isPresent()) {
            Optional<Session> optionalSession = sessionService.getByUser(optionalUser.get());
            if (optionalSession.isPresent()) {
                String token = optionalSession.get().getToken();
                Cookie cookie = new Cookie("user-token", token);
                cookie.setMaxAge(Integer.parseInt(ServiceLocator.PROPERTIES_READER.getProperty("cookie.max.age")));
                cookie.setHttpOnly(true);
                response.addCookie(cookie);
                response.setStatus(HttpServletResponse.SC_OK);
                jsonStringResponse = new JSONObject()
                        .put("message", "Successfully logged in!")
                        .toString();
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonStringResponse = new JSONObject()
                        .put("message", "Session can't create!")
                        .toString();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonStringResponse = new JSONObject()
                    .put("message", "Password or email is incorrect!")
                    .toString();
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(jsonStringResponse);
        out.flush();
    }
}
