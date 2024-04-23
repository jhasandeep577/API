package com.dreamsol.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dreamsol.api.entities.EndPoint;

@Repository
public interface EndPointRepo extends JpaRepository<EndPoint,String>{
    public Optional<EndPoint> findByEndpoint(String endpoint);
}
