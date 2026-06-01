package dev.sorokin.eventmanager.mapper;

import dev.sorokin.eventmanager.data.Role;
import dev.sorokin.eventmanager.dto.UserDTO;
import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.entityToBusinnes.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUserFromDto(UserDTO userDTO) {

        return new User(
                userDTO.getId(),
                userDTO.getLogin(),
                userDTO.getAge(),
                userDTO.getRole()
        );
    }


    public UserDTO toUserDtoFromUser(User user) {

        return new UserDTO(
                user.getId(),
                user.getLogin(),
                user.getAge(),
                user.getRole()
        );
    }


    public User toUserFromEntity(UserEntity entity) {

        User user = new User(
                entity.getId(),
                entity.getLogin(),
                entity.getAge(),
                Role.valueOf(entity.getRole())
        );
        user.setPassword(entity.getPassword());
        return user;
    }


    public UserEntity toEntityFromUser(User user) {

        return new UserEntity(
                user.getId(),
                user.getLogin(),
                user.getPassword(),
                user.getAge(),
                user.getRole().name()
        );
    }


    public UserDTO toUserDtoFromEntity(UserEntity entity) {

        return new UserDTO(
                entity.getId(),
                entity.getLogin(),
                entity.getAge(),
                Role.valueOf(entity.getRole())
        );
    }
}