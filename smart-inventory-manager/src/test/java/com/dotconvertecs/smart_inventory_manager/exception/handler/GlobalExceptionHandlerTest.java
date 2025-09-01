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

/**
 * Basic unit tests for the GlobalExceptionHandler using Mockito.
 * This approach tests the handler's methods directly without loading the Spring web context,
 * making the tests faster and more focused on the handler's logic.
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    // The class we are testing
    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    // A mock of the exception that has nested objects we need to control
    @Mock
    private MethodArgumentNotValidException notValidException;

    @Mock
    private BindingResult bindingResult;

    // The @BeforeEach setUp() method has been removed as it was causing the error.

    @Test
    void whenItemNotFoundException_thenReturns404Response() {
        // Arrange
        ItemNotFoundException ex = new ItemNotFoundException("Item with ID 5 was not found.");

        // Act
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleNotFound(ex);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(404, responseEntity.getBody().getStatus());
        assertEquals("Item with ID 5 was not found.", responseEntity.getBody().getMessage());
    }

    @Test
    void whenMethodArgumentNotValidException_thenReturns400Response() {
        // Arrange
        // FIX: The necessary mock setup is moved here, into the only test that uses it.
        when(notValidException.getBindingResult()).thenReturn(bindingResult);
        FieldError fieldError = new FieldError("objectName", "fieldName", "must not be null");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // Act
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleValidation(notValidException);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(400, responseEntity.getBody().getStatus());
        assertEquals("fieldName: must not be null", responseEntity.getBody().getMessage());
    }

    @Test
    void whenGenericException_thenReturns500Response() {
        // Arrange
        Exception ex = new NullPointerException("A random error occurred.");

        // Act
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleAll(ex);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(500, responseEntity.getBody().getStatus());
        assertEquals("An unexpected error occurred", responseEntity.getBody().getMessage());
    }
}

