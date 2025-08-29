package com.dotconvertecs.smart_inventory_manager.model.mapper;

import com.dotconvertecs.smart_inventory_manager.model.dto.ItemDTO;
import com.dotconvertecs.smart_inventory_manager.model.entity.Item;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemMapper {
    public ItemDTO toDTO(Item item){
        return new ItemDTO(item.getId(),item.getName(),item.getDescription(),item.getQuantity(),item.getPrice());
    }
    public Item toEntity(ItemDTO dto){
        return new Item(dto.getId(),dto.getName(),dto.getDescription(),dto.getQuantity(),dto.getPrice());
    }

    public List<Item> toEntities(List<ItemDTO> dtos){
        return dtos.stream().map(this::toEntity).toList();
    }
    public List<ItemDTO> toDTOs(List<Item> entities){
        return entities.stream().map(this::toDTO).toList();
    }
}
