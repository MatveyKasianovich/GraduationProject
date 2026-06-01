package dev.sorokin.eventmanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id",nullable = false)
    private Long id;

    @Column(name = "user_login", unique = true)
    private String login;

    @Column(name = "user_password")  // Это поле ДОЛЖНО быть
    private String password;


    @Column(name = "user_age")
    private int age;

    @Column(name = "user_role")
    private String role;


    public UserEntity() {
    }



    // Конструктор со всеми полями
    public UserEntity(Long id,String login, String password, int age, String role) {
        this.id=id;
        this.login = login;
        this.role = role;
        this.age = age;
        this.password = password;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getPassword() { return password; }  // Геттер для пароля
    public void setPassword(String password) { this.password = password; }  // Сеттер для пароля
}