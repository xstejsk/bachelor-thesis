package com.example.sportsreservationsystembackend.model;

import com.sun.istack.NotNull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class represents user entity
 * @author Radim Stejskal
 */

@Entity
@Table(name = "app_user")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"reservations", "confirmationTokens","refreshTokens"})
@EqualsAndHashCode(exclude = {"reservations", "confirmationTokens"})
@NoArgsConstructor
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    @Column(unique = true)
    private String email;

    @Column(name = "balance")
    private int balance;

    @NotNull
    private String password;

    @Column(name = "has_daily_discount")
    private boolean hasDailyDiscount = false;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Reservation> reservations;
    private Boolean locked = false;
    private Boolean enabled = false;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserToken> confirmationTokens;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user", fetch = FetchType.LAZY)
    private List<RefreshToken> refreshTokens;

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
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getName());
        return Collections.singletonList(authority);
    }

    public UserRole getRole() {
        return role;
    }
}
