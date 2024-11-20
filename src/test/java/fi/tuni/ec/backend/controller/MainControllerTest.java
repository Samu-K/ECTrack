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

  private MainController mainController;
  private Stage stage;

  /**
   * Sets up an instance of mainController for each test.
   */
  protected void setUp() throws Exception {
    // Initialize JavaFX for tests.
    final CountDownLatch latch = new CountDownLatch(1);
    if (!Platform.isFxApplicationThread()) {
      Platform.startup(() -> {});
    }
    Platform.runLater(() -> {
      stage = new Stage();
      mainController = new MainController(stage);
      latch.countDown();
    });
    latch.await();
  }

  /**
   * Tests fetching data with valid inputs.
   */
  public void testFetchDataWithValidInputs() {
    String country = "Finland";
    String startDate = "202401010000";
    String endDate = "202401312300";

    List<ApiData> data = mainController.fetchData(country, startDate, endDate);
    assertNotNull("Data should not be null for valid inputs.", data);
  }
}
