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

import com.dreamsol.api.dto.PageResponse;
import com.dreamsol.api.dto.UserTypeDto;
import com.dreamsol.api.dto.UserTypeUserDto;
import com.dreamsol.api.dto.UsertypeExcelDto;
import com.dreamsol.api.services.DtoUtility;
import com.dreamsol.api.services.FileService;
import com.dreamsol.api.services.UserTypeService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@Tag(name = "UserType Controller", description = "To Perform Operations On UserType")
@AllArgsConstructor(onConstructor_ = { @Autowired })
@RequestMapping("/Dreamsol")
public class UserTypeController {
    UserTypeService userTypeService;
    FileService file_service;
    DtoUtility utility;

    @GetMapping(path = "/fetch-all-UserTypes")
    public ResponseEntity<PageResponse> getAllUserTypes(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "userTypeName", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String dir,
            @RequestParam(value = "filter", defaultValue = "", required = false) String filter) {
        return userTypeService.fetchAllUserTypes(pageNumber, pageSize, sortBy, dir, filter);
    }

    @GetMapping(path = "/fetch-all-UserTypes-With-Users")
    public ResponseEntity<PageResponse> getAllUserTypesWithUsers(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "userTypeName", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String dir,
            @RequestParam(value = "filter", defaultValue = "", required = false) String filter) {
        return userTypeService.fetchAllUserTypesWithUsers(pageNumber, pageSize, sortBy, dir, filter);
    }

    @GetMapping(path = "/fetch-UserType/{id}")
    public ResponseEntity<UserTypeDto> getUserType(@PathVariable int id) {
        return userTypeService.fetchUserType(id);
    }

    @GetMapping(path = "/fetch-UserTypes-With-Users/{id}")
    public ResponseEntity<UserTypeUserDto> getUserTypeWithUsers(@PathVariable int id) {
        return userTypeService.fetchUserTypeWithUsers(id);
    }

    @PostMapping(path = "create-UserType")
    public ResponseEntity<UserTypeDto> createUserType(@Valid @RequestBody UserTypeDto userTypeDto)
            throws IOException {
        return userTypeService.addUserType(userTypeDto);
    }

    @PutMapping(path = "update-UserType/{id}")
    public ResponseEntity<UserTypeDto> updateUserType(@Valid @RequestBody UserTypeDto userTypeDto,
            @PathVariable int id) throws IOException {
        return userTypeService.updateUserType(userTypeDto, id);
    }

    @DeleteMapping(path = "delete-UserType/{id}")
    public ResponseEntity<Map<String, String>> deleteUserType(@PathVariable int id) {
        return userTypeService.deleteUserType(id);
    }

    @PostMapping(path = "save-UserType-Excel-Data")
    public ResponseEntity<?> saveExcelData(@RequestBody List<UsertypeExcelDto> listExcelData) {
        return userTypeService.saveExcelData(listExcelData);
    }

    @GetMapping(path = "download-UserType--Excel-Sheet")
    public ResponseEntity<Resource> downloadExcelSheet(
            @RequestParam(value = "keyword", required = false) String keyword) throws Exception {
        return this.userTypeService.getExcelSheet(keyword);
    }
}
