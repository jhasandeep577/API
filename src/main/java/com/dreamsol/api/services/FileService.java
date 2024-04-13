package com.dreamsol.api.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dreamsol.api.dto.DepartmentDto;
import com.dreamsol.api.dto.UserTypeDto;
import com.dreamsol.api.repositories.DepartmentRepo;
import com.dreamsol.api.repositories.UserTypeRepo;

@Service
public class FileService {
    @Autowired
    DepartmentRepo departmentRepo;
    @Autowired
    UserTypeRepo usertypeRepo;
    @Autowired
    DtoUtility dtoUtility;

    public String fileSave(MultipartFile file, String path) throws IOException {
        String fileName = file.getOriginalFilename();
        file.getContentType();
        String fileExtension = fileName.substring(fileName.lastIndexOf('.'));
        String id = UUID.randomUUID().toString();
        String newFileName = id + fileExtension;
        String filePath = path + newFileName;
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdir();
        }
        Files.copy(file.getInputStream(), Paths.get(filePath));
        return newFileName;
    }

    public byte[] getImage(String path, String imageName) throws IOException {
        String fullpath = path + imageName;
        InputStream stream = new FileInputStream(fullpath);
        byte[] bytes = stream.readAllBytes();
        // String encoded=Base64.getEncoder().encodeToString(bytes);
        stream.close();
        // String
        // uri=ServletUriComponentsBuilder.fromCurrentContextPath().path("/download").toUriString();
        return bytes;
    }

    public boolean deleteImage(String path, String fileName) {
        Path filePath = Paths.get(path, fileName);
        try {
            Files.delete(filePath);
            return true;
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean checkType(MultipartFile file) {
        if (file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return true;
        } else {
            return false;
        }
    }

    public <T> List<?> getList(MultipartFile file, Map<String, String> columnMapping, Class<T> entityType) {
        List<T> resultList = new ArrayList<>();
        try {
            @SuppressWarnings("resource")
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheet(workbook.getSheetName(0));
            int rowNumber = 0;
            int lastRowId = sheet.getLastRowNum();
            if(lastRowId==3){
                throw new Exception("Empty Excel Sheet");
            }
            while (rowNumber <= lastRowId) {
                Row row = sheet.getRow(rowNumber);
                if (rowNumber <= 2 || row == null) {
                    rowNumber++;
                    continue;
                }

                T instance = entityType.getDeclaredConstructor().newInstance();

                for (Map.Entry<String, String> entry : columnMapping.entrySet()) {
                    String columnName = entry.getKey();
                    String fieldName = entry.getValue();
                    int columnIndex = getColumnIndex(sheet, columnName);

                    if (columnIndex != -1) {
                        Field field = entityType.getDeclaredField(fieldName);
                        field.setAccessible(true);
                        Cell cell = row.getCell(columnIndex);
                        setFieldValue(instance, field, cell);
                    }
                }
                resultList.add(instance);
                rowNumber++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    private int getColumnIndex(XSSFSheet sheet, String columnName) {
        Row headerRow = sheet.getRow(0);
        int lastCellNum = headerRow.getLastCellNum();
        for (int i = 0; i < lastCellNum; i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null && cell.getStringCellValue().equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1;
    }

    private void setFieldValue(Object instance, Field field, Cell cell) throws IllegalAccessException {
        if (cell != null) {
            Class<?> fieldType = field.getType();
            field.setAccessible(true);
            if (String.class.isAssignableFrom(fieldType)) {
                field.set(instance, cell.getStringCellValue());
            } else if (Integer.class.isAssignableFrom(fieldType) || int.class.isAssignableFrom(fieldType)) {
                field.set(instance, (int) cell.getNumericCellValue());
            } else if (Double.class.isAssignableFrom(fieldType) || double.class.isAssignableFrom(fieldType)) {
                field.set(instance, cell.getNumericCellValue());
            } else if (Long.class.isAssignableFrom(fieldType) || long.class.isAssignableFrom(fieldType)) {
                field.set(instance, (long) cell.getNumericCellValue());
            } else if (DepartmentDto.class.isAssignableFrom(fieldType)) {
                DepartmentDto departmentDto = new DepartmentDto();
                if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                    departmentRepo.findByDepartmentCode((int) cell.getNumericCellValue()).ifPresent((department -> {
                        DepartmentDto DBdepartmentDto = dtoUtility.toDepartmentDto(department);
                        departmentDto.setDepartmentCode(DBdepartmentDto.getDepartmentCode());
                        departmentDto.setDepartmentName(DBdepartmentDto.getDepartmentName());
                    }));
                }
                field.set(instance, departmentDto);
            } else if (UserTypeDto.class.isAssignableFrom(fieldType)) {
                UserTypeDto userTypeDto = new UserTypeDto();
                usertypeRepo.findByUserTypeName(cell.getStringCellValue()).ifPresent((usertype) -> {
                    UserTypeDto dto = dtoUtility.toUserTypeDto(usertype);
                    userTypeDto.setUserTypeName(dto.getUserTypeName());
                });
                field.set(instance, userTypeDto);
            }
        }
    }

    public ByteArrayInputStream getExcelDataFormat(Class<?> cls, Map<String, String> map) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Sheet sheet = workbook.createSheet("ExcelFormat");
        // Create header row (row 0) for attribute names
        Row headerRow = sheet.createRow(0);
        int cellIndex = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String attributeName = entry.getKey();
            Cell headerCell = headerRow.createCell(cellIndex++);
            headerCell.setCellValue(attributeName);
        }
        Row row = sheet.createRow(1);
        cellIndex = 0;
        for (@SuppressWarnings("unused") Map.Entry<String, String> entry : map.entrySet()) {
            Cell cell = row.createCell(cellIndex++);
            cell.setCellValue("Mandatory");
        }
        // Create example row (row 2) with sample data
        Row exampleRow = sheet.createRow(2);

        cellIndex = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String fieldName = entry.getValue();
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);

            // Set sample values to the fields of the example instance
            Object sampleValue = getSampleValueForType(field.getType());

            // Set the field value in the example instance
            //  field.set(exampleInstance, sampleValue);

            // Populate the cell with the sample value
            Cell cell = exampleRow.createCell(cellIndex++);
            if (sampleValue != null) {
                cell.setCellValue(sampleValue.toString());
            } else {
                cell.setCellValue(""); // Set empty value if sampleValue is null
            }
        }
        workbook.write(out);
        workbook.close();
        out.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    // Method to generate sample values based on field type
    private Object getSampleValueForType(Class<?> type) {
        if (String.class.isAssignableFrom(type)) {
            return "john doe";
        }
        else if (Integer.class.isAssignableFrom(type)|| int.class.isAssignableFrom(type)) {
            return 84638; // Sample integer
        }  else if (Long.class.isAssignableFrom(type)|| long.class.isAssignableFrom(type)) {
            return 987383728; // Sample long
        } else if (Boolean.class.isAssignableFrom(type)) {
            return true; // Sample boolean
        } else if (DepartmentDto.class.isAssignableFrom(type)) {
            return new DepartmentDto("Marketing",87676); // sample departmentdto
        } else if (UserTypeDto.class.isAssignableFrom(type)) {
            return new UserTypeDto("Admin");  // sample usertypedto
        } else {
            return null; // For other types, return null
        }
    }
     public  <T> ByteArrayInputStream getExcelData(List<T> entityList, Class<T> entityType) throws IOException, IllegalAccessException {
        Workbook workbook = new SXSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Sheet sheet = workbook.createSheet(entityType.getSimpleName() + "s");

        // Create header row
        Row headerRow = sheet.createRow(0);
        Field[] fields = entityType.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(fields[i].getName());
        }

        // Populate data rows
        int rowIndex = 1;
        for (T entity : entityList) {
            Row dataRow = sheet.createRow(rowIndex++);
            int cellIndex = 0;
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(entity);
                Cell cell = dataRow.createCell(cellIndex++);
                if (value != null) {
                    if (value instanceof List) {
                        // Convert list values to comma-separated strings
                        StringBuffer listString= new StringBuffer();
                        for (Object listItem : (List<?>) value) {
                            listString.append(listItem.toString()).append(",");
                        }
                        cell.setCellValue(listString.toString());
                    } else {
                        cell.setCellValue(value.toString());
                    }
                } else {
                    cell.setCellValue("");
                }
            }
        }
        workbook.write(out);
        workbook.close();
        out.close();
        return new ByteArrayInputStream(out.toByteArray());
    }
}
