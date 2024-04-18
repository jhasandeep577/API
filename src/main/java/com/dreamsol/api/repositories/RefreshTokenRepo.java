package com.dreamsol.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dreamsol.api.entities.RefreshToken;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken,Integer>{
    Optional<RefreshToken> findByRefreshToken(String token);
    Optional<RefreshToken> findByUserEmail(String mail);
}
