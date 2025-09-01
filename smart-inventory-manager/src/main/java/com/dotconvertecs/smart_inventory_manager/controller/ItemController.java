package com.dotconvertecs.smart_inventory_manager.controller;

import com.dotconvertecs.smart_inventory_manager.model.dto.request.ItemRequestCreateDto;
import com.dotconvertecs.smart_inventory_manager.model.dto.request.ItemRequestUpdateDto;
import com.dotconvertecs.smart_inventory_manager.model.dto.response.ItemResponseDto;
import com.dotconvertecs.smart_inventory_manager.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
public class ItemController {
    private final ItemService service;

    @Autowired
    public ItemController(ItemService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getAllItems(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String order) {
        List<ItemResponseDto> items = service.getAllItems(minPrice, maxPrice, sortBy, order);
        return ResponseEntity.ok(items);
    }


    @GetMapping("/low-stock")
    public ResponseEntity<List<ItemResponseDto>> getLowStockItems(
            @RequestParam(defaultValue = "5") int threshold) {
        List<ItemResponseDto> items = service.getLowStockItems(threshold);
        return ResponseEntity.ok(items);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDto> getItemById(@PathVariable Long id) {
        ItemResponseDto dto = service.getItemById(id);
        return ResponseEntity.ok(dto);
    }


    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(@Valid @RequestBody ItemRequestCreateDto dto) {
        ItemResponseDto created = service.createItem(dto);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ItemResponseDto> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemRequestUpdateDto dto) {
        ItemResponseDto updated = service.updateItem(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        service.deleteItem(id);

        return ResponseEntity.noContent().build();
    }
}
