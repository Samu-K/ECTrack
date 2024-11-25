package fi.tuni.ec.api;

import fi.tuni.ec.api.ApiData;
import fi.tuni.ec.api.ApiService;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

      // Assert: Throws an exception with invalid time periods
      String invalidPeriod = "INVALID_PERIOD";
      try {
        apiService.fetchData(country, invalidPeriod, invalidPeriod);
        fail("IOException was not thrown");
      } catch (Exception e) {
        assertEquals("Expected exception was not thrown", e.getClass(),
            IOException.class);
      }
    } catch (Exception e) {
      e.printStackTrace();
      fail("fetchData threw an exception: " + e.getMessage());
    }
  }

  /**
   * Tests the fetchMultiZoneData method.
   */
  public void testFetchMultiZoneData() throws Exception {
    String country = "Sweden";
    String periodStart = "202410010000";
    String periodEnd = "202410012300";

    // Accessing the method via reflection as it's private
    Method method = ApiService.class.getDeclaredMethod("fetchMultiZoneData", String.class,
        String.class, String.class);
    method.setAccessible(true);
    List<ApiData> result = (List<ApiData>) method.invoke(apiService, country, periodStart,
        periodEnd);

    // Assert: the results are not null or empty
    assertNotNull("Data should not be null", result);
    assertFalse("Data should not be empty", result.isEmpty());

    // Assert: There should be at least 24 data points when fetching data for the period with 60 min
    // intervals
    int size = result.size();
    assertTrue("There should be more data points for this period", size >= 24);

    // Assert: Throws an exception with invalid time periods
    String invalidPeriod = "INVALID_PERIOD";
    try {
      method.invoke(apiService, country, invalidPeriod, invalidPeriod);
      fail("IOException was not thrown");
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      assertEquals("Expected exception was not thrown", cause.getClass(),
          IOException.class);
    }
  }
}
