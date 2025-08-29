package com.dotconvertecs.smart_inventory_manager.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDTO {

    private Long id;

    @NotBlank(message = "required field : name")
    private String name;

    private String description;

    @NotNull(message = "required field : quantity")
    @Min(value = 0,message = "the value must be >= 0")
    private Integer quantity;

    @NotNull(message = "required field : price")
    @Min(value = 0,message = "the price must be >= 0")
    private Double price;
}
