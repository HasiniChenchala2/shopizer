package com.salesmanager.test.shop.integration.grammarTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.manufacturer.PersistableManufacturer;
import com.salesmanager.shop.model.catalog.manufacturer.ReadableManufacturer;
import com.salesmanager.shop.store.controller.manufacturer.facade.ManufacturerFacade;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.v1.product.ProductManufacturerApi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ManufacturerControllerTest {

    @Mock
    private ManufacturerFacade manufacturerFacade;

    @InjectMocks
    private ProductManufacturerApi manufacturerController;

    @Mock
    private MerchantStore mockStore;

    @Mock
    private Language mockLanguage;

    @Mock
    private HttpServletResponse mockResponse;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        mockStore = new MerchantStore(1, MerchantStore.DEFAULT_STORE, "Default Store");
        mockLanguage = new Language("en");
    }

    @ParameterizedTest
    @MethodSource("provideGetManufacturerJsonInputs")
    public void testGetManufacturerParameterized(String jsonInput) throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Integer> inputMap = objectMapper.readValue(jsonInput.replace("\uFEFF", ""), Map.class);
        Integer manufacturerId = inputMap.get("id");


        if (manufacturerId >= 1 && manufacturerId <= 10) {
            // Mock valid manufacturer retrieval
            ReadableManufacturer mockManufacturer = mock(ReadableManufacturer.class);
			//ReadableManufacturer manufacturer = manufacturerFacade.getManufacturer(manufacturerId.longValue(), mockStore, mockLanguage);
            when(manufacturerFacade.getManufacturer(manufacturerId.longValue(), mockStore, mockLanguage)).thenReturn(mockManufacturer);

            // Simulate calling the controller method
            ReadableManufacturer result = manufacturerController.get(manufacturerId.longValue(), mockStore, mockLanguage, mockResponse);

            // Verify behavior and assert results
            verify(manufacturerFacade).getManufacturer(manufacturerId.longValue(), mockStore, mockLanguage);
            assertNotNull(result);
            assertSame(mockManufacturer, result);
        } else if (manufacturerId == 0) {
            when(manufacturerFacade.getManufacturer(manufacturerId.longValue(), mockStore, mockLanguage))
                    .thenReturn(null);
    
            // Simulate calling the controller method
            manufacturerController.get(manufacturerId.longValue(), mockStore, mockLanguage, mockResponse);
    
            // Verify the response sends the 404 error for a null manufacturer
            verify(mockResponse).sendError(404, "No Manufacturer found for ID : " + manufacturerId);
        } 
        else {
            when(manufacturerFacade.getManufacturer(manufacturerId.longValue(), mockStore, mockLanguage))
                    .thenThrow(new Exception("Some unexpected error"));
    
            // Perform the test and check for the 503 response
            manufacturerController.get(manufacturerId.longValue(), mockStore, mockLanguage, mockResponse);
    
            // Verify the response sends the 503 error for an exception
            verify(mockResponse).sendError(503, "Error while getting manufacturer Some unexpected error");
        }
    }
    static Stream<String> provideGetManufacturerJsonInputs() throws Exception {
        java.io.InputStream inputStream = CatalogControllerTest.class.getResourceAsStream("/getManufacturer.json");
        if (inputStream == null) {
            throw new IllegalArgumentException("Resource not found: /getManufacturer.json");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8) ); 
        return reader.lines();
    }
}
