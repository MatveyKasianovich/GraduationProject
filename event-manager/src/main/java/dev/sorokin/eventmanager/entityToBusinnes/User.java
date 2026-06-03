package dev.sorokin.eventmanager.entityToBusinnes;

import dev.sorokin.eventmanager.data.Role;

public class User {

    private Long id;
    private String login;
    private String password;
    private int age;
    private Role role;


    public User(Long id, String login, int age, Role role) {
        this.id=id;
        this.login = login;
        this.age = age;
        this.role = role;
    }

    public User(String login, int age, Role role) {
        this.role = role;
        this.age = age;
        this.login = login;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}