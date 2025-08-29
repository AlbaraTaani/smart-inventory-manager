package com.dotconvertecs.smart_inventory_manager.service;

import com.dotconvertecs.smart_inventory_manager.model.dto.request.ItemRequestCreateDto;
import com.dotconvertecs.smart_inventory_manager.model.dto.request.ItemRequestUpdateDto;
import com.dotconvertecs.smart_inventory_manager.model.dto.response.ItemResponseDto;

import java.util.List;

public interface ItemService {

    List<ItemResponseDto> getAllItems(Double minPrice, Double maxPrice, String sortBy, String order);
    ItemResponseDto getItemById(Long id);
    ItemResponseDto createItem(ItemRequestCreateDto dto);
    ItemResponseDto updateItem(Long id, ItemRequestUpdateDto dto);
    void deleteItem(Long id);
    List<ItemResponseDto> getLowStockItems(int threshold);

}
