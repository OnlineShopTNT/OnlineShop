package com.tnt.onlineshop.web.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tnt.onlineshop.entity.User;
import com.tnt.onlineshop.json.JsonConverter;
import com.tnt.onlineshop.service.UserService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

public class SignInServlet extends HttpServlet {

    private UserService userService;

    public SignInServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonConverter jsonConverter = new JsonConverter();
        PrintWriter out = response.getWriter();
        String jsonStringResponse;
        Gson gson = new Gson();
        JsonObject credentialsJsonObject = gson.fromJson(request.getReader(), JsonObject.class);
        String email = credentialsJsonObject.get("email").getAsString();
        String password = credentialsJsonObject.get("password").getAsString();
        Optional<User> optionalUser = userService.signInCheck(email, password);
        if (optionalUser.isPresent()) {
            response.setStatus(HttpServletResponse.SC_OK);
            jsonStringResponse = new JSONObject()
                    .put("message", "Verify is Ok!")
                    .toString();
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
