package com.salesmanager.test.shop.integration.grammarTest;

import com.braintreegateway.Merchant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.catalog.PersistableCatalog;
import com.salesmanager.shop.model.catalog.catalog.ReadableCatalog;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.v1.catalog.CatalogApi;
import com.salesmanager.shop.store.controller.catalog.facade.CatalogFacade;

import antlr.collections.List;

import org.jgroups.util.Base64.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CatalogControllerTest {
    @Mock
    private CatalogFacade catalogFacade;

    @InjectMocks
    private CatalogApi catalogController;

    @Mock
    private MerchantStore mockStore;  // Mock for MerchantStore

    @Mock
    private Language mockLanguage;   // Mock for Language

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper(); 
        mockStore = new MerchantStore(1, MerchantStore.DEFAULT_STORE, "Default Store");
        mockLanguage = new Language("en");
    }
    @ParameterizedTest
    @MethodSource("provideJsonInputs")
    public void testGetCatalogWithValidAndInvalidIds(String jsonInput) throws IOException {
        @SuppressWarnings("unchecked")
        Map<String, Integer> inputMap = objectMapper.readValue(jsonInput.replace("\uFEFF", ""), Map.class);
        Long catalogId = ((Number) inputMap.get("id")).longValue();

 
        // Mock behavior for IDs 1 to 100 (valid IDs)
        if (catalogId >= 1 && catalogId <= 100) {
            ReadableCatalog mockCatalog = mock(ReadableCatalog.class);
            when(catalogFacade.getCatalog(catalogId.longValue(),mockStore,mockLanguage)).thenReturn(mockCatalog);

            // Simulate calling the controller method
            ReadableCatalog result = catalogController.getCatalog(catalogId.longValue(),mockStore,mockLanguage);

            // Verify that the catalogFacade method was called correctly
            verify(catalogFacade).getCatalog(catalogId.longValue(),mockStore,mockLanguage);
            assertNotNull(result);
            assertSame(mockCatalog, result);
        } 
        // Mock behavior for IDs greater than 100 (invalid IDs)
        else {
            when(catalogFacade.getCatalog(catalogId.longValue(),mockStore,mockLanguage))
                .thenThrow(new ResourceNotFoundException("Catalog with id [" + catalogId + "] not found"));

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                catalogController.getCatalog(catalogId.longValue(),mockStore,mockLanguage);
            });
            assertTrue(exception.getErrorMessage().contains("Catalog with id [" + catalogId + "] not found"));
        }
    }
    static Stream<String> provideJsonInputs() throws Exception {
        java.io.InputStream inputStream = CatalogControllerTest.class.getResourceAsStream("/getCatalog.json");
        if (inputStream == null) {
            throw new IllegalArgumentException("Resource not found: /getCatalog.json");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8) ); 
        return reader.lines();
    }

    @ParameterizedTest
    @MethodSource("provideJsonPayloads")
    public void CreateCatalog(String jsonInput) throws IOException {
        PersistableCatalog inputCatalog = objectMapper.readValue(jsonInput.replace("\uFEFF", ""), PersistableCatalog.class);

        boolean isValid = inputCatalog.getCode() != null && !inputCatalog.getCode().isEmpty() && inputCatalog.getId() > 0;

        if (isValid) {
            // Mock behavior for valid inputs
            ReadableCatalog mockResponse = mock(ReadableCatalog.class);
            when(catalogFacade.saveCatalog(inputCatalog, mockStore, mockLanguage)).thenReturn(mockResponse);

            // Simulate calling the controller method
            ReadableCatalog result = catalogController.createCatalog(inputCatalog, mockStore, mockLanguage);

            // Verify that the catalogFacade method was called correctly
            verify(catalogFacade).saveCatalog(inputCatalog, mockStore, mockLanguage);
            assertNotNull(result);
            assertSame(mockResponse, result);
        } else {
            when(catalogFacade.saveCatalog(inputCatalog, mockStore, mockLanguage))
                .thenThrow(new IllegalArgumentException("Invalid catalog input"));
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                catalogController.createCatalog(inputCatalog, mockStore, mockLanguage);
            });
            assertTrue(exception.getMessage().contains("Invalid catalog input"));
        }
    }
    static Stream<String> provideJsonPayloads() throws Exception {
        java.io.InputStream inputStream = CatalogControllerTest.class.getResourceAsStream("/postCatalog.json");
        if (inputStream == null) {
            throw new FileNotFoundException("Test file not found at /postCatalog.json");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return reader.lines();
    }


    @ParameterizedTest
    @MethodSource("provideJsonPayloadsPatchMethod")
    public void testUpdateCatalog(String jsonInput) throws IOException {
        PersistableCatalog inputCatalog = objectMapper.readValue(jsonInput.replace("\uFEFF", ""), PersistableCatalog.class);
        Long catalogId = inputCatalog.getId();
        boolean isValid = inputCatalog.getCode() != null && !inputCatalog.getCode().isEmpty();

        if (isValid) {
            // Mock behavior for valid inputs
            doNothing().when(catalogFacade).updateCatalog(catalogId, inputCatalog, mockStore, mockLanguage);

            // Simulate calling the controller method
            assertDoesNotThrow(() -> catalogController.updateCatalog(catalogId, inputCatalog, mockStore, mockLanguage));

            // Verify that the catalogFacade method was called correctly
            verify(catalogFacade).updateCatalog(catalogId, inputCatalog, mockStore, mockLanguage);
        } else {
            // Mock behavior for invalid inputs
            doThrow(new IllegalArgumentException("Invalid catalog input"))
                .when(catalogFacade)
                .updateCatalog(catalogId, inputCatalog, mockStore, mockLanguage);

            // Perform the test and check for the exception
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                catalogController.updateCatalog(catalogId, inputCatalog, mockStore, mockLanguage);
            });

            // Assert that the exception message contains the expected error
            assertTrue(exception.getMessage().contains("Invalid catalog input"));
        }
    }
    static Stream<String> provideJsonPayloadsPatchMethod() throws Exception {
        java.io.InputStream inputStream = CatalogControllerTest.class.getResourceAsStream("/patchCatalog.json");
        if (inputStream == null) {
            throw new FileNotFoundException("Test file not found at /patchCatalog.json");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return reader.lines();
    }

    @ParameterizedTest
    @MethodSource("provideDeleteCatlaogJsonInputs")
    public void testDeleteCatalog(String jsonInput)  throws IOException{
        @SuppressWarnings("unchecked")
        Map<String, Integer> inputMap = objectMapper.readValue(jsonInput.replace("\uFEFF", ""), Map.class);
        Long catalogId = ((Number) inputMap.get("id")).longValue();

        // Mock valid catalog IDs (e.g., 1-10)
        if (catalogId >= 1 && catalogId <= 10) {
            doNothing().when(catalogFacade).deleteCatalog(catalogId.longValue(), mockStore, mockLanguage);
    
            // Simulate calling the controller method
            assertDoesNotThrow(() -> catalogController.deleteCatalog(catalogId.longValue(), mockStore, mockLanguage));
    
            // Verify that the facade's delete method was called
            verify(catalogFacade).deleteCatalog(catalogId.longValue(), mockStore, mockLanguage);
        } 
        // Mock invalid catalog IDs (>10)
        else {
            doThrow(new ResourceNotFoundException("Catalog with id [" + catalogId + "] not found"))
                .when(catalogFacade)
                .deleteCatalog(catalogId.longValue(), mockStore, mockLanguage);
    
            // Perform the test and check for the exception
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                catalogController.deleteCatalog(catalogId.longValue(), mockStore, mockLanguage);
            });
    
            // Assert that the exception message contains the expected error
            assertTrue(exception.getErrorMessage().contains("Catalog with id [" + catalogId + "] not found"));
        }
    }
    static Stream<String> provideDeleteCatlaogJsonInputs() throws Exception {
        java.io.InputStream inputStream = CatalogControllerTest.class.getResourceAsStream("/deleteCatalog.json");
        if (inputStream == null) {
            throw new IllegalArgumentException("Resource not found: /deleteCatalog.json");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8) ); 
        return reader.lines();
    }


}

