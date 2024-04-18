package com.dreamsol.api.dto;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class UserDto {
   @NotBlank(message = "User Name cannot be Empty")
   @Pattern(regexp = "^[a-zA-Z]+([\s][a-zA-Z]+)*$", message = "Only Alphabets are allowed in Name")
   @Size(min = 3, max = 20, message = "Enter a Valid Name")
   private String name;
   @NotNull(message = "Department Cannot be Empty")
   @Valid
   private DepartmentDto department;
   @NotNull(message = "UserType cannot be Empty")
   @Valid
   private UserTypeDto usertype;
   @Valid
   private UserPermissionDto permission;
   @Min(message = "Not Valid phone Number", value = 6000000000l)
   @Max(message = "Not Valid phone Number", value = 9999999999l)
   private long mobile;
   @NotBlank(message = "Email cannot be Empty")
   @Email(message = "Not a Valid email")
   @Size(max = 50, message = "Length of email cannot be more than 50")
   private String email;
   @Schema(hidden = true)
   private String FileUrl;
   private String password;
}
