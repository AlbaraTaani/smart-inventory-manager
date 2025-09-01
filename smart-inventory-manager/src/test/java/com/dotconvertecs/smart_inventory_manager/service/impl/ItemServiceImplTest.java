package com.dotconvertecs.smart_inventory_manager.service.impl;

import com.dotconvertecs.smart_inventory_manager.exception.custome.ItemNotFoundException;
import com.dotconvertecs.smart_inventory_manager.model.dto.request.ItemRequestCreateDto;
import com.dotconvertecs.smart_inventory_manager.model.dto.request.ItemRequestUpdateDto;
import com.dotconvertecs.smart_inventory_manager.model.dto.response.ItemResponseDto;
import com.dotconvertecs.smart_inventory_manager.model.entity.Item;
import com.dotconvertecs.smart_inventory_manager.model.mapper.ItemMapper;
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

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl service;

    private Item item1;
    private Item item2;

    private ItemResponseDto itemResponseDto1;


    @BeforeEach
    public void setUp() {
        item1 = new Item(1L, "Laptop", "High-end laptop", 10, 1500.0);
        item2 = new Item(2L, "Mouse", "Wireless mouse", 50, 25.0);

        itemResponseDto1 = new ItemResponseDto(1L, "Laptop", "High-end laptop", 10, 1500.0);
    }

    @Test
    public void whenGetAllItems_withPriceFilterAndSort_thenReturnsFilteredAndSortedList() {
        when(repository.findAll()).thenReturn(List.of(item1, item2));

        when(itemMapper.toResponse(item1)).thenReturn(itemResponseDto1);

        List<ItemResponseDto> result = service.getAllItems(1000.0, null, "price", "asc");

        assertEquals(1, result.size());
        assertEquals("Laptop", result.getFirst().getName());
        assertEquals(1L, result.getFirst().getId());

        verify(repository).findAll();
    }

    @Test
    public void whenCreateItem_validDto_thenReturnsCreatedDto() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Keyboard", "Mechanical", 30, 75.0);
        Item itemToSave = new Item(null, "Keyboard", "Mechanical", 30, 75.0);
        Item savedItem = new Item(3L, "Keyboard", "Mechanical", 30, 75.0);
        ItemResponseDto responseDto = new ItemResponseDto(3L, "Keyboard", "Mechanical", 30, 75.0);

        when(itemMapper.toEntity(any(ItemRequestCreateDto.class))).thenReturn(itemToSave);
        when(repository.save(any(Item.class))).thenReturn(savedItem);
        when(itemMapper.toResponse(any(Item.class))).thenReturn(responseDto);

        ItemResponseDto result = service.createItem(createDto);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Keyboard", result.getName());
        verify(repository).save(itemToSave);
        verify(itemMapper).toResponse(savedItem);
    }

    @Test
    public void whenUpdateItem_existingItem_thenReturnsUpdatedDto() {
        ItemRequestUpdateDto updateDto = new ItemRequestUpdateDto("Updated Name", "Updated Desc", 15, 1600.0);

        when(repository.findById(1L)).thenReturn(Optional.of(item1));
        doNothing().when(itemMapper).updateEntityFromDto(any(Item.class), any(ItemRequestUpdateDto.class));

        when(repository.update(any(Item.class))).thenReturn(1);

        ItemResponseDto updatedResponse = new ItemResponseDto(1L, "Updated Name", "Updated Desc", 15, 1600.0);
        when(itemMapper.toResponse(item1)).thenReturn(updatedResponse);

        ItemResponseDto result = service.updateItem(1L, updateDto);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        verify(repository).findById(1L);
        verify(itemMapper).updateEntityFromDto(item1, updateDto);
        verify(repository).update(item1);
    }


    @Test
    public void whenUpdateItem_notExisting_thenThrowException() {
        ItemRequestUpdateDto dto = new ItemRequestUpdateDto();
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.updateItem(999L, dto));
        verify(repository, never()).update(any());
    }


    @Test
    public void whenDeleteItem_existing_thenDeletesSuccessfully() {
        when(repository.findById(1L)).thenReturn(Optional.of(item1));

        when(repository.delete(1L)).thenReturn(1);

        assertDoesNotThrow(() -> service.deleteItem(1L));
        verify(repository).delete(1L);
    }
    @Test
    public void whenDeleteItem_notExisting_thenThrowException() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.deleteItem(999L));
        verify(repository, never()).delete(anyLong());
    }

    @Test
    public void whenGetLowStockItems_thenReturnsListOfDtos() {
        when(repository.findLowStock(15)).thenReturn(List.of(item1));
        when(itemMapper.toResponse(item1)).thenReturn(itemResponseDto1);

        List<ItemResponseDto> result = service.getLowStockItems(15);

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
        verify(repository).findLowStock(15);
        verify(itemMapper).toResponse(item1);
    }
}