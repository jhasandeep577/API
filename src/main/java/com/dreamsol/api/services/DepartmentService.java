package com.dreamsol.api.services;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.dreamsol.api.dto.DepartmentDto;
import com.dreamsol.api.dto.DepartmentUserDto;
import com.dreamsol.api.dto.DeptExcelDto;
import com.dreamsol.api.dto.PageResponse;

@Service
public interface DepartmentService {

    ResponseEntity<PageResponse> fetchAllDepartments(int pageNumber, int pageSize, String sortBy, String dir,
            String filter);

    ResponseEntity<PageResponse> fetchAllDepartmentsWithUsers(int pageNumber, int pageSize, String sortBy, String dir,
            String filter);

    ResponseEntity<DepartmentUserDto> fetchDepartmentWithUsers(int id);

    ResponseEntity<DepartmentDto> fetchDepartment(int id);

    ResponseEntity<DepartmentDto> addDepartment(DepartmentDto department) throws Exception;

    ResponseEntity<DepartmentDto> updateDepartment(DepartmentDto department, int id);

    ResponseEntity<Map<String, String>> deleteDepartment(int id);

    ResponseEntity<?> saveExcelData(List<DeptExcelDto> dto);

    ResponseEntity<Resource> getExcelSheet(String keyword) throws Exception;

}
