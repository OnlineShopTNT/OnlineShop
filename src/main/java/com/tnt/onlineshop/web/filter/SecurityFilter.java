package com.tnt.onlineshop.web.filter;

import com.tnt.onlineshop.entity.Session;
import com.tnt.onlineshop.service.SessionService;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class SecurityFilter implements Filter {

    private final SessionService sessionService;

    public SecurityFilter(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String method = httpServletRequest.getMethod();
        String uri = httpServletRequest.getRequestURI();
        //TODO: add session-only, no-session-only.

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
