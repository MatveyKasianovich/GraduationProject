package dev.sorokin.eventmanager.entityToBusinnes;

import dev.sorokin.eventmanager.data.Role;

public class User {

    private Long id;
    private String login;
    private String password;  // ЭТО ПОЛЕ ДОЛЖНО БЫТЬ!
    private int age;
    private Role role;

    // Конструктор с паролем
    public User(Long id, String login, int age, Role role) {
        this.id=id;
        this.login = login;
        this.password = password;
        this.age = age;
        this.role = role;
    }

    public User(String login, int age, String password, Role role) {
        this.role = role;
        this.age = age;
        this.password = password;
        this.login = login;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }  // Геттер для пароля
    public void setPassword(String password) { this.password = password; }  // Сеттер для пароля

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}