package com.dotconvertecs.smart_inventory_manager.repository;

import com.dotconvertecs.smart_inventory_manager.model.entity.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    List<Item> findAll();
    Optional<Item> findById(Long id);
    Item save(Item item);               // create: returns item with generated id
    int update(Item item);          // update returns true if affected row(s)
    int delete(Long id);        // delete returns true if affected row(s)
    List<Item> findLowStock(int threshold);


    int create(Item item);
}
