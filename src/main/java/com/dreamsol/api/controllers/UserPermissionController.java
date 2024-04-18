package com.dreamsol.api.controllers;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;

import com.dreamsol.api.dto.PageResponse;
import com.dreamsol.api.dto.UserPermissionDto;
import com.dreamsol.api.services.DtoUtility;
import com.dreamsol.api.services.FileService;
import com.dreamsol.api.services.UserPermissionService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@Tag(name = "User-Permission Controller", description = "To Perform Operations On Permissions")
@AllArgsConstructor(onConstructor_ = { @Autowired })
@RequestMapping("/User-Permission")
@PreAuthorize("hasAuthority('Admin')")
public class UserPermissionController {

    UserPermissionService service;
    FileService file_service;
    DtoUtility utility;
 //   @PreAuthorize("hasAuthority('Admin')")
    @GetMapping(path = "/fetch-all-Permissions")
    public ResponseEntity<PageResponse> getAllPermissions(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "permission", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String dir,
            @RequestParam(value = "filter", defaultValue = "", required = false) String filter) {
        return service.fetchAllPermissions(pageNumber, pageSize, sortBy, dir, filter);
    }

    @GetMapping(path = "/fetch-Permission/{id}")
    public ResponseEntity<UserPermissionDto> getPermission(@PathVariable int id) {
        return service.fetchPermission(id);
    }

    @PostMapping(path = "add-Permission")
    public ResponseEntity<UserPermissionDto> createPermission(@Valid @RequestBody UserPermissionDto permission)
            throws Exception {
        return service.addPermission(permission);
    }

    @PutMapping(path = "update-Permission/{id}")
    public ResponseEntity<UserPermissionDto> updatePermission(@Valid @RequestBody UserPermissionDto dto,
            @PathVariable int id) throws IOException {
        return service.updatePermission(dto, id);
    }

    @DeleteMapping(path = "delete-Permission/{id}")
    public ResponseEntity<Map<String, String>> deletePermission(@PathVariable int id) {
        return service.deletePermission(id);
    }
}