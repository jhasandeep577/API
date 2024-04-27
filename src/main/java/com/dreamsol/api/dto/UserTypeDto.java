package com.dreamsol.api.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTypeDto {
    @NotBlank(message = "UserTypeName cannot be Empty")
    @Pattern(regexp = "^[a-zA-Z]+([\s][a-zA-Z]+)*$", message = "Only Alphabets are allowed in UserTypeName")
    @Size(min = 3, max = 10, message = "UserTypeName must be greater than 3 and less than 10")
    private String UserTypeName;
    private List<String> endointKeys;

    public String toString() {
        return UserTypeName;
    }
}
