package com.tnt.onlineshop.web.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tnt.onlineshop.service.UserService;
import com.tnt.onlineshop.util.ServiceLocator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;

public class SignUpServlet extends HttpServlet {

    private final UserService userService = (UserService) ServiceLocator.gerServiceMap("userService");

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final Gson gson = new Gson();
        Reader reader = request.getReader();
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
        boolean isAdded = userService.add(jsonObject.get("email").getAsString(), jsonObject.get("password").getAsString());
        if (isAdded) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

}
