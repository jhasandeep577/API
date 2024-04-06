package com.dreamsol.api.services;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.dreamsol.api.dto.DepartmentDto;
import com.dreamsol.api.dto.DepartmentUserDto;
import com.dreamsol.api.dto.DeptExcelDto;
import com.dreamsol.api.dto.PageResponse;
import com.dreamsol.api.entities.Department;
import com.dreamsol.api.exceptionhandler.customexceptions.NoContentFoundException;
import com.dreamsol.api.exceptionhandler.customexceptions.ResourceAlreadyExist;
import com.dreamsol.api.exceptionhandler.customexceptions.ResourceNotFoundException;
import com.dreamsol.api.repositories.DepartmentRepo;

@Service
public class DepartmentServiceIml implements DepartmentService {
    @Autowired
    FileService fileService;
    @Autowired
    RepoUtility repoUtility;
    @Autowired
    DepartmentRepo departmentRepo;
    @Autowired
    DtoUtility dtoUtility;
    @Autowired
    @Qualifier("Message")
    Map<String, String> message;
    @Autowired
    @Qualifier("DepartmentMap")
    Map<String, String> DepartmentMap;

    public ResponseEntity<PageResponse> fetchAllDepartments(int pageNumber, int pageSize, String sortBy,
            String dir, String filter) {

        Sort sort = (dir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable PageData = PageRequest.of(pageNumber, pageSize, sort);
        Page<Department> departmentPage = departmentRepo.findByFilter(PageData, filter);
        if (departmentPage.isEmpty())
            throw new NoContentFoundException();
        List<Department> departments = departmentPage.getContent();
        List<DepartmentDto> dbDepartments = departments.stream().map((department) -> {
            DepartmentDto dto = this.dtoUtility.toDepartmentDto(department);
            return dto;
        }).collect(Collectors.toList());

        PageResponse response = new PageResponse();
        response.setContent(dbDepartments);
        response.setLastPage(departmentPage.isLast());
        response.setPageNumber(departmentPage.getNumber());
        response.setPageSize(departmentPage.getSize());
        response.setTotalElements(departmentPage.getTotalElements());
        response.setTotalPages(departmentPage.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<PageResponse> fetchAllDepartmentsWithUsers(int pageNumber, int pageSize, String sortBy,
            String dir, String filter) {

        Sort sort = (dir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable PageData = PageRequest.of(pageNumber, pageSize, sort);
        Page<Department> departmentPage = departmentRepo.findByFilter(PageData, filter);
        if (departmentPage.isEmpty())
            throw new NoContentFoundException();
        List<Department> departments = departmentPage.getContent();
        List<DepartmentUserDto> dbDepartments = departments.stream().map((department) -> {
            DepartmentUserDto dto = this.dtoUtility.toDepartmentUserDto(department);
            return dto;
        }).collect(Collectors.toList());

        PageResponse response = new PageResponse();
        response.setContent(dbDepartments);
        response.setLastPage(departmentPage.isLast());
        response.setPageNumber(departmentPage.getNumber());
        response.setPageSize(departmentPage.getSize());
        response.setTotalElements(departmentPage.getTotalElements());
        response.setTotalPages(departmentPage.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<DepartmentUserDto> fetchDepartmentWithUsers(int id) {
        Department dbDepartment = this.departmentRepo.findById(id).orElseThrow(() -> {
            return new ResourceNotFoundException(id);
        });
        return ResponseEntity.status(HttpStatus.OK).body(dtoUtility.toDepartmentUserDto(dbDepartment));
    }

    public ResponseEntity<DepartmentDto> fetchDepartment(int id) {
        Department dbDepartment = this.departmentRepo.findById(id).orElseThrow(() -> {
            return new ResourceNotFoundException(id);
        });
        return ResponseEntity.status(HttpStatus.OK).body(dtoUtility.toDepartmentDto(dbDepartment));
    }

    public ResponseEntity<DepartmentDto> addDepartment(DepartmentDto departmentdto) throws Exception {
        Department department = this.dtoUtility.toDepartment(departmentdto);
        this.departmentRepo.findByDepartmentCode(departmentdto.getDepartmentCode())
                .ifPresent((alreadyExistingDepartment) -> {
                    throw new ResourceAlreadyExist(departmentdto.getDepartmentCode(), "Department-Code");
                });
        this.departmentRepo.findByDepartmentName(departmentdto.getDepartmentName())
                .ifPresent((alreadyExistingDepartment) -> {
                    throw new ResourceAlreadyExist(departmentdto.getDepartmentName(), "Department-Name");
                });
        Department dbdepartment = this.departmentRepo.save(department);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoUtility.toDepartmentDto(dbdepartment));
    }

    public ResponseEntity<DepartmentDto> updateDepartment(DepartmentDto department, int dep_id) {
        this.departmentRepo.findById(dep_id).orElseThrow(() -> new ResourceNotFoundException(dep_id));
        Department newDepartment = dtoUtility.toDepartment(department);
        newDepartment.setId(dep_id);
        Department updatedDepartment = this.departmentRepo.save(newDepartment);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoUtility.toDepartmentDto(updatedDepartment));
    }

    public ResponseEntity<Map<String, String>> deleteDepartment(int id) {
        Department department = departmentRepo.findById(id).orElseThrow(() -> {
            return new ResourceNotFoundException(id);
        });
        departmentRepo.delete(department);
        message.put("message", "Department Data has been deleted");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(message);
    }

    public ResponseEntity<?> saveExcelData(List<DeptExcelDto> dto) {
        List<Department> departments = dto.stream()
                .map(dtoUtility::toDepartment)
                .collect(Collectors.toList());
        List<Department> validDepartments = departments.stream()
                .map(department -> {
                    this.departmentRepo.findByDepartmentCode(department.getDepartmentCode())
                            .ifPresent(existingDept -> department.setId(existingDept.getId()));
                    return department;
                }).collect(Collectors.toList());
        List<Department> dbDepartments = this.departmentRepo.saveAll(validDepartments);
        List<DepartmentDto> dbDepartmentResponse = dbDepartments.stream()
                .map(dtoUtility::toDepartmentDto)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(dbDepartmentResponse);
    }

    public ResponseEntity<Resource> getExcelSheet(String keyword) throws Exception {

        List<Department> departments = this.repoUtility.SearchDepartment(keyword);
        List<DepartmentDto> departmentdto = departments.stream().map(dtoUtility::toDepartmentDto)
                .collect(Collectors.toList());
        String fileName = "DepartmentData.xlsx";
        ByteArrayInputStream actualData = fileService.getExcelData(departmentdto, DepartmentDto.class);
        InputStreamResource file = new InputStreamResource(actualData);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName
                        + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }
}
