package com.dotconvertecs.smart_inventory_manager.model.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponseDto {

    private Long id;
    private String name;
    private String description;
    private Integer quantity;
    private Double price;


}
