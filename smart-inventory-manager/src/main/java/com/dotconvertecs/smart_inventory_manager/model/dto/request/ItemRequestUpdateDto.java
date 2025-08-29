package com.dotconvertecs.smart_inventory_manager.model.dto.request;



import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Update DTO - split from create DTO to allow future differences (partial update, permissions, etc.).
 * Currently requires same validations as create. Change later if you support PATCH-style updates.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestUpdateDto {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be >= 0")
    private Integer quantity;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be >= 0")
    private Double price;

}
