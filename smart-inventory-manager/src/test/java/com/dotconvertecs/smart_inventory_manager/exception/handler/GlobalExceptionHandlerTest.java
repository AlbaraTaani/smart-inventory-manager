package com.dotconvertecs.smart_inventory_manager.exception.handler;

import com.dotconvertecs.smart_inventory_manager.exception.custome.ItemNotFoundException;
import com.dotconvertecs.smart_inventory_manager.model.dto.error.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private MethodArgumentNotValidException notValidException;

    @Mock
    private BindingResult bindingResult;


    @Test
    void whenItemNotFoundException_thenReturns404Response() {
        ItemNotFoundException ex = new ItemNotFoundException("Item with ID 5 was not found.");

        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleNotFound(ex);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(404, responseEntity.getBody().getStatus());
        assertEquals("Item with ID 5 was not found.", responseEntity.getBody().getMessage());
    }

    @Test
    void whenMethodArgumentNotValidException_thenReturns400Response() {
        when(notValidException.getBindingResult()).thenReturn(bindingResult);
        FieldError fieldError = new FieldError("objectName", "fieldName", "must not be null");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleValidation(notValidException);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(400, responseEntity.getBody().getStatus());
        assertEquals("fieldName: must not be null", responseEntity.getBody().getMessage());
    }

    @Test
    void whenGenericException_thenReturns500Response() {
        Exception ex = new NullPointerException("A random error occurred.");

        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleAll(ex);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(500, responseEntity.getBody().getStatus());
        assertEquals("An unexpected error occurred", responseEntity.getBody().getMessage());
    }
}

