package com.dreamsol.api.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dreamsol.api.entities.Department;
import com.dreamsol.api.entities.User;
import com.dreamsol.api.entities.UserType;
import com.dreamsol.api.repositories.DepartmentRepo;
import com.dreamsol.api.repositories.UserRepository;
import com.dreamsol.api.repositories.UserTypeRepo;

@Component
public class RepoUtility {
    @Autowired
    UserRepository User_repo;
    @Autowired
    DepartmentRepo departmentRepo;
    @Autowired
    UserTypeRepo userTypeRepo;

    public List<User> SearchUser(String keyword) throws Exception {
        List<User> users = new ArrayList<User>();
        if (keyword == null || keyword == "") {
            users = this.User_repo.findAllUsersWithUsertypesAndDepartment();
        } else {
            users = this.User_repo.findAllDataWithUsertypesAndDepartmentWithFilter(keyword);
        }
        if (users.isEmpty()) {
            throw new Exception("No Data Found");
        } else {
            return users;
        }
    }
    public List<Department> SearchDepartment(String keyword) throws Exception {
        List<Department> departments = new ArrayList<Department>();
        if (keyword == null || keyword == "") {
            departments = this.departmentRepo.findAllDepartmentsWithUsers();
        } else {
            departments = this.departmentRepo.findAllDepartmentsWithUsersWithFilter(keyword);
        }
        if (departments.isEmpty()) {
            throw new Exception("No Data Found");
        } else {
            return departments;
        }
    }
    public List<UserType> SearchUserType(String keyword) throws Exception {
        List<UserType> userTypes = new ArrayList<UserType>();
        if (keyword == null || keyword == "") {
            userTypes = this.userTypeRepo.findAllUserTypesWithUsers();
        } else {
            userTypes = this.userTypeRepo.findAllUserTypesWithUsersWithFilter(keyword);
        }
        if (userTypes.isEmpty()) {
            throw new Exception("No Data Found");
        } else {
            return userTypes;
        }
    }
}
