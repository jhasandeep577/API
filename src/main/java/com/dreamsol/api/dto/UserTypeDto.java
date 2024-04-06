package com.dreamsol.api.dto;

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
    @Size(min = 3, max = 20, message = "UserTypeName must be greater than 3 and less than 20")
    private String UserTypeName;

    public String toString() {
        return UserTypeName;
    }
}
