package com.allaboutevemirolive.sb_auth_jwt_blacklist.config.jwt;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.allaboutevemirolive.sb_auth_jwt_blacklist.service.TokenBlacklistService;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    // @Autowired
    // private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (tokenBlacklistService.isTokenBlacklisted(token)) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    return;
                }

                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.extractUsername(token);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username,
                            null, new ArrayList<>());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            // handlerExceptionResolver.resolveException(request, response, null, exception);
        }

    }
}
