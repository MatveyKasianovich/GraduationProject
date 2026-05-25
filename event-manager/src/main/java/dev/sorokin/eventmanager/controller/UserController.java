package dev.sorokin.eventmanager.controller;


import dev.sorokin.eventmanager.dto.UserDTO;

import dev.sorokin.eventmanager.entityToBusinnes.User;

import dev.sorokin.eventmanager.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

   private final UserService userService;
   private final Logger log= LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<UserDTO> registerUser(@RequestBody SignUpRequest signUpRequest){
        log.info("Get request for sign-u,login=%s".formatted(signUpRequest.login()));
        User user=userService.registerUser(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserDTO(user.getId(),user.getLogin(),user.getAge(),user.getRole()));
    }
}
