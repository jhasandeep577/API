package com.dreamsol.api.dto;

import java.util.Set;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class DeptExcelDto {
    @NotBlank(message = "Department Name cannot be Empty")
    @Pattern(regexp = "^[a-zA-Z]+([\s][a-zA-Z]+)*$", message = "Only Alphabets are allowed in DepartmentName")
    @Size(min = 3, max = 20, message = "Department Name must be greater than 3 and less than 20")
    private String departmentName;
    @Max(value = 9999999, message = "DepartmentCode must be Less than 7 Digits")
    @Min(value = 100, message = "DepartmentCode must be greater than 3 Digits")
    private int departmentCode;
    private boolean status = true;
    private Set<String> messages;
}
