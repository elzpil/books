package com.app.users.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " not found with ID: " + id);
    }

    public ResourceNotFoundException(String resourceName, String name) {
        super(resourceName + " named " + name + " not found");
    }
}
