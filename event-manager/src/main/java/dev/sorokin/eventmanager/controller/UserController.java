package dev.sorokin.eventmanager.controller;


import dev.sorokin.eventmanager.dto.SignUpRequest;
import dev.sorokin.eventmanager.dto.UserDTO;

import dev.sorokin.eventmanager.entityToBusinnes.User;

import dev.sorokin.eventmanager.mapper.UserMapper;
import dev.sorokin.eventmanager.security.JwtAuthenticationService;
import dev.sorokin.eventmanager.security.JwtTokenResponse;
import dev.sorokin.eventmanager.security.SignInRequest;
import dev.sorokin.eventmanager.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

   private final UserMapper userMapper;
   private final UserService userService;
   private final JwtAuthenticationService jwtAuthenticationService;
   private final Logger log= LoggerFactory.getLogger(UserController.class);

    public UserController(UserMapper userMapper, UserService userService, JwtAuthenticationService jwtAuthenticationService) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.jwtAuthenticationService = jwtAuthenticationService;
    }

    @PostMapping()
    public ResponseEntity<UserDTO> registerUser(@RequestBody SignUpRequest signUpRequest){
        log.info("Get request for sign-u,login=%s".formatted(signUpRequest.login()));
        User user=userService.registerUser(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserDTO(user.getId(),user.getLogin(),user.getAge(),user.getRole()));
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtTokenResponse>autheticate(@RequestBody @Valid SignInRequest signInRequest){

        log.info("Get request for sign-in, login:{}",signInRequest.login());
        String token =jwtAuthenticationService.authenticateUser(signInRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new JwtTokenResponse(token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getuserById(@PathVariable Long id){

        log.info("Get request for getUserById, id:{}",id);
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toUserDtoFromUser(userService.getUserById(id)));

    }
}
