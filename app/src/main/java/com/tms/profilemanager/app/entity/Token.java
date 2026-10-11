package com.tms.profilemanager.app.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.time.OffsetDateTime;

import static javax.persistence.GenerationType.SEQUENCE;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Token {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "tokenSeq")
    @SequenceGenerator(name = "tokenSeq", sequenceName = "token_seq", allocationSize = 1)
    @Column(nullable = false, unique = true)
    Long id;

    @Column(nullable = false)
    Long userId;

    @Column(nullable = false)
    String tokenHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TokenType tokenType;

    @Column(nullable = false)
    OffsetDateTime createdAt;

    @Column(nullable = false)
    Integer lifeTimeInMinutes;

    public enum TokenType {
        PASSWORD_RESET
    }
}
