package com.dreamsol.api.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.dreamsol.api.dto.DepartmentDto;
import com.dreamsol.api.dto.DepartmentUserDto;
import com.dreamsol.api.dto.DeptExcelDto;
import com.dreamsol.api.dto.EndPointDto;
import com.dreamsol.api.dto.ExcelDataResponseDto;
import com.dreamsol.api.dto.RefreshTokenRequest;
import com.dreamsol.api.dto.UserDto;
import com.dreamsol.api.dto.UserPermissionDto;
import com.dreamsol.api.dto.UserTypeDto;
import com.dreamsol.api.dto.UserTypeUserDto;
import com.dreamsol.api.dto.UsertypeExcelDto;
import com.dreamsol.api.entities.Department;
import com.dreamsol.api.entities.EndPoint;
import com.dreamsol.api.entities.RefreshToken;
import com.dreamsol.api.entities.User;
import com.dreamsol.api.entities.UserFile;
import com.dreamsol.api.entities.UserPermission;
import com.dreamsol.api.entities.UserType;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

@Component
public class DtoUtility {
    @Autowired
    PasswordEncoder passwordEncoder;
    private Validator validator;

    public <T> Set<String> validateDto(T dto) {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        Set<String> failedValidations = violations.stream().map((violation) -> {
            return violation.getMessage();
        }).collect(Collectors.toSet());

        return failedValidations;
    }

    public <T> boolean validateDtoBool(T dto) {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (violations.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public Object toValidExcelDto(Object dto, String entityName) {
        switch (entityName.toLowerCase()) {
            case "user":
                return this.toExcelDataResponseDto((UserDto) dto);
            case "department":
                return this.toDeptExcelDto((DepartmentDto) dto);
            case "usertype":
                return this.toUserTypeExcelDto((UserTypeDto) dto);
            default:
                throw new IllegalArgumentException("Enter a valid entity name");
        }
    }

    public Object toInvalidExcelDto(Object dto, String entityName) {
        switch (entityName.toLowerCase()) {
            case "user":
                ExcelDataResponseDto userDto = this.toExcelDataResponseDto((UserDto) dto);
                userDto.setStatus(false);
                userDto.setMessages(this.validateDto((UserDto) dto));
                return userDto;
            case "department":
                DeptExcelDto departmentDto = this.toDeptExcelDto((DepartmentDto) dto);
                departmentDto.setStatus(false);
                departmentDto.setMessages(this.validateDto((DepartmentDto) dto));
                return departmentDto;
            case "usertype":
                UsertypeExcelDto userTypeDto = this.toUserTypeExcelDto((UserTypeDto) dto);
                userTypeDto.setStatus(false);
                userTypeDto.setMessages(this.validateDto((UserTypeDto) dto));
                return userTypeDto;
            default:
                throw new IllegalArgumentException("Enter a valid entity name");
        }
    }

    public UserTypeUserDto toUserTypeUserDto(UserType usertype) {
        UserTypeUserDto dto = new UserTypeUserDto();
        BeanUtils.copyProperties(usertype, dto);
        Set<UserDto> usersdto = usertype.getUsers().stream().map((user) -> {
            return this.toUserDto(user);
        }).collect(Collectors.toSet());
        dto.setUsers(usersdto);
        return dto;
    }

    public UserType toUserType(UserTypeDto dto) {
        UserType usertype = new UserType();
        BeanUtils.copyProperties(dto, usertype);
        return usertype;
    }

    public UserTypeDto toUserTypeDto(UserType user) {
        UserTypeDto usertypedto = new UserTypeDto();
        BeanUtils.copyProperties(user, usertypedto);
        return usertypedto;
    }

    public Department toDepartment(DepartmentDto dto) {
        Department department = new Department();
        BeanUtils.copyProperties(dto, department);
        return department;
    }

    public DepartmentDto toDepartmentDto(Department department) {
        DepartmentDto dto = new DepartmentDto();
        BeanUtils.copyProperties(department, dto);
        return dto;
    }

    public DepartmentUserDto toDepartmentUserDto(Department department) {
        DepartmentUserDto dto = new DepartmentUserDto();
        BeanUtils.copyProperties(department, dto);
        Set<UserDto> users = department.getUsers().stream().map((user) -> {
            return this.toUserDto(user);
        }).collect(Collectors.toSet());
        dto.setUsers(users);
        return dto;
    }

    public User toUser(UserDto dto) {
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setDepartment(this.toDepartment(dto.getDepartment()));
        user.setUsertype(this.toUserType(dto.getUsertype()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPermission(this.toUserPermission(dto.getPermission()));
        // user.setAuthorizedEndpoints(this.toListEndPoint(dto.getEndPoints()));
        return user;
    }

    public RefreshTokenRequest toRefreshTokenRequest(RefreshToken token) {
        RefreshTokenRequest refreshToken = new RefreshTokenRequest();
        BeanUtils.copyProperties(token, refreshToken);
        return refreshToken;
    }

    public RefreshToken toRefreshToken(RefreshTokenRequest refreshToken) {
        RefreshToken token = new RefreshToken();
        BeanUtils.copyProperties(refreshToken, token);
        return token;
    }

    public UserDto toUserDto(User user) {
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(user, dto);
        if (user.getFile() != null) {
            dto.setFileUrl(toUrl(user.getFile()));
        }
        if (user.getDepartment() != null) {
            dto.setDepartment(this.toDepartmentDto(user.getDepartment()));
        }
        if (user.getUsertype() != null) {
            UserTypeDto typeDto=this.toUserTypeDto(user.getUsertype());
             typeDto.setEndointKeys(user.getAuthorizedEndpoints().stream().map(endpoint->endpoint.getId()).toList());
            dto.setUsertype(typeDto);
        }
        if (user.getPermission() != null) {
            dto.setPermission(this.toUserPermissionDto(user.getPermission()));
        }
        // if (user.getAuthorizedEndpoints() != null) {
        //     List<String> endpoints=user.getAuthorizedEndpoints().stream().map(endpoint->endpoint.getId()).collect(Collectors.toList());
        //     dto.setAccessibleEndPoints(endpoints);
        // }
        return dto;
    }

    public ExcelDataResponseDto toExcelDataResponseDto(UserDto userdto) {
        ExcelDataResponseDto exceldto = new ExcelDataResponseDto();
        BeanUtils.copyProperties(userdto, exceldto);
        return exceldto;
    }

    public DeptExcelDto toDeptExcelDto(DepartmentDto deptDto) {
        DeptExcelDto exceldto = new DeptExcelDto();
        BeanUtils.copyProperties(deptDto, exceldto);
        return exceldto;
    }

    public Department toDepartment(DeptExcelDto dto) {
        Department department = new Department();
        BeanUtils.copyProperties(dto, department);
        return department;
    }

    public UsertypeExcelDto toUserTypeExcelDto(UserTypeDto deptDto) {
        UsertypeExcelDto exceldto = new UsertypeExcelDto();
        BeanUtils.copyProperties(deptDto, exceldto);
        return exceldto;
    }

    public UserType toUserType(UsertypeExcelDto dto) {
        UserType usertype = new UserType();
        BeanUtils.copyProperties(dto, usertype);
        return usertype;
    }

    public UserPermission toUserPermission(UserPermissionDto dto) {
        UserPermission permission = new UserPermission();
        BeanUtils.copyProperties(dto, permission);
        return permission;
    }

    public UserPermissionDto toUserPermissionDto(UserPermission permission) {
        UserPermissionDto dto = new UserPermissionDto();
        BeanUtils.copyProperties(permission, dto);
        return dto;
    }

    public User toUser(ExcelDataResponseDto dto) {
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setDepartment(this.toDepartment(dto.getDepartment()));
        user.setUsertype(this.toUserType(dto.getUsertype()));
        return user;
    }

    public EndPoint toEndPoint(EndPointDto dto) {
        EndPoint endpoint = new EndPoint();
        BeanUtils.copyProperties(dto, endpoint);
        return endpoint;
    }

    public EndPointDto tEndPointDto(EndPoint endPoint) {
        EndPointDto dto = new EndPointDto();
        BeanUtils.copyProperties(endPoint, dto);
        return dto;
    }

    public List<EndPoint> toListEndPoint(List<EndPointDto> dtos) {
        List<EndPoint> endpoints = dtos.stream().map(endpointdto -> this.toEndPoint(endpointdto))
                .collect(Collectors.toList());
        return endpoints;
    }

    public List<EndPointDto> toListEndPointDto(List<EndPoint> points) {
        List<EndPointDto> endpointsDto = points.stream().map(endpoint -> this.tEndPointDto(endpoint))
                .collect(Collectors.toList());
        return endpointsDto;
    }

    public String toUrl(UserFile file) {
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/User/download/")
                .path("" + file.getGeneratedFileName())
                .toUriString();
        return url;
    }
}
