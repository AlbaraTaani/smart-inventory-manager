package com.dotconvertecs.smart_inventory_manager.model.mapper;

import com.dotconvertecs.smart_inventory_manager.model.entity.Item;
import org.springframework.stereotype.Component;



import com.dotconvertecs.smart_inventory_manager.model.dto.request.ItemRequestCreateDto;
import com.dotconvertecs.smart_inventory_manager.model.dto.request.ItemRequestUpdateDto;
import com.dotconvertecs.smart_inventory_manager.model.dto.response.ItemResponseDto;


@Component // Mark this as a Spring-managed component
public class ItemMapper {

    public ItemResponseDto toResponse(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }

    public Item toEntity(ItemRequestCreateDto dto) {
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .build();
    }

    public void updateEntityFromDto(Item existingItem, ItemRequestUpdateDto dto) {
        existingItem.setName(dto.getName());
        existingItem.setDescription(dto.getDescription());
        existingItem.setQuantity(dto.getQuantity());
        existingItem.setPrice(dto.getPrice());
    }
}