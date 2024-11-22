package fi.tuni.ec.api;

import fi.tuni.ec.api.ApiData;
import fi.tuni.ec.api.ApiService;
import java.util.List;
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
  public void testFetchData() {
    try {
      String country = "Finland";
      String periodStart = "202401010000";
      String periodEnd = "202401312300";

      List<ApiData> pricingData = apiService.fetchData(country, periodStart, periodEnd);
      // Assert: Confirms pricing data is not null or empty
      assertNotNull("Data shouldn't be null", pricingData);
      assertFalse("Data shouldn't be empty", pricingData.isEmpty());
    } catch (Exception e) {
      e.printStackTrace();
      fail("fetchData threw an exception: " + e.getMessage());
    }
  }
}
