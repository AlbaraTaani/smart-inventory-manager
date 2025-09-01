package com.dotconvertecs.smart_inventory_manager.service.impl;

import com.dotconvertecs.smart_inventory_manager.exception.custome.ItemNotFoundException;
import com.dotconvertecs.smart_inventory_manager.model.dto.request.ItemRequestCreateDto;
import com.dotconvertecs.smart_inventory_manager.model.dto.request.ItemRequestUpdateDto;
import com.dotconvertecs.smart_inventory_manager.model.dto.response.ItemResponseDto;
import com.dotconvertecs.smart_inventory_manager.model.entity.Item;
import com.dotconvertecs.smart_inventory_manager.model.mapper.ItemMapper; // Import the mapper
import com.dotconvertecs.smart_inventory_manager.repository.ItemRepository;
import com.dotconvertecs.smart_inventory_manager.service.impl.ItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository repository;

    // FIX 1: Mock the ItemMapper, as it is a required dependency for the service.
    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl service;

    // Test data entities
    private Item item1;
    private Item item2;

    // Test data DTOs
    private ItemResponseDto itemResponseDto1;


    @BeforeEach
    public void setUp() {
        // Initialize entities
        item1 = new Item(1L, "Laptop", "High-end laptop", 10, 1500.0);
        item2 = new Item(2L, "Mouse", "Wireless mouse", 50, 25.0);

        // Initialize corresponding DTOs for mocking mapper responses
        itemResponseDto1 = new ItemResponseDto(1L, "Laptop", "High-end laptop", 10, 1500.0);
    }

    @Test
    public void whenGetAllItems_withPriceFilterAndSort_thenReturnsFilteredAndSortedList() {
        // Arrange: Mock the repository to return a list of all items
        when(repository.findAll()).thenReturn(List.of(item1, item2)); // item1 price 1500, item2 price 25

        // Arrange: Mock the mapper's behavior for the item that will remain after filtering
        when(itemMapper.toResponse(item1)).thenReturn(itemResponseDto1);

        // Act: Call the service method to get items with a minimum price of $1000
        List<ItemResponseDto> result = service.getAllItems(1000.0, null, "price", "asc");

        // Assert: Verify the result is correct
        assertEquals(1, result.size());
        assertEquals("Laptop", result.getFirst().getName());
        assertEquals(1L, result.getFirst().getId());

        // Assert: Verify the repository's findAll method was called
        verify(repository).findAll();
    }

    @Test
    public void whenCreateItem_validDto_thenReturnsCreatedDto() {
        // Arrange
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Keyboard", "Mechanical", 30, 75.0);
        Item itemToSave = new Item(null, "Keyboard", "Mechanical", 30, 75.0); // Entity before saving (no ID)
        Item savedItem = new Item(3L, "Keyboard", "Mechanical", 30, 75.0);   // Entity after saving (with ID)
        ItemResponseDto responseDto = new ItemResponseDto(3L, "Keyboard", "Mechanical", 30, 75.0);

        // FIX 2: Mock the mapper's conversion from DTO to Entity and back
        when(itemMapper.toEntity(any(ItemRequestCreateDto.class))).thenReturn(itemToSave);
        when(repository.save(any(Item.class))).thenReturn(savedItem);
        when(itemMapper.toResponse(any(Item.class))).thenReturn(responseDto);

        // Act
        ItemResponseDto result = service.createItem(createDto);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Keyboard", result.getName());
        verify(repository).save(itemToSave); // Verify the correct object was passed to save
        verify(itemMapper).toResponse(savedItem);
    }

    @Test
    public void whenUpdateItem_existingItem_thenReturnsUpdatedDto() {
        // Arrange
        ItemRequestUpdateDto updateDto = new ItemRequestUpdateDto("Updated Name", "Updated Desc", 15, 1600.0);

        when(repository.findById(1L)).thenReturn(Optional.of(item1));
        doNothing().when(itemMapper).updateEntityFromDto(any(Item.class), any(ItemRequestUpdateDto.class));

        // FIX HERE: Use when().thenReturn() because the 'update' method returns a value (likely an int).
        when(repository.update(any(Item.class))).thenReturn(1); // Stub it to return 1 (for 1 row updated)

        ItemResponseDto updatedResponse = new ItemResponseDto(1L, "Updated Name", "Updated Desc", 15, 1600.0);
        when(itemMapper.toResponse(item1)).thenReturn(updatedResponse);

        // Act
        ItemResponseDto result = service.updateItem(1L, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        verify(repository).findById(1L);
        verify(itemMapper).updateEntityFromDto(item1, updateDto);
        verify(repository).update(item1);
    }


    @Test
    public void whenUpdateItem_notExisting_thenThrowException() {
        // Arrange
        ItemRequestUpdateDto dto = new ItemRequestUpdateDto();
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ItemNotFoundException.class, () -> service.updateItem(999L, dto));
        verify(repository, never()).update(any()); // Ensure update is never called
    }


    @Test
    public void whenDeleteItem_existing_thenDeletesSuccessfully() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(item1));

        // FIX HERE: Use when().thenReturn() because the 'delete' method returns a value.
        when(repository.delete(1L)).thenReturn(1); // Stub it to return 1 (for 1 row deleted)

        // Act & Assert
        assertDoesNotThrow(() -> service.deleteItem(1L));
        verify(repository).delete(1L);
    }
    @Test
    public void whenDeleteItem_notExisting_thenThrowException() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ItemNotFoundException.class, () -> service.deleteItem(999L));
        verify(repository, never()).delete(anyLong());
    }

    @Test
    public void whenGetLowStockItems_thenReturnsListOfDtos() {
        // Arrange
        when(repository.findLowStock(15)).thenReturn(List.of(item1)); // Laptop has quantity 10
        when(itemMapper.toResponse(item1)).thenReturn(itemResponseDto1);

        // Act
        List<ItemResponseDto> result = service.getLowStockItems(15);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
        verify(repository).findLowStock(15);
        verify(itemMapper).toResponse(item1);
    }
}