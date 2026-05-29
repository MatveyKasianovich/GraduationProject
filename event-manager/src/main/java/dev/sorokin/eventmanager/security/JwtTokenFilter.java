package dev.sorokin.eventmanager.security;


import dev.sorokin.eventmanager.entityToBusinnes.User;
import dev.sorokin.eventmanager.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenManager jwtTokenManager;
    private final UserService userService;

    public JwtTokenFilter(JwtTokenManager jwtTokenManager, @Lazy UserService userService) {
        this.jwtTokenManager = jwtTokenManager;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader=request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authorizationHeader==null || !authorizationHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        String jwtToken=authorizationHeader.substring(7);

        String loginFromToken;
        try {
            loginFromToken=jwtTokenManager.getLoginFromJwt(jwtToken);
        } catch (Exception e){
            logger.error("Error while reading jwt",e);
            filterChain.doFilter(request,response);
            return;
        }

        User user = userService.findByLogin(loginFromToken);

        UsernamePasswordAuthenticationToken token=new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority(user.getRole().toString()))
        );
        SecurityContextHolder.getContext().setAuthentication(token);
        filterChain.doFilter(request,response);
    }
}
