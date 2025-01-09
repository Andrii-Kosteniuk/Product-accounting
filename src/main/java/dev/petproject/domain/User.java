package dev.petproject.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Integer id;

    @Column(nullable = false)
    @NotEmpty(message = "User first name can not be empty")
    @Pattern(regexp = "^[A-Z][a-z]*$", message = "Name must start with a capital letter followed by one or more lowercase letters")
    private String firstName;

    @Column(nullable = false)
    @NotEmpty(message = "User last name can not be empty")
    @Pattern(regexp = "^[A-Z][a-z]*$", message = "Last name must start with a capital letter followed by one or more lowercase letters")
    private String lastName;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}", message = "Should be a valid email")
    private String email;

    @Column(nullable = false)
    @Pattern(regexp = "(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}",
            message = "Must be minimum 6 characters, at least one letter and one number")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
        return UserDetails.super.isEnabled();
    }
}