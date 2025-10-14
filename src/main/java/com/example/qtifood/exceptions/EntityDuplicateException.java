package com.example.qtifood.exceptions;


public class EntityDuplicateException extends RuntimeException {
    public EntityDuplicateException(String entityName) {
        super(entityName + " already exists in the system");
    }
    
}
