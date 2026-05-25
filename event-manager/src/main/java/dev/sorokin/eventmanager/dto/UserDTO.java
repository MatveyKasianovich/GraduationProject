package dev.sorokin.eventmanager.dto;

import dev.sorokin.eventmanager.Role;

public class UserDTO {

    private Long id;
    private String login;
    private int age;
    private Role role;

    public UserDTO(Long id, String login, int age, Role role) {
        this.id = id;
        this.login = login;
        this.age = age;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }


}
