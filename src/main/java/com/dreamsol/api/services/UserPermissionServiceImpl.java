package com.dreamsol.api.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.dreamsol.api.dto.PageResponse;
import com.dreamsol.api.dto.UserPermissionDto;
import com.dreamsol.api.entities.EndPoint;
import com.dreamsol.api.entities.UserPermission;
import com.dreamsol.api.exceptionhandler.customexceptions.NoContentFoundException;
import com.dreamsol.api.exceptionhandler.customexceptions.ResourceAlreadyExist;
import com.dreamsol.api.exceptionhandler.customexceptions.ResourceNotFoundException;
import com.dreamsol.api.repositories.EndPointRepo;
import com.dreamsol.api.repositories.UserPermissionRepo;

@Service
public class UserPermissionServiceImpl implements UserPermissionService {

    @Autowired
    UserPermissionRepo permissionRepo;
    @Autowired
    DtoUtility dtoUtility;
    @Autowired
    EndPointRepo endPointRepo;
    @Autowired
    @Qualifier("Message")
    Map<String, String> message;

    @Override
    public ResponseEntity<PageResponse> fetchAllPermissions(int pageNumber, int pageSize, String sortBy, String dir,
            String filter) {
        Sort sort = (dir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable PageData = PageRequest.of(pageNumber, pageSize, sort);
        Page<UserPermission> UserPermissionPage = permissionRepo.findByFilter(PageData, filter);
        if (UserPermissionPage.isEmpty())
            throw new NoContentFoundException();
        List<UserPermission> UserPermissions = UserPermissionPage.getContent();
        List<UserPermissionDto> dbUserPermissions = UserPermissions.stream().map((userpermission) -> {
            UserPermissionDto dto = this.dtoUtility.toUserPermissionDto(userpermission);
            return dto;
        }).collect(Collectors.toList());

        PageResponse response = new PageResponse();
        response.setContent(dbUserPermissions);
        response.setLastPage(UserPermissionPage.isLast());
        response.setPageNumber(UserPermissionPage.getNumber());
        response.setPageSize(UserPermissionPage.getSize());
        response.setTotalElements(UserPermissionPage.getTotalElements());
        response.setTotalPages(UserPermissionPage.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<UserPermissionDto> fetchPermission(int id) {
        UserPermission userPermission = this.permissionRepo.findById(id).orElseThrow(() -> {
            return new ResourceNotFoundException(id);
        });
        return ResponseEntity.status(HttpStatus.OK).body(dtoUtility.toUserPermissionDto(userPermission));
    }

    @Override
    public ResponseEntity<UserPermissionDto> addPermission(UserPermissionDto dto) throws Exception {
        UserPermission userpermission = this.dtoUtility.toUserPermission(dto);
        this.permissionRepo.findByPermission(userpermission.getPermission()).ifPresent((Existingpermission) -> {
            throw new ResourceAlreadyExist(userpermission.getPermission(), "Permission");
        });
        UserPermission dbUserPermission = this.permissionRepo.save(userpermission);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoUtility.toUserPermissionDto(dbUserPermission));
    }

    @Override
    public ResponseEntity<UserPermissionDto> updatePermission(UserPermissionDto dto, int id) {
        this.permissionRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        UserPermission userPermission = dtoUtility.toUserPermission(dto);
        userPermission.setId(id);
        UserPermission updatedUserPermission = permissionRepo.save(userPermission);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoUtility.toUserPermissionDto(updatedUserPermission));
    }

    @Override
    public ResponseEntity<Map<String, String>> deletePermission(int id) {
        UserPermission userPermission = permissionRepo.findById(id).orElseThrow(() -> {
            return new ResourceNotFoundException(id);
        });
        permissionRepo.delete(userPermission);
        message.put("message", "Permission has been deleted");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(message);
    }
    public ResponseEntity<?> addEndpoints(List<EndPoint> endpointList){
      //  List<EndPoint> endpoints=dtoUtility.toListEndPoint(endpointList);
        List<EndPoint> dbEndPoints=endPointRepo.saveAll(endpointList);
        return ResponseEntity.status(HttpStatus.OK).body(dtoUtility.toListEndPointDto(dbEndPoints));
    }
}
