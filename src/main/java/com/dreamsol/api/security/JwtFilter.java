package com.dreamsol.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtility jwtUtility;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, java.io.IOException {
        String requestHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
            token = requestHeader.substring(7);
            username = this.verifyJwt(token);
            if (jwtUtility.getRoleFormToken(token).equalsIgnoreCase("user")) {
                int userId = jwtUtility.getIdFormToken(token);
                String[] ar = request.getRequestURL().toString().split("/");
                int pathId = -1;
                try {
                    pathId = Integer.parseInt(ar[ar.length - 1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                System.out.println(pathId + "------" + userId);
                if (pathId != -1 && pathId != userId) {
                    new CustomAccessDeniedHandler().handle(request, response,
                            new AccessDeniedException("Permission Denied"));
                }
            }
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // fetch User
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            Boolean validateToken = this.jwtUtility.validateToken(token, userDetails);
            if (validateToken) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication
                        .setDetails(new WebAuthenticationDetailsSource()
                                .buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        }
        filterChain.doFilter(request, response);
    }

    private String verifyJwt(String jwttoken) {
        String username = null;
        username = this.jwtUtility.getUsernameFormToken(jwttoken);
        return username;
    }

}
