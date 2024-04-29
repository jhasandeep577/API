package com.dreamsol.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtRequest {
     @NotBlank(message = "Username cannot be Empty")
     @Email(message = "Not a Valid username")
     @Size(max = 50, message = "Length of username cannot be more than 50")
     private String username;
     @NotBlank(message = "Password cannot be Empty")
     @Size(max = 50,min = 3, message = "Length of password cannot be more than 10 and more than 50")
     private String password;
}
