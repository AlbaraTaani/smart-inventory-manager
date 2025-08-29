package com.dotconvertecs.smart_inventory_manager.exception.custome;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String message) {
        super(message);
    }
}
