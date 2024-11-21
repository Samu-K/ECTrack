package fi.tuni.ec.backend.controller;

import fi.tuni.ec.api.ApiData;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.stage.Stage;
import junit.framework.TestCase;

/**
 * This class is used for testing MainController.
 */
public class MainControllerTest extends TestCase {

  private static boolean isJavaFXInitialized = false;
  private static MainController mainController;
  private static Stage stage;

  /**
   * Sets up an instance of mainController and stage for each test.
   */
  protected void setUp() throws Exception {
    // Initialize JavaFX for tests only once to prevent multiple windows opening and
    // taking up unnecessary resources
    if (!isJavaFXInitialized) {
      final CountDownLatch latch = new CountDownLatch(1);
      Platform.startup(() -> {
        stage = new Stage();
        mainController = new MainController(stage);
        latch.countDown();
      });
      latch.await();
      isJavaFXInitialized = true;
    }
  }

  /**
   * Tests fetching data with valid inputs.
   */
  public void testFetchDataWithValidInputs() {
    // All parameters are valid
    String country = "Finland";
    String startDate = "202401010000";
    String endDate = "202401312300";

    List<ApiData> data = mainController.fetchData(country, startDate, endDate);
    assertNotNull("Data should not be null for valid inputs.", data);
  }

  /**
   * Tests fetching data with an invalid country code.
   */
  public void testFetchDataWithInvalidCountryCode() {
    // Country is invalid, dates are valid
    String country = "InvalidCountry";
    String startDate = "202401010000";
    String endDate = "202401312300";

    List<ApiData> data = mainController.fetchData(country, startDate, endDate);
    assertNull("Data should be null for an invalid country code.", data);
  }

  /**
   * Tests fetching data with an invalid date range.
   */
  public void testFetchDataWithInvalidDateRange() {
    // Country is valid, dates are invalid
    String country = "Finland";
    String startDate = "202413330000";
    String endDate = "202414372972";

    List<ApiData> data = mainController.fetchData(country, startDate, endDate);
    assertNull("Data should be null for an invalid date range.", data);
  }

  /**
   * Tests that the main page loads properly without errors.
   */
  public void testShowMainPage() {
    final CountDownLatch latch = new CountDownLatch(1);
    Platform.runLater(() -> {
      try {
        mainController.showMainPage();
        // Assert: Stage has a scene
        assertNotNull("The primary stage should have a scene.", stage.getScene());
        // Assert: The scene has elements in it
        assertNotNull("The scene should not be empty.", stage.getScene().getRoot());
        // Assert: The window is showing and UI is loaded
        assertTrue("The page should be showing after showing main page.", stage.isShowing());
      } catch (Exception e) {
        fail("showMainPage should not throw an exception.");
      } finally {
        latch.countDown();
      }
    });
    try {
      latch.await();
    } catch (InterruptedException e) {
      fail("Test interrupted.");
    }
  }
}

