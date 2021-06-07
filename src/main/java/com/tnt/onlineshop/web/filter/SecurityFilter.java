package com.tnt.onlineshop.web.filter;

import com.tnt.onlineshop.entity.Session;
import com.tnt.onlineshop.service.SessionService;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class SecurityFilter implements Filter {

    private final SessionService sessionService;

    public SecurityFilter(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String method = httpServletRequest.getMethod();
        String uri = httpServletRequest.getRequestURI();

        if (uri.startsWith("/products") && !"GET".equals(method)) {
            Optional<String> token = Arrays.stream(httpServletRequest.getCookies())
                    .filter(cookie -> "user-token".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findAny();
            if (token.isEmpty()) {
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } else {
                Optional<Session> session = sessionService.getByToken(token.get());
                if (session.isEmpty()) {
                    httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                } else {
                    chain.doFilter(httpServletRequest, httpServletResponse);
                }
            }
        } else {
            chain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

}
