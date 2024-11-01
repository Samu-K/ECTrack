package fi.tuni.ec.backend.controller;

import java.util.List;

import fi.tuni.ec.api.ApiService;
import junit.framework.TestCase;

/**
 * This class is used for testing ApiServices.
 */
public class ApiServiceTest extends TestCase {

  private ApiService apiService;

  /**
   * Sets up an instance of the ApiService class for each test.
   *
   * @throws Exception if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    apiService = new ApiService();
  }

  /**
   * Cleans up the instance after each test.
   *
   * @throws Exception if an error occurs.
   */
  protected void tearDown() throws Exception {
    super.tearDown();
    apiService = null;
  }

  /**
   * Tests the getApiKey method.
   */
  public void testGetApiKey() {
    String apiKey = ApiService.getApiKey();

    // Assert: Confirms API key is not null or empty
    assertNotNull("API key shouldn't be null.", apiKey);
    assertFalse("API key shouldn't be empty", apiKey.isEmpty());
  }

  /**
   * Tests the fetchPricing method.
   */
  public void testFetchPricing() {
    try {
      String country = "Finland";
      String periodStart = "202401010000";
      String periodEnd = "202401312300";

      List<Double> pricingData = apiService.fetchPricing(country, periodStart, periodEnd);

      // Assert: Confirms pricing data is not null or empty
      assertNotNull("Pricing data shouldn't be null", pricingData);
      assertFalse("Pricing data shouldn't be empty", pricingData.isEmpty());
    } catch (Exception e) {
      fail("fetchPricing threw an exception: " + e.getMessage());
    }
  }

  /**
   * Tests the fetchUsage method.
   */
  public void testFetchUsage() {

    try {
      String country = "Finland";
      String periodStart = "202401010000";
      String periodEnd = "202401310000";

      List<Double> usageData = apiService.fetchUsage(country, periodStart, periodEnd);

      // Assert: Confirms usage data is not null or empty
      assertNotNull("Usage data shouldn't be null", usageData);
      assertFalse("Usage data shouldn't be empty", usageData.isEmpty());
    } catch (Exception e) {
      fail("fetchUsage threw an exception: " + e.getMessage());
    }
  }
}
