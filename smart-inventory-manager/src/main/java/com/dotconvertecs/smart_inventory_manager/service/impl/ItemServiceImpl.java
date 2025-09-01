package com.dotconvertecs.smart_inventory_manager.service.impl;

import com.dotconvertecs.smart_inventory_manager.exception.custome.ItemNotFoundException;
import com.dotconvertecs.smart_inventory_manager.model.dto.request.ItemRequestCreateDto;
import com.dotconvertecs.smart_inventory_manager.model.dto.request.ItemRequestUpdateDto;
import com.dotconvertecs.smart_inventory_manager.model.dto.response.ItemResponseDto;
import com.dotconvertecs.smart_inventory_manager.model.entity.Item;
import com.dotconvertecs.smart_inventory_manager.model.mapper.ItemMapper;
import com.dotconvertecs.smart_inventory_manager.repository.ItemRepository;
import com.dotconvertecs.smart_inventory_manager.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {


    private final ItemRepository repository;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemResponseDto> getAllItems(Double minPrice, Double maxPrice, String sortBy, String order) {
        List<Item> items = repository.findAll();

        items = filterPrice(items,minPrice,maxPrice);



        items.sort(buildComparator(sortBy,order));


        return items.stream()
                .map(itemMapper::toResponse)
                .collect(Collectors.toList());
    }
    @Override
    public ItemResponseDto getItemById(Long id) {
        Item item = repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id " + id));


        return itemMapper.toResponse(item);
    }

    @Override
    public ItemResponseDto createItem(ItemRequestCreateDto dto) {

        Item itemToSave = itemMapper.toEntity(dto);

        Item savedItem = repository.save(itemToSave);


        return itemMapper.toResponse(savedItem);
    }

    @Override
    public ItemResponseDto updateItem(Long id, ItemRequestUpdateDto dto) {
        Item existing = repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id " + id));


        itemMapper.updateEntityFromDto(existing, dto);

        repository.update(existing);


        return itemMapper.toResponse(existing);
    }

    @Override
    public void deleteItem(Long id) {

        if (repository.findById(id).isEmpty()) {
            throw new ItemNotFoundException("Item not found with id " + id);
        }
        repository.delete(id);
    }

    @Override
    public List<ItemResponseDto> getLowStockItems(int threshold) {
        List<Item> items = repository.findLowStock(threshold);

        return items.stream()
                .map(itemMapper::toResponse)
                .collect(Collectors.toList());
    }
    private List<Item> filterPrice(List<Item> items, Double minPrice, Double maxPrice) {

        var itemsStream = items.stream();


        if (minPrice != null) {
            itemsStream = itemsStream.filter(i -> i.getPrice() >= minPrice);
        }
        if (maxPrice != null) {
            itemsStream = itemsStream.filter(i -> i.getPrice() <= maxPrice);
        }


        return itemsStream.collect(Collectors.toList());
    }


    private Comparator<Item> buildComparator(String sortBy,String order){
        Comparator<Item> comparator;
        if ("price".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(Item::getPrice);
        } else {
            comparator = Comparator.comparing(Item::getId);
        }

        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }
        return comparator;
    }

}