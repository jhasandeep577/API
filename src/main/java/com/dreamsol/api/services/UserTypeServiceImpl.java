package com.dreamsol.api.services;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.dreamsol.api.dto.PageResponse;
import com.dreamsol.api.dto.UserTypeDto;
import com.dreamsol.api.dto.UserTypeUserDto;
import com.dreamsol.api.dto.UsertypeExcelDto;
import com.dreamsol.api.entities.UserType;
import com.dreamsol.api.exceptionhandler.customexceptions.NoContentFoundException;
import com.dreamsol.api.exceptionhandler.customexceptions.ResourceAlreadyExist;
import com.dreamsol.api.exceptionhandler.customexceptions.ResourceNotFoundException;
import com.dreamsol.api.repositories.UserTypeRepo;

@Service
public class UserTypeServiceImpl implements UserTypeService {
    @Autowired
    RepoUtility repoUtility;
    @Autowired
    UserTypeRepo userTypeRepo;
    @Autowired
    DtoUtility dtoUtility;
    @Autowired
    @Qualifier("Message")
    Map<String, String> message;
    @Autowired
    FileService fileService;

    public ResponseEntity<PageResponse> fetchAllUserTypesWithUsers(int pageNumber, int pageSize, String sortBy,
            String dir,
            String filter) {

        Sort sort = (dir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable PageData = PageRequest.of(pageNumber, pageSize, sort);
        Page<UserType> UserTypePage = userTypeRepo.findByFilter(PageData, filter);
        if (UserTypePage.isEmpty())
            throw new NoContentFoundException();
        List<UserType> UserTypes = UserTypePage.getContent();
        List<UserTypeUserDto> dbUserTypes = UserTypes.stream().map((usertype) -> {
            UserTypeUserDto dto = this.dtoUtility.toUserTypeUserDto(usertype);
            return dto;
        }).collect(Collectors.toList());

        PageResponse response = new PageResponse();
        response.setContent(dbUserTypes);
        response.setLastPage(UserTypePage.isLast());
        response.setPageNumber(UserTypePage.getNumber());
        response.setPageSize(UserTypePage.getSize());
        response.setTotalElements(UserTypePage.getTotalElements());
        response.setTotalPages(UserTypePage.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<PageResponse> fetchAllUserTypes(int pageNumber, int pageSize, String sortBy,
            String dir,
            String filter) {

        Sort sort = (dir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable PageData = PageRequest.of(pageNumber, pageSize, sort);
        Page<UserType> UserTypePage = userTypeRepo.findByFilter(PageData, filter);
        if (UserTypePage.isEmpty())
            throw new NoContentFoundException();
        List<UserType> UserTypes = UserTypePage.getContent();
        List<UserTypeDto> dbUserTypes = UserTypes.stream().map((usertype) -> {
            UserTypeDto dto = this.dtoUtility.toUserTypeDto(usertype);
            return dto;
        }).collect(Collectors.toList());

        PageResponse response = new PageResponse();
        response.setContent(dbUserTypes);
        response.setLastPage(UserTypePage.isLast());
        response.setPageNumber(UserTypePage.getNumber());
        response.setPageSize(UserTypePage.getSize());
        response.setTotalElements(UserTypePage.getTotalElements());
        response.setTotalPages(UserTypePage.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<UserTypeDto> fetchUserType(int id) {
        UserType usertype = this.userTypeRepo.findById(id).orElseThrow(() -> {
            return new ResourceNotFoundException(id);
        });
        return ResponseEntity.status(HttpStatus.OK).body(dtoUtility.toUserTypeDto(usertype));
    }

    public ResponseEntity<UserTypeUserDto> fetchUserTypeWithUsers(int id) {
        UserType usertype = this.userTypeRepo.findById(id).orElseThrow(() -> {
            return new ResourceNotFoundException(id);
        });
        return ResponseEntity.status(HttpStatus.OK).body(dtoUtility.toUserTypeUserDto(usertype));
    }

    public ResponseEntity<UserTypeDto> addUserType(UserTypeDto userTypeDto) {
        UserType userType = this.dtoUtility.toUserType(userTypeDto);
        this.userTypeRepo.findByUserTypeName(userType.getUserTypeName()).ifPresent((Existingusertype) -> {
            throw new ResourceAlreadyExist(userType.getUserTypeName(), "UserType");
        });
        UserType dbUserType = this.userTypeRepo.save(userType);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoUtility.toUserTypeDto(dbUserType));
    }

    public ResponseEntity<UserTypeDto> updateUserType(UserTypeDto userTypeDto, int id) {
        this.userTypeRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        UserType usertype = dtoUtility.toUserType(userTypeDto);
        usertype.setId(id);
        UserType updatedUserType = userTypeRepo.save(usertype);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoUtility.toUserTypeDto(updatedUserType));

    }

    public ResponseEntity<Map<String, String>> deleteUserType(int id) {
        UserType userType = userTypeRepo.findById(id).orElseThrow(() -> {
            return new ResourceNotFoundException(id);
        });
        userTypeRepo.delete(userType);
        message.put("message", "UserType has been deleted");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(message);
    }

    public ResponseEntity<?> saveExcelData(List<UsertypeExcelDto> dto) {
        List<UsertypeExcelDto> validDto = new ArrayList<>();
        List<UsertypeExcelDto> invalidDto = new ArrayList<>();
        dto.stream().forEach((usertype) -> {
            if (dtoUtility.validateDtoBool(usertype) == false) {
                usertype.setMessages(dtoUtility.validateDto(usertype));
                usertype.setStatus(false);
                invalidDto.add(usertype);
            } else {
                validDto.add(usertype);
            }
        });
        List<UserType> usertypes = validDto.stream()
                .map(dtoUtility::toUserType)
                .collect(Collectors.toList());
        List<UserType> validUserTypes = new ArrayList<>();
        List<UserType> existingUserTypes = new ArrayList<>();
        usertypes.stream()
                .forEach(usertype -> {
                    this.userTypeRepo.findByUserTypeName(usertype.getUserTypeName())
                            .ifPresentOrElse((dbUserType) -> {
                                existingUserTypes.add(dbUserType);
                            }, () -> {
                                validUserTypes.add(usertype);
                            });
                });
        List<UserType> dbUserTypes = this.userTypeRepo.saveAll(validUserTypes);
        List<UserTypeDto> invalidResponse = existingUserTypes.stream().map(dtoUtility::toUserTypeDto)
                .collect(Collectors.toList());
        List<UserTypeDto> dbUsertypeResponse = dbUserTypes.stream()
                .map(dtoUtility::toUserTypeDto)
                .collect(Collectors.toList());
        Map<String, List<?>> reposneMap = new HashMap<>();
        if (!dbUsertypeResponse.isEmpty()) {
            reposneMap.put("Added User-Type-List", dbUsertypeResponse);
        }
        if (!invalidResponse.isEmpty()) {
            reposneMap.put("Already Existing-UserTypes", invalidResponse);
        }
        if (!invalidDto.isEmpty()) {
            reposneMap.put("Invalid-UserTypes", invalidDto);
        }
        if (dbUsertypeResponse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(reposneMap);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(reposneMap);

    }

    public ResponseEntity<Resource> getExcelSheet(String keyword) throws Exception {

        List<UserType> userTypes = this.repoUtility.SearchUserType(keyword);
        List<UserTypeDto> userTypesdto = userTypes.stream().map(dtoUtility::toUserTypeDto)
                .collect(Collectors.toList());
        String fileName = "UserTypeData.xlsx";
        ByteArrayInputStream actualData = fileService.getExcelData(userTypesdto, UserTypeDto.class);
        InputStreamResource file = new InputStreamResource(actualData);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName
                        + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

}
