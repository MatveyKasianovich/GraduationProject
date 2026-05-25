package dev.sorokin.eventmanager.service;


import dev.sorokin.eventmanager.Role;
import dev.sorokin.eventmanager.controller.SignUpRequest;
import dev.sorokin.eventmanager.dto.UserDTO;
import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.entity.UserRepository;
import dev.sorokin.eventmanager.entityToBusinnes.User;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Service
public class UserService {

    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(SignUpRequest signUpRequest){
        if(userRepository.existsUserEntityByLogin(signUpRequest.login())){
            throw new IllegalArgumentException("User with login=%s already esists".formatted(signUpRequest.login()));
        }

        UserEntity userToSave=new UserEntity(null,
                signUpRequest.login(),
                signUpRequest.password(),
                signUpRequest.age(),
                Role.USER.name());
        userRepository.save(userToSave);
        return new User(
                userToSave.getId(),
                userToSave.getLogin(),
                userToSave.getAge(),
                Role.valueOf(userToSave.getRole()));

    }
}
