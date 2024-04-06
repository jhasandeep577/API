package com.dreamsol.api.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTypeUserDto {
     private String UserTypeName;
     private Set<UserDto> users;
}
