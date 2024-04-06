package com.dreamsol.api.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.dreamsol.api.dto.ExcelDataResponseDto;
import com.dreamsol.api.dto.PageResponse;
import com.dreamsol.api.dto.UserDto;
import com.dreamsol.api.entities.UserFile;

public interface UserService {
    ResponseEntity<PageResponse> fetchAllUser(int PageNumber, int PageSize, String sortBy, String sortDir,
            String filter, String path);

    ResponseEntity<UserDto> fetchUser(int id, String path);

    ResponseEntity<UserDto> addUser(UserDto user, MultipartFile file, String path) throws Exception;

    ResponseEntity<UserDto> updateUser(UserDto user, int id, String path, MultipartFile file) throws Exception;

    ResponseEntity<Map<String, String>> deleteUser(String path, int id);

    UserFile uploadfile(MultipartFile file, String path);

    ResponseEntity<Resource> getFile(String fileName, String path) throws IOException;

    ResponseEntity<Resource> getExcelSheet(String keyword) throws Exception;

    ResponseEntity<?> downloadExcelFormat(String entityName) throws Exception;
    
    ResponseEntity<?> validateExcelData(MultipartFile file,String EntityName);

    ResponseEntity<?> saveExcelData(List<ExcelDataResponseDto> dto);

}
