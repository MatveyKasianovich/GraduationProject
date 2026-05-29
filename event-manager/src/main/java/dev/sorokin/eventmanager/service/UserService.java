package dev.sorokin.eventmanager.service;


import dev.sorokin.eventmanager.data.Role;
import dev.sorokin.eventmanager.dto.SignUpRequest;
import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.entity.UserRepository;
import dev.sorokin.eventmanager.entityToBusinnes.User;
import dev.sorokin.eventmanager.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper mapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }


    public User registerUser(SignUpRequest signUpRequest){
        if(userRepository.existsUserEntityByLogin(signUpRequest.login())){
            throw new IllegalArgumentException("User with login=%s already esists".formatted(signUpRequest.login()));
        }

       String hashPass=passwordEncoder.encode(signUpRequest.password());
        UserEntity userToSave=new UserEntity(null,
                signUpRequest.login(),
                hashPass,
                signUpRequest.age(),
                Role.USER.name());
        userRepository.save(userToSave);
        return new User(
                userToSave.getId(),
                userToSave.getLogin(),
                userToSave.getAge(),
                Role.valueOf(userToSave.getRole()));

    }

    public User getUserById(Long id) {
        return mapper.toUserFromEntity(userRepository.findById(id).orElseThrow(()->new EntityNotFoundException("User not found")));
    }

    public User findByLogin(String login) {
        return mapper.toUserFromEntity(userRepository.findByLogin(login).orElseThrow(()->new EntityNotFoundException("User not found")));
    }
}
