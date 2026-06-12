package dev.sorokin.eventmanager.security;

import dev.sorokin.eventmanager.user.UserEntity;
import dev.sorokin.eventmanager.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenManager jwtTokenManager;
    private final UserRepository userRepository;

    public JwtAuthenticationService(AuthenticationManager authenticationManager,
                                    JwtTokenManager jwtTokenManager,
                                    UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenManager = jwtTokenManager;
        this.userRepository = userRepository;
    }

    public String authenticateUser(SignInRequest signInRequest) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.login(),
                        signInRequest.password()
                )
        );

        UserEntity user = userRepository.findByLogin(signInRequest.login())
                .orElseThrow(() -> new RuntimeException("User not found"));


        return jwtTokenManager.generateToken(
                user.getId(),
                user.getLogin(),
                user.getRole()
        );
    }
}