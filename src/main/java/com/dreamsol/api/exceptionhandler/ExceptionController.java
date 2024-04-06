package com.dreamsol.api.exceptionhandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.dreamsol.api.exceptionhandler.customexceptions.NoContentFoundException;
import com.dreamsol.api.exceptionhandler.customexceptions.ResourceAlreadyExist;
import com.dreamsol.api.exceptionhandler.customexceptions.ResourceNotFoundException;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> defException(Exception exception) {
        Map<String, String> errorMessage = new HashMap<String, String>();
        exception.printStackTrace();
        errorMessage.put("error", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(ResourceNotFoundException exception) {
        Map<String, String> errorMessage = new HashMap<String, String>();
        errorMessage.put("error", "User not found with Id : " + exception.getId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(ResourceAlreadyExist.class)
    public ResponseEntity<Map<String, String>> notFound(ResourceAlreadyExist exception) {
        Map<String, String> errorMessage = new HashMap<String, String>();
        errorMessage.put("error", exception.getDataName() + " " + exception.getData() + " Already Exist");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, String>> imageIssue(IOException exception) {
        Map<String, String> errorMessage = new HashMap<String, String>();
        errorMessage.put("error", "File Related Issue Occured");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> bindingErrors(MethodArgumentNotValidException exception) {
        Map<String, String> errorMessage = new HashMap<String, String>();
        exception.getBindingResult().getAllErrors().forEach((error -> {
            String FieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errorMessage.put(FieldName, message);
        }));
        return new ResponseEntity<Map<String, String>>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoContentFoundException.class)
    public ResponseEntity<Map<String, String>> notContentFound(NoContentFoundException exception) {
        Map<String, String> errorMessage = new HashMap<String, String>();
        errorMessage.put("error", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> illlegalArgumantException(IllegalArgumentException exception) {
        Map<String, String> errorMessage = new HashMap<String, String>();
        errorMessage.put("error", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }
}
