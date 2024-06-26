package com.dreamsol.api.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentUserDto {
    private String departmentName;
    private int departmentCode;
    private Set<UserDto> users;
}
