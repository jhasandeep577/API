package com.dreamsol.api.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dreamsol.api.dto.DepartmentDto;
import com.dreamsol.api.dto.DepartmentUserDto;
import com.dreamsol.api.dto.DeptExcelDto;
import com.dreamsol.api.dto.PageResponse;
import com.dreamsol.api.services.DepartmentService;
import com.dreamsol.api.services.DtoUtility;
import com.dreamsol.api.services.FileService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@Tag(name = "Department Controller", description = "To Perform Operations On Department")
@AllArgsConstructor(onConstructor_ = { @Autowired })
@RequestMapping("/Department")
public class DepartmentController {

    DepartmentService DepartmentService;
    FileService file_service;
    DtoUtility utility;

    @GetMapping(path = "/fetch-all-Departments-with-Users")
    public ResponseEntity<PageResponse> getAllDepartments(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "departmentName", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String dir,
            @RequestParam(value = "filter", defaultValue = "", required = false) String filter) {
        return DepartmentService.fetchAllDepartmentsWithUsers(pageNumber, pageSize, sortBy, dir, filter);
    }

    @GetMapping(path = "/fetch-all-Departments")
    public ResponseEntity<PageResponse> getAllDepartment(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "departmentName", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String dir,
            @RequestParam(value = "filter", defaultValue = "", required = false) String filter) {
        return DepartmentService.fetchAllDepartments(pageNumber, pageSize, sortBy, dir, filter);
    }

    @GetMapping(path = "/fetch-Department/{id}")
    public ResponseEntity<DepartmentDto> getDepartment(@PathVariable int id) {
        return DepartmentService.fetchDepartment(id);
    }

    @GetMapping(path = "/fetch-Department-with-Users/{id}")
    public ResponseEntity<DepartmentUserDto> getDepartmentWithUsers(@PathVariable int id) {
        return DepartmentService.fetchDepartmentWithUsers(id);
    }

    @PostMapping(path = "create-Department")
    public ResponseEntity<DepartmentDto> createDepartment(@Valid @RequestBody DepartmentDto department)
            throws Exception {
        return DepartmentService.addDepartment(department);
    }

    @PutMapping(path = "update-Department/{id}")
    public ResponseEntity<DepartmentDto> updateDepartment(@Valid @RequestBody DepartmentDto department,
            @PathVariable int id) throws IOException {
        return DepartmentService.updateDepartment(department, id);
    }

    @DeleteMapping(path = "delete-Department/{id}")
    public ResponseEntity<Map<String, String>> deleteDepartment(@PathVariable int id) {
        return DepartmentService.deleteDepartment(id);
    }

    @PostMapping(path = "save-Department-Excel-Data")
    public ResponseEntity<?> saveExcelData(@RequestBody List<DeptExcelDto> listExcelData) {
        return DepartmentService.saveExcelData(listExcelData);
    }

    @GetMapping(path = "download-Department-Excel-Sheet")
    public ResponseEntity<Resource> downloadExcelSheet(
            @RequestParam(value = "keyword", required = false) String keyword) throws Exception {
        return this.DepartmentService.getExcelSheet(keyword);
    }
}
