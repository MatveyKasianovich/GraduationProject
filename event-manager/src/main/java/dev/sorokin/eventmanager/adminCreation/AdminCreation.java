package dev.sorokin.eventmanager.adminCreation;


import dev.sorokin.eventmanager.user.Role;
import dev.sorokin.eventmanager.user.UserEntity;
import dev.sorokin.eventmanager.user.UserRepository;
import dev.sorokin.eventmanager.user.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminCreation {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AdminCreation(UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void adminCreation(){

        if(userRepository.existsUserEntityByLogin("admin")){
            return;
        }

        UserEntity adminTosave=(new UserEntity(null,
                "admin",
                passwordEncoder.encode("admin"),
                19,
                Role.ADMIN.name()));
        userRepository.save(adminTosave);
    }
}
