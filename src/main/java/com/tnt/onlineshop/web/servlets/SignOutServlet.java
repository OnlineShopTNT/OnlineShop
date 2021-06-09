package com.tnt.onlineshop.web.servlets;

import com.tnt.onlineshop.service.SessionService;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Optional;

public class SignOutServlet extends HttpServlet {

    private final SessionService sessionService;

    public SignOutServlet(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            Optional<String> optionalUserToken = Arrays.stream(request.getCookies())
                    .filter(cookie -> "user-token".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findAny();
            if (optionalUserToken.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                if (sessionService.delete(optionalUserToken.get())) {
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                }
            }
        } catch (NullPointerException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

}
