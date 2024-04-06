package com.dreamsol.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dreamsol.api.entities.Department;

@Repository
public interface DepartmentRepo extends JpaRepository<Department, Integer> {
      public List<Department> findByDepartmentNameLike(String dep_name);

      public Optional<Department> findByDepartmentCode(int code);

      public Optional<Department> findByDepartmentName(String dep_name);

      @Query("Select d from Department d where d.departmentName LIKE %:Key%")
      public Page<Department> findByFilter(Pageable pageable, @Param("Key") String Key);

      @Query("SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.users")
      public List<Department> findAllDepartmentsWithUsers();

      @Query("SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.users u WHERE d.departmentName LIKE %:searchTerm%")
      public List<Department> findAllDepartmentsWithUsersWithFilter(@Param("searchTerm")String searchTerm);
}
