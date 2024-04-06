package com.dreamsol.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dreamsol.api.entities.UserType;

@Repository
public interface UserTypeRepo extends JpaRepository<UserType, Integer> {
    public List<UserType> findByUserTypeNameLike(String userTypeName);

    public Optional<UserType> findByUserTypeName(String userTypeName);

    @Query("Select u from UserType u where u.userTypeName LIKE %:Key%")
    public Page<UserType> findByFilter(Pageable pageable, @Param("Key") String Key);

    @Query("SELECT DISTINCT ut FROM UserType ut LEFT JOIN FETCH ut.users")
    public List<UserType> findAllUserTypesWithUsers();

    @Query("SELECT DISTINCT ut FROM UserType ut LEFT JOIN FETCH ut.users WHERE ut.userTypeName LIKE %:searchTerm%")
    public List<UserType> findAllUserTypesWithUsersWithFilter(@Param("searchTerm")String searchTerm);

}
