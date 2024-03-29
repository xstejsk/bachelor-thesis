package com.rstejskalprojects.reservationsystem.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String username;
    @NotNull
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRoleEnum userRole;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Reservation> reservations;
    private Boolean locked = false;
    private Boolean enabled = false;
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user", fetch = FetchType.LAZY)
    private List<PasswordToken> passwordTokens;
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user", fetch = FetchType.LAZY)
    private List<RegistrationToken> registrationTokens;


    public AppUser(String firstName, String lastName, String username, String password, UserRoleEnum userRole) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.userRole = userRole;
        this.email = username;
    }

    public AppUser(Long id, String firstName, String lastName, String email, String username, String password, UserRoleEnum userRole, Boolean locked, Boolean enabled) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.userRole = userRole;
        this.locked = locked;
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", userRole=" + userRole +
                ", locked=" + locked +
                ", enabled=" + enabled +
                '}';
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole.getName());
        return Collections.singletonList(authority);
    }

    public UserRoleEnum getUserRole() {
        return userRole;
    }
}
