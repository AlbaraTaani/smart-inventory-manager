package com.dotconvertecs.smart_inventory_manager.service.impl;

import com.dotconvertecs.smart_inventory_manager.exception.custome.ItemNotFoundException;
import com.dotconvertecs.smart_inventory_manager.model.dto.request.ItemRequestCreateDto;
import com.dotconvertecs.smart_inventory_manager.model.dto.request.ItemRequestUpdateDto;
import com.dotconvertecs.smart_inventory_manager.model.dto.response.ItemResponseDto;
import com.dotconvertecs.smart_inventory_manager.model.entity.Item;
import com.dotconvertecs.smart_inventory_manager.model.mapper.ItemMapper;
import com.dotconvertecs.smart_inventory_manager.repository.ItemRepository;
import com.dotconvertecs.smart_inventory_manager.service.ItemService;
import lombok.RequiredArgsConstructor; // Using Lombok for a cleaner constructor
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Replaces the manual constructor for dependency injection
public class ItemServiceImpl implements ItemService {

    // 1. Inject both the repository and the new mapper
    private final ItemRepository repository;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemResponseDto> getAllItems(Double minPrice, Double maxPrice, String sortBy, String order) {
        List<Item> items = repository.findAll();

        // Price filtering (no change here)
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

        // Sorting (no change here)
        Comparator<Item> comparator;
        if ("price".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(Item::getPrice);
        } else if ("name".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(Item::getName);
        } else if ("quantity".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(Item::getQuantity);
        } else {
            comparator = Comparator.comparing(Item::getId);
        }

        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }
        items.sort(comparator);

        // 2. Use the mapper for the final conversion
        return items.stream()
                .map(itemMapper::toResponse) // Replaced this::toDto with itemMapper::toResponse
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponseDto getItemById(Long id) {
        Item item = repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id " + id));

        // 2. Use the mapper
        return itemMapper.toResponse(item);
    }

    @Override
    public ItemResponseDto createItem(ItemRequestCreateDto dto) {
        // 2. Use the mapper to create the entity
        Item itemToSave = itemMapper.toEntity(dto);

        Item savedItem = repository.save(itemToSave); // Use the save method that returns the entity with ID

        // 2. Use the mapper to create the response
        return itemMapper.toResponse(savedItem);
    }

    @Override
    public ItemResponseDto updateItem(Long id, ItemRequestUpdateDto dto) {
        Item existing = repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id " + id));

        // 2. Use the mapper to update the entity's fields
        itemMapper.updateEntityFromDto(existing, dto);

        repository.update(existing);

        // 2. Use the mapper to create the response
        return itemMapper.toResponse(existing);
    }

    @Override
    public void deleteItem(Long id) {
        // First check if the item exists to provide a clear error
        if (repository.findById(id).isEmpty()) {
            throw new ItemNotFoundException("Item not found with id " + id);
        }
        repository.delete(id);
    }

    @Override
    public List<ItemResponseDto> getLowStockItems(int threshold) {
        List<Item> items = repository.findLowStock(threshold);
        // 2. Use the mapper
        return items.stream()
                .map(itemMapper::toResponse)
                .collect(Collectors.toList());
    }

    // 3. The private toDto helper method is now removed! ✅
}