package com.dreamsol.api.services;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.dreamsol.api.dto.PageResponse;
import com.dreamsol.api.dto.UserTypeDto;
import com.dreamsol.api.dto.UserTypeUserDto;
import com.dreamsol.api.dto.UsertypeExcelDto;

@Service
public interface UserTypeService {

    ResponseEntity<PageResponse> fetchAllUserTypes(int pageNumber, int pageSize, String sortBy, String dir,
            String filter);

    ResponseEntity<PageResponse> fetchAllUserTypesWithUsers(int pageNumber, int pageSize, String sortBy, String dir,
            String filter);

    ResponseEntity<UserTypeUserDto> fetchUserTypeWithUsers(int id);

    ResponseEntity<UserTypeDto> fetchUserType(int id);

    ResponseEntity<UserTypeDto> addUserType(UserTypeDto userTypeDto);

    ResponseEntity<UserTypeDto> updateUserType(UserTypeDto userTypeDto, int id);

    ResponseEntity<Map<String, String>> deleteUserType(int id);

    ResponseEntity<?> saveExcelData(List<UsertypeExcelDto> dto);

    ResponseEntity<Resource> getExcelSheet(String keyword) throws Exception;
}
