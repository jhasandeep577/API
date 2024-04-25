package com.dreamsol.api.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.dreamsol.api.dto.DepartmentDto;
import com.dreamsol.api.dto.ExcelDataResponseDto;
import com.dreamsol.api.dto.PageResponse;
import com.dreamsol.api.dto.UserDto;
import com.dreamsol.api.dto.UserTypeDto;
import com.dreamsol.api.entities.EndPoint;
import com.dreamsol.api.entities.User;
import com.dreamsol.api.entities.UserFile;
import com.dreamsol.api.exceptionhandler.customexceptions.NoContentFoundException;
import com.dreamsol.api.exceptionhandler.customexceptions.ResourceAlreadyExist;
import com.dreamsol.api.exceptionhandler.customexceptions.ResourceNotFoundException;
import com.dreamsol.api.repositories.DepartmentRepo;
import com.dreamsol.api.repositories.EndPointRepo;
import com.dreamsol.api.repositories.UserFileRepo;
import com.dreamsol.api.repositories.UserPermissionRepo;
import com.dreamsol.api.repositories.UserRepository;
import com.dreamsol.api.repositories.UserTypeRepo;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserPermissionRepo userPermissionRepo;
    @Autowired
    UserFileRepo userFileRepo;
    @Autowired
    DepartmentRepo departmentRepo;
    @Autowired
    UserTypeRepo userTypeRepo;
    @Autowired
    DtoUtility dtoUtility;
    @Autowired
    UserRepository User_repo;
    @Autowired
    @Qualifier("Message")
    Map<String, String> message;
    @Autowired
    FileService fileService;
    @Autowired
    RepoUtility repoUtil;
    @Autowired
    @Qualifier("UserMap")
    Map<String, String> UserMap;
    @Autowired
    @Qualifier("DepartmentMap")
    Map<String, String> DepartmentMap;
    @Autowired
    @Qualifier("UserTypeMap")
    Map<String, String> UserTypeMap;
    @Autowired
    EndPointUtility endPointUtility;
    @Autowired
    EndPointRepo endPointRepo;

    public ResponseEntity<PageResponse> fetchAllUser(int PageNumber, int PageSize, String SortBy, String sortDir,
            String filter, String path) {

        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(SortBy).ascending() : Sort.by(SortBy).descending();
        Pageable PageData = PageRequest.of(PageNumber, PageSize, sort);
        Page<User> userPage = this.User_repo.findByFilter(PageData, filter);
        if (userPage.isEmpty())
            throw new NoContentFoundException();
        List<User> users = userPage.getContent();
        List<UserDto> dbUsers = users.stream().map((user) -> {
            UserDto dtouser = this.dtoUtility.toUserDto(user);
            return dtouser;
        }).collect(Collectors.toList());

        PageResponse response = new PageResponse();
        response.setContent(dbUsers);
        response.setLastPage(userPage.isLast());
        response.setPageNumber(userPage.getNumber());
        response.setPageSize(userPage.getSize());
        response.setTotalElements(userPage.getTotalElements());
        response.setTotalPages(userPage.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<UserDto> fetchUser(int id, String path) {

        User dbUser = this.User_repo.findById(id).orElseThrow(() -> {
            return new ResourceNotFoundException(id);
        });
        UserDto dtouser = this.dtoUtility.toUserDto(dbUser);
        return ResponseEntity.status(HttpStatus.OK).body(dtouser);
    }

    public ResponseEntity<UserDto> addUser(UserDto user, MultipartFile file, String path) throws Exception {
        User dbuser = this.dtoUtility.toUser(user);
        if (this.User_repo.findByMobile(dbuser.getMobile()).isPresent()) {
            throw new ResourceAlreadyExist(dbuser.getMobile(), "Mobile Number");
        } else if (this.User_repo.findByEmail(dbuser.getEmail()).isPresent()) {
            throw new ResourceAlreadyExist(dbuser.getEmail(), "Email");
        } else if (this.departmentRepo.findByDepartmentCode(user.getDepartment().getDepartmentCode()).isPresent()) {
            dbuser.setDepartment(
                    this.departmentRepo.findByDepartmentCode(user.getDepartment().getDepartmentCode()).get());
            if (this.userTypeRepo.findByUserTypeName(user.getUsertype().getUserTypeName()).isPresent()) {
                dbuser.setUsertype(this.userTypeRepo.findByUserTypeName(user.getUsertype().getUserTypeName()).get());

                if (this.userPermissionRepo.findByPermission(user.getPermission().getPermission()).isPresent()) {
                    dbuser.setPermission(
                            this.userPermissionRepo.findByPermission(user.getPermission().getPermission()).get());
                    dbuser.setFile(uploadfile(file, path));
        // ^ Saving file on server and getting its randomly generated name
                    String[] endpoints = endPointUtility.getAuthorizedUrls(
                            List.of(dbuser.getUsertype().getUserTypeName(), dbuser.getPermission().getPermission()));
        // Wokring on Endpoints DB Logic
                    List<EndPoint> listEndPoints = new ArrayList<>();
                    for (int x = 0; x < endpoints.length; x++) {
                        if (endPointRepo.findByEndpoint(endpoints[x]).isPresent()) {
                            listEndPoints.add((endPointRepo.findByEndpoint(endpoints[x])).get());
                        }
                    }
                    if (listEndPoints != null) {
                        dbuser.setAuthorizedEndpoints(listEndPoints);
                    }
                    User NewDbUser = this.User_repo.save(dbuser);
                    UserDto dtouser = this.dtoUtility.toUserDto(NewDbUser);
                    return ResponseEntity.status(HttpStatus.CREATED).body(dtouser);
                } else {
                    throw new Exception("Provide a Valid UserPermission");
                }
            } else {
                throw new Exception("Provide a Valid UserType");
            }
        } else {
            throw new Exception("Provide a Valid Department");
        }
    }

    public ResponseEntity<UserDto> updateUser(UserDto user, int id, String path, MultipartFile file)
            throws Exception {
        User oldUser = this.User_repo.findById(id).orElseThrow(() -> {
            return new ResourceNotFoundException(id);
        });

        User dbuser = this.dtoUtility.toUser(user);
        dbuser.setID(oldUser.getID());
        if (this.User_repo.findByMobile(dbuser.getMobile()).isPresent()) {
            throw new ResourceAlreadyExist(dbuser.getMobile(), "Mobile Number");
        } else if (this.User_repo.findByEmail(dbuser.getEmail()).isPresent()) {
            throw new ResourceAlreadyExist(dbuser.getEmail(), "Email");
        } else if (this.departmentRepo.findByDepartmentCode(user.getDepartment().getDepartmentCode()).isPresent()) {
            if (this.userTypeRepo.findByUserTypeName(user.getUsertype().getUserTypeName()).isPresent()) {
                if (this.userPermissionRepo.findByPermission(user.getPermission().getPermission()).isPresent()) {
                    dbuser.setFile(uploadfile(file, path));
                    // ^ Saving file on server and getting its randomly generated name
                    User NewDbUser = this.User_repo.save(dbuser);
                    UserDto dtouser = this.dtoUtility.toUserDto(NewDbUser);
                    return ResponseEntity.status(HttpStatus.CREATED).body(dtouser);
                } else {
                    throw new Exception("Provide a valid UserPermission");
                }
            } else {
                throw new Exception("Provide a Valid UserType");
            }
        } else {
            throw new Exception("Provide a Valid Department");
        }
    }

    public ResponseEntity<Map<String, String>> deleteUser(String path, int id) {
        User dbUser = User_repo.findById(id).orElseThrow(() -> {
            return new ResourceNotFoundException(id);
        });
        User_repo.delete(dbUser);
        message.put("message", "User Data has been deleted");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(message);
    }

    @Override
    public UserFile uploadfile(MultipartFile file, String path) {
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            if (fileName.contains("..")) {
                throw new Exception("Invalid file Name");
            }
            UserFile userfile = new UserFile();
            userfile.setOriginalFileName(fileName);
            userfile.setGeneratedFileName(fileService.fileSave(file, path));
            userfile.setFileType(file.getContentType());
            return userfile;
            // return this.userFileRepo.save(userfile);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new UserFile();

    }

    @Override
    public ResponseEntity<Resource> getFile(String fileName, String path) throws IOException {
        UserFile file = this.userFileRepo.findByGeneratedFileName(fileName).orElseThrow(() -> {
            throw new ResourceNotFoundException();
        });
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(file.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalFileName() + "\"")
                .body(new ByteArrayResource(fileService.getImage(path, file.getGeneratedFileName())));
    }

    @SuppressWarnings("unchecked")
    public ResponseEntity<?> validateExcelData(MultipartFile file, String entityName) {
        try {
            if (!fileService.checkType(file)) {
                return ResponseEntity.badRequest().body("Invalid file type");
            }

            Class<?> dtoClass = null;
            switch (entityName.toLowerCase()) {
                case "user":
                    dtoClass = UserDto.class;
                    break;
                case "department":
                    dtoClass = DepartmentDto.class;
                    break;
                case "usertype":
                    dtoClass = UserTypeDto.class;
                    break;
                default:
                    throw new IllegalArgumentException("Enter a valid Entity name");
            }

            List<?> validData = new ArrayList<>();
            List<?> invalidData = new ArrayList<>();
            List<?> excelData = (List<?>) fileService.getList(file, getDtoMap(entityName), dtoClass);

            excelData.forEach(excelUser -> {
                boolean isValid = dtoUtility.validateDtoBool(excelUser);
                Object dto = isValid ? dtoUtility.toValidExcelDto(excelUser, entityName)
                        : dtoUtility.toInvalidExcelDto(excelUser, entityName);
                ((List<Object>) (isValid ? validData : invalidData)).add(dto);
            });

            Map<String, Object> response = new HashMap<>();
            if (!validData.isEmpty()) {
                response.put("ValidData", validData);
            }
            if (!invalidData.isEmpty()) {
                response.put("InvalidData", invalidData);
            }
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter a valid Entity Name");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file");
        }
    }

    private Map<String, String> getDtoMap(String entityName) {
        switch (entityName.toLowerCase()) {
            case "user":
                return UserMap;
            case "department":
                return DepartmentMap;
            case "usertype":
                return UserTypeMap;
            default:
                throw new IllegalArgumentException("Enter a valid entity name");
        }
    }

    public ResponseEntity<?> downloadExcelFormat(String entityName) throws Exception {
        String fileName = "ExcelFormat.xlsx";
        Class<?> dtoClass = null;
        switch (entityName.toLowerCase()) {
            case "user":
                dtoClass = UserDto.class;
                break;
            case "department":
                dtoClass = DepartmentDto.class;
                break;
            case "usertype":
                dtoClass = UserTypeDto.class;
                break;
            default:
                throw new IllegalArgumentException("Enter a valid Entity name");
        }
        ByteArrayInputStream actualData = fileService.getExcelDataFormat(dtoClass, getDtoMap(entityName));
        InputStreamResource file = new InputStreamResource(actualData);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName
                        + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    public ResponseEntity<?> saveExcelData(List<ExcelDataResponseDto> dto) {
        List<ExcelDataResponseDto> validData = new ArrayList<>();
        List<ExcelDataResponseDto> InvalidData = new ArrayList<>();
        dto.stream().forEach((user) -> {
            if (dtoUtility.validateDtoBool(user) == false) {
                user.setStatus(false);
                user.setMessages(dtoUtility.validateDto(user));
                InvalidData.add(user);
            } else {
                validData.add(user);
            }
        });
        List<User> users = validData.stream()
                .map(dtoUtility::toUser)
                .collect(Collectors.toList());

        List<User> validUsers = new ArrayList<>();
        List<UserDto> existingUsers = new ArrayList<>();
        users.stream()
                .forEach(user -> {
                    this.User_repo.findByEmail(user.getEmail())
                            .ifPresentOrElse(emailUser -> existingUsers.add(dtoUtility.toUserDto(user)),
                                    () -> validUsers.add(user));
                });
        List<User> dbUsers = this.User_repo.saveAll(validUsers);

        List<UserDto> dbUserResponse = dbUsers.stream()
                .map(dtoUtility::toUserDto)
                .collect(Collectors.toList());
        Map<String, List<?>> reposneMap = new HashMap<>();
        if (!validUsers.isEmpty()) {
            reposneMap.put("Added User-List", dbUserResponse);
        }
        if (!existingUsers.isEmpty()) {
            reposneMap.put("Already Existing-User-List", existingUsers);
        }
        if (!InvalidData.isEmpty()) {
            reposneMap.put("Invalid-Users", InvalidData);
        }
        if (validUsers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(reposneMap);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(reposneMap);
    }

    public ResponseEntity<Resource> getExcelSheet(String keyword) throws Exception {

        List<User> users = this.repoUtil.SearchUser(keyword);
        List<UserDto> usersdto = users.stream().map(dtoUtility::toUserDto).collect(Collectors.toList());
        String fileName = "UserData.xlsx";
        ByteArrayInputStream actualData = fileService.getExcelData(usersdto, UserDto.class);
        InputStreamResource file = new InputStreamResource(actualData);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName
                        + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }
}
