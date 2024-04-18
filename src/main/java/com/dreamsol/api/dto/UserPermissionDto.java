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
public class UserPermissionDto {
    @NotBlank(message = "Permission of User cannot be Empty")
    @Pattern(regexp = "^[a-zA-Z]+([\s][a-zA-Z]+)*$", message = "Only Alphabets are allowed in Permission")
    @Size(min = 3, max = 10, message = "UserTypeName must be greater than 3 and less than 10")
    private String permission;

    public String toString() {
        return permission;
    }
}
