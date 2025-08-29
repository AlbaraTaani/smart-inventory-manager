package com.dotconvertecs.smart_inventory_manager.service;

import com.dotconvertecs.smart_inventory_manager.exception.custome.ItemNotFoundException;
import com.dotconvertecs.smart_inventory_manager.model.dto.request.ItemRequestCreateDto;
import com.dotconvertecs.smart_inventory_manager.model.dto.request.ItemRequestUpdateDto;
import com.dotconvertecs.smart_inventory_manager.model.dto.response.ItemResponseDto;
import com.dotconvertecs.smart_inventory_manager.model.entity.Item;
import com.dotconvertecs.smart_inventory_manager.repository.ItemRepository;
import com.dotconvertecs.smart_inventory_manager.service.impl.ItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;

import java.util.List;


/**
 * Controller tests using standalone MockMvc so we avoid @MockBean (deprecated in your setup).
 * This creates the controller with a Mockito mock and attaches the global exception handler.
 */
@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository repository;

    @InjectMocks
    private ItemServiceImpl service;

    private Item item1;
    private Item item2;

    @BeforeEach
    public void setUp() {
        item1 = new Item(1L, "Item1", "Desc1", 2, 10.0);
        item2 = new Item(2L, "Item2", "Desc2", 10, 50.0);
    }

//    @Test
//    public void whenCreateItem_valid_thenReturnsCreatedDto() {
//        ItemRequestCreateDto dto = new ItemRequestCreateDto();
//        dto.setName("New");
//        dto.setDescription("New desc");
//        dto.setQuantity(5);
//        dto.setPrice(12.5);
//
//        // simulate repository saving and assigning id
//        Item saved = new Item(100L, dto.getName(), dto.getDescription(), dto.getQuantity(), dto.getPrice());
//        when(repository.save(any(Item.class))).thenReturn(saved);
//
//        ItemResponseDto result = service.createItem(dto);
//
//        assertNotNull(result);
//        assertEquals(100L, result.getId());
//        assertEquals("New", result.getName());
//        verify(repository, times(1)).save(any(Item.class));
//    }

    @Test
    public void whenUpdateItem_existing_thenReturnsUpdated() {
        ItemRequestUpdateDto dto = new ItemRequestUpdateDto();
        dto.setName("Updated");
        dto.setDescription("Updated desc");
        dto.setQuantity(8);
        dto.setPrice(20.0);

        Item existing = new Item(2L, "Old", "Old desc", 3, 15.0);
        when(repository.findById(2L)).thenReturn(Optional.of(existing));
        when(repository.update(any(Item.class))).thenReturn(1);

        ItemResponseDto updated = service.updateItem(2L, dto);

        assertNotNull(updated);
        assertEquals(2L, updated.getId());
        assertEquals("Updated", updated.getName());
        verify(repository).update(any(Item.class));
    }

    @Test
    public void whenUpdateItem_notExisting_thenThrow() {
        ItemRequestUpdateDto dto = new ItemRequestUpdateDto();
        dto.setName("Updated");
        dto.setDescription("desc");
        dto.setQuantity(1);
        dto.setPrice(1.0);

        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> service.updateItem(999L, dto));
        verify(repository, never()).update(any());
    }

    @Test
    public void whenDeleteItem_existing_thenDeletes() {
        when(repository.findById(1L)).thenReturn(Optional.of(item1));
        when(repository.delete(1L)).thenReturn(1);

        assertDoesNotThrow(() -> service.deleteItem(1L));
        verify(repository).delete(1L);
    }

    @Test
    public void whenDeleteItem_notExisting_thenThrow() {
        when(repository.findById(123L)).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> service.deleteItem(123L));
        verify(repository, never()).delete(anyLong());
    }

    @Test
    public void whenGetLowStockItems_thenReturnsMatching() {
        when(repository.findLowStock(5)).thenReturn(List.of(item1));
        var res = service.getLowStockItems(5);
        assertEquals(1, res.size());
        assertEquals("Item1", res.getFirst().getName());
    }

    @Test
    public void whenGetAllItems_withPriceFilterAndSort_thenWorks() {
        when(repository.findAll()).thenReturn(List.of(item1, item2)); // item1 price 10, item2 price 50

        var result = service.getAllItems(20.0, null, "price", "asc");
        // After minPrice=20, item2 remains
        assertEquals(1, result.size());
        assertEquals("Item2", result.getFirst().getName());
    }
}