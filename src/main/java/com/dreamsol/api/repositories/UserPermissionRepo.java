package com.dreamsol.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dreamsol.api.entities.UserPermission;

public interface UserPermissionRepo extends JpaRepository<UserPermission,Integer>{
    Optional<UserPermission> findByPermission(String permission);
    public List<UserPermission> findByPermissionLike(String permission);

    @Query("Select u from UserPermission u where u.permission LIKE %:Key%")
    public Page<UserPermission> findByFilter(Pageable pageable, @Param("Key") String Key);

    @Query("SELECT DISTINCT u FROM UserPermission u LEFT JOIN FETCH u.users")
    public List<UserPermission> findAllPermissionsWithUsers();

    @Query("SELECT DISTINCT u FROM UserPermission u LEFT JOIN FETCH u.users users WHERE u.permission LIKE %:searchTerm%")
    public List<UserPermission> findAllPermissionsWithUsersWithFilter(@Param("searchTerm")String searchTerm);
}
