package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findByTokenType(Token.TokenType tokenType);

    List<Token> findByUserIdAndTokenType(Long userId, Token.TokenType tokenType);

    void deleteByUserIdAndTokenType(Long userId, Token.TokenType tokenType);

    @Modifying
    @Query(value = "delete from token where (extract(epoch from :currentTime - created_at) / 60) > life_time_in_minutes", nativeQuery = true)
    void deleteExpiredTokens(@Param("currentTime") OffsetDateTime currentTime);
}
