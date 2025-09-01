package com.dotconvertecs.smart_inventory_manager.repository;

import com.dotconvertecs.smart_inventory_manager.model.entity.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    List<Item> findAll();
    Optional<Item> findById(Long id);
    Item save(Item item);
    int update(Item item);
    int delete(Long id);
    List<Item> findLowStock(int threshold);

}
