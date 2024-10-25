package fi.tuni.ec.backend.controller;

import junit.framework.TestCase;

public class ApiServiceTest extends TestCase {

    public void testGetApiKey() {
        String apiKey = ApiService.getApiKey();
        assertNotNull("API key shouldn't be null.", apiKey);
        assertFalse("API key shouldn't be empty", apiKey.isEmpty());
    }
}
