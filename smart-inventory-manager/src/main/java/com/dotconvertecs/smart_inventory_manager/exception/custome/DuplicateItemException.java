package com.dotconvertecs.smart_inventory_manager.exception.custome;

public class DuplicateItemException extends RuntimeException {
    public DuplicateItemException(String message) {
        super(message);
    }
}
