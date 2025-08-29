package com.dotconvertecs.smart_inventory_manager.controller;


import com.dotconvertecs.smart_inventory_manager.exception.GlobalExceptionHandler;
import com.dotconvertecs.smart_inventory_manager.exception.custome.ItemNotFoundException;
import com.dotconvertecs.smart_inventory_manager.model.dto.request.ItemRequestCreateDto;
import com.dotconvertecs.smart_inventory_manager.model.dto.request.ItemRequestUpdateDto;
import com.dotconvertecs.smart_inventory_manager.model.dto.response.ItemResponseDto;
import com.dotconvertecs.smart_inventory_manager.service.ItemService;
import com.dotconvertecs.smart_inventory_manager.service.impl.ItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Controller tests using standalone MockMvc so we avoid @MockBean (deprecated in your setup).
 * This creates the controller with a Mockito mock and attaches the global exception handler.
 */
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class ItemControllerTest {

    private ItemService service;
    private ItemController controller;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        service = Mockito.mock(ItemService.class);
        controller = new ItemController(service);
        objectMapper = new ObjectMapper();

        // Enable bean validation support in standaloneSetup
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean() {{
                    afterPropertiesSet();
                }})
                .build();
    }

    @Test
    public void testGetAllItems_returnsList() throws Exception {
        ItemResponseDto dto = new ItemResponseDto(1L, "Item1", "Desc", 10, 5.0);
        when(service.getAllItems(null, null, "id", "asc")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Item1"));

        verify(service).getAllItems(null, null, "id", "asc");
    }

    @Test
    public void testGetLowStock_returnsList() throws Exception {
        ItemResponseDto dto = new ItemResponseDto(1L, "Low", "Desc", 2, 3.0);
        when(service.getLowStockItems(5)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/items/low-stock").param("threshold", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantity").value(2));

        verify(service).getLowStockItems(5);
    }

    @Test
    public void testCreateItem_returns201() throws Exception {
        ItemRequestCreateDto create = new ItemRequestCreateDto();
        create.setName("Created");
        create.setDescription("Desc");
        create.setQuantity(4);
        create.setPrice(9.99);

        ItemResponseDto response = new ItemResponseDto(10L, "Created", "Desc", 4, 9.99);
        when(service.createItem(any(ItemRequestCreateDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Created"));

        verify(service).createItem(any(ItemRequestCreateDto.class));
    }

    @Test
    public void testCreateItem_validationError() throws Exception {
        // Missing name and negative quantity -> should trigger validation error
        String badJson = """
                {
                  "name": "",
                  "description": "x",
                  "quantity": -1,
                  "price": 5.0
                }
                """;

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists()); // message contains field errors

        verify(service, never()).createItem(any());
    }

    @Test
    public void testUpdateItem_returns200() throws Exception {
        ItemRequestUpdateDto update = new ItemRequestUpdateDto();
        update.setName("Updated");
        update.setDescription("Updated desc");
        update.setQuantity(7);
        update.setPrice(15.0);

        ItemResponseDto response = new ItemResponseDto(2L, "Updated", "Updated desc", 7, 15.0);
        when(service.updateItem(eq(2L), any(ItemRequestUpdateDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/items/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));

        verify(service).updateItem(eq(2L), any(ItemRequestUpdateDto.class));
    }

    @Test
    public void testDeleteItem_returns204() throws Exception {
        // service.deleteItem doesn't return; we just verify it is called and returns no content
        doNothing().when(service).deleteItem(3L);

        mockMvc.perform(delete("/api/items/3"))
                .andExpect(status().isNoContent());

        verify(service).deleteItem(3L);
    }

    @Test
    public void testGetById_notFound_returns404() throws Exception {
        when(service.getItemById(999L)).thenThrow(new ItemNotFoundException("not found"));

        mockMvc.perform(get("/api/items/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("not found"));

        verify(service).getItemById(999L);
    }

    @Test
    public void testFilteringAndSorting_paramsPassedToService() throws Exception {
        // ensure query params are passed to service
        when(service.getAllItems(10.0, 100.0, "price", "desc")).thenReturn(List.of());

        mockMvc.perform(get("/api/items")
                        .param("minPrice", "10")
                        .param("maxPrice", "100")
                        .param("sortBy", "price")
                        .param("order", "desc"))
                .andExpect(status().isOk());

        verify(service).getAllItems(10.0, 100.0, "price", "desc");
    }
}
