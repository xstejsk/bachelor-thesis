package com.example.sportsreservationsystembackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;

/**
 * This class represents refresh token entity
 * @author Radim Stejskal
 */

@Entity
@Table(name = "refresh_token")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"user"})
@Getter
@ToString
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 1000)
    private String token;

    private LocalDateTime usedAt = null;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime expiresAt;

    @ManyToOne
    @JoinColumn(
            name = "user_id"
    )
    private AppUser user;

    public RefreshToken setToken(String token) {
        this.token = token;
        return this;
    }

    public RefreshToken setUser(AppUser user) {
        this.user = user;
        return this;
    }

    public RefreshToken setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
        return this;
    }

    public RefreshToken setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
