package com.dotconvertecs.smart_inventory_manager.model.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    private Long id;
    private String name;
    private String description;
    private Integer quantity;
    private Double price;

}
