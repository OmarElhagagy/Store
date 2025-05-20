package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
<<<<<<< HEAD

    private static final long serialVersionUID = 1L;

    private String resourceName;
    private String fieldName;
    private Object fieldValue;

=======
    private static final long serialVersionUID = 1L;
    
    private String resourceName;
    private String fieldName;
    private Object fieldValue;
    
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
<<<<<<< HEAD

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

=======
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public String getResourceName() {
        return resourceName;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
    public Object getFieldValue() {
        return fieldValue;
    }
}
