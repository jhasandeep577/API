package com.dreamsol.api.services;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.dreamsol.api.dto.PageResponse;
import com.dreamsol.api.dto.UserPermissionDto;
import com.dreamsol.api.entities.EndPoint;

@Service
public interface UserPermissionService {
    ResponseEntity<PageResponse> fetchAllPermissions(int pageNumber, int pageSize, String sortBy, String dir,
            String filter);

    ResponseEntity<UserPermissionDto> fetchPermission(int id);

    ResponseEntity<UserPermissionDto> addPermission(UserPermissionDto department) throws Exception;

    ResponseEntity<UserPermissionDto> updatePermission(UserPermissionDto department, int id);

    ResponseEntity<Map<String, String>> deletePermission(int id);

    ResponseEntity<?> addEndpoints(List<EndPoint> endpointList);

}
