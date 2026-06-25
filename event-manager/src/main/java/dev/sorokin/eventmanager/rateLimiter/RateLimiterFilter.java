package dev.sorokin.eventmanager.rateLimiter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.util.RateLimiter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimiterFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;
    private static final int DEFAULT_LIMIT = 10;
    private static final Duration DURATION_WINDOW = Duration.ofMinutes(1);


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String client = Optional.ofNullable(request.getHeader("X-API-KEY"))
                .filter(s -> !s.isEmpty())
                .orElseGet(() -> Optional.ofNullable(request.getRemoteAddr())
                        .orElse("unknown"));


        boolean allowed = rateLimiterService.isAllowed(
                client,
                DEFAULT_LIMIT,
                DURATION_WINDOW
        );


        if (!allowed) {
            log.warn("Rate limit exceeded for client: {}", client);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate limit exceeded for client: " + client);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
