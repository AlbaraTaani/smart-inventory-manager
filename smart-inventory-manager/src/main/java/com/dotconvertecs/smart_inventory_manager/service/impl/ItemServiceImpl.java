package com.dotconvertecs.smart_inventory_manager.service.impl;

import com.dotconvertecs.smart_inventory_manager.exception.custome.ItemNotFoundException;
import com.dotconvertecs.smart_inventory_manager.model.dto.request.ItemRequestCreateDto;
import com.dotconvertecs.smart_inventory_manager.model.dto.request.ItemRequestUpdateDto;
import com.dotconvertecs.smart_inventory_manager.model.dto.response.ItemResponseDto;
import com.dotconvertecs.smart_inventory_manager.model.entity.Item;
import com.dotconvertecs.smart_inventory_manager.repository.ItemRepository;
import com.dotconvertecs.smart_inventory_manager.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;

    @Autowired
    public ItemServiceImpl(ItemRepository repository) {
        this.repository = repository;
    }


    @Override
    public List<ItemResponseDto> getAllItems(Double minPrice, Double maxPrice, String sortBy, String order) {
        // Fetch all items, then filter/sort in-memory for simplicity
        List<Item> items = repository.findAll();

        // Price filtering
        if (minPrice != null) {
            items = items.stream()
                    .filter(i -> i.getPrice() >= minPrice)
                    .collect(Collectors.toList());
        }
        if (maxPrice != null) {
            items = items.stream()
                    .filter(i -> i.getPrice() <= maxPrice)
                    .collect(Collectors.toList());
        }

        // Sorting (only by "price" or default "id")
        Comparator<Item> comparator = Comparator.comparing(Item::getId);
        if ("price".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(Item::getPrice);
        }
        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }
        items.sort(comparator);

        // Map to DTOs
        return items.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public ItemResponseDto getItemById(Long id) {
        Item item = repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id " + id));
        return toDto(item);
    }


    @Override
    public ItemResponseDto createItem(ItemRequestCreateDto dto) {
        // Map DTO to entity
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());
        repository.create(item);
        return toDto(item);
    }

    @Override
    public ItemResponseDto updateItem(Long id, ItemRequestUpdateDto dto) {
        Item existing = repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id " + id));
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setQuantity(dto.getQuantity());
        existing.setPrice(dto.getPrice());
        repository.update(existing);
        return toDto(existing);
    }

    @Override
    public void deleteItem(Long id) {
        Optional<Item> existing = repository.findById(id);
        if (existing.isEmpty()) {
            throw new ItemNotFoundException("Item not found with id " + id);
        }
        repository.delete(id);
    }

    @Override
    public List<ItemResponseDto> getLowStockItems(int threshold) {
        List<Item> items = repository.findLowStock(threshold);
        return items.stream().map(this::toDto).collect(Collectors.toList());
    }

    // Helper to map entity to DTO
    private ItemResponseDto toDto(Item item) {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        return dto;
    }
}
