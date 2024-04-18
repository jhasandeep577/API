package com.dreamsol.api.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dreamsol.api.dto.ExcelDataResponseDto;
import com.dreamsol.api.dto.PageResponse;
import com.dreamsol.api.dto.UserDto;
import com.dreamsol.api.services.DtoUtility;
import com.dreamsol.api.services.FileService;
import com.dreamsol.api.services.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "User Controller", description = "To Perform Operation On User")
@RequestMapping("/User")

public class UserController {
    @Value("${project.image}")
    String path;
    @Autowired
    UserService User_service;
    @Autowired
    FileService file_service;
    @Autowired
    DtoUtility utility;
    
    @GetMapping(path = "/fetch-all-Users")
    public ResponseEntity<PageResponse> getAllUser(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "name", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String dir,
            @RequestParam(value = "filter", defaultValue = "", required = false) String filter) {
        return User_service.fetchAllUser(pageNumber, pageSize, sortBy, dir, filter, path);
    }

    @GetMapping(path = "/fetch-User/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable int id) {
        return User_service.fetchUser(id, path);
    }

    @PutMapping(path = "update-User/{id}")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestPart("UserDto") UserDto user,
            @RequestParam("image") MultipartFile file, @PathVariable int id) throws Exception {
        return User_service.updateUser(user, id, path, file);
    }

    @DeleteMapping(path = "delete-User/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable int id) {
        return User_service.deleteUser(path, id);
    }

    @GetMapping(path = "/download/{fileName}")
    public ResponseEntity<Resource> downloadfile(@PathVariable String fileName) throws IOException {
        return this.User_service.getFile(fileName, path);
    }

    @PostMapping(path = "validate-Excel-Data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> validateExcelData(@RequestParam("excel") MultipartFile exceldata,
            @RequestParam("EntityName") String EntityName) {
        return this.User_service.validateExcelData(exceldata, EntityName);
    }

    @PostMapping(path = "save-User-Excel-Data")
    public ResponseEntity<?> saveExcelData(@RequestBody List<ExcelDataResponseDto> listExcelData) {
        return this.User_service.saveExcelData(listExcelData);
    }

    @GetMapping(path = "download-User-Excel-Sheet")
    public ResponseEntity<Resource> downloadExcelSheet(
            @RequestParam(value = "keyword", required = false) String keyword) throws Exception {
        return this.User_service.getExcelSheet(keyword);
    }

    @GetMapping(path = "download-Excel-Format")
    public ResponseEntity<?> downloadExcelFormat(@RequestParam("EntityName") String EntityName) throws Exception {
        return this.User_service.downloadExcelFormat(EntityName);
    }
}
