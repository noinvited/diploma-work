package ru.journal.homework.aggregator.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.journal.homework.aggregator.domain.helperEntity.Role;
import ru.journal.homework.aggregator.domain.helperEntity.Status;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@Entity
@Table(name = "users", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "users_login_key", columnNames = {"login"})
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "authorities", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled"})
public class User implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('users_user_id_seq'::regclass)")
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Size(max = 50)
    @NotBlank(message = "Login cannot be empty")
    @Column(name = "login", nullable = false, length = 50)
    private String username;

    @Size(max = 255)
    @NotBlank(message = "Password cannot be empty")
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 255)
    @Column(name = "activation_code")
    private String activationCode;

    @Size(max = 255)
    @Column(name = "email")
    @Email(message = "Email is not correct")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @Size(max = 127)
    @Column(name = "fio", length = 127)
    private String fio;

    @Past(message = "Birthdate must be in past")
    @Column(name = "birthdate")
    private LocalDate birthdate;

    @Column(name = "status", length = 50)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "role", length = 50)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "active")
    private Boolean active;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(role);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }

    public boolean isActive() {
        return active;
    }

    public boolean isAdmin(){
        return role.getAuthority().equals("ADMIN");
    }

    public int isStudent(){
        if (status == null) {
            return 0;
        } else if (Status.STUDENT == status) {
            return 1;
        } else {
            return 2;
        }
    }
}