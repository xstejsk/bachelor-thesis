package com.example.sportsreservationsystembackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * This class represents user token entity
 * @author Radim Stejskal
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_token")
public class UserToken {
    @Id
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    private String id;

    @NotBlank(
            message = "Token value cannot be blank"
    )
    private String token;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime expiresAt;

    @Enumerated(
            value = EnumType.STRING
    )
    @Column(nullable = false)
    private TokenType tokenType;

    private String encodedPassword;

    private LocalDateTime confirmedAt = null;

    @ManyToOne
    @JoinColumn(
            name = "user_id"
    )
    private AppUser user;

    public UserToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, TokenType tokenType, AppUser user) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.tokenType = tokenType;
        this.user = user;
    }

    public UserToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, TokenType tokenType, String encodedPassword, AppUser user) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.tokenType = tokenType;
        this.encodedPassword = encodedPassword;
        this.user = user;
    }
}
