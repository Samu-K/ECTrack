package fi.tuni.ec.backend.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import junit.framework.TestCase;

public class LandingControllerTest extends TestCase {

  private static boolean isJavaFXInitialized = false;
  private LandingController controller;




  protected void setUp() throws Exception {
    super.setUp();

    if (!isJavaFXInitialized) {
      CountDownLatch latch = new CountDownLatch(1);
      Platform.startup(() -> {
        latch.countDown();
      });
      latch.await();
      isJavaFXInitialized = true;
    }

    CountDownLatch setupLatch = new CountDownLatch(1);
    Platform.runLater(() -> {
      try {
        controller = new LandingController();
        controller.dateLabel = new Label();
        controller.countryCb = new ComboBox<>();
        controller.initialize();
      } finally {
        setupLatch.countDown();
      }
    });
    setupLatch.await();
  }

  public void testInitialize() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    Platform.runLater(() -> {
      try {
        // Assert: Date label is initialized
        assertNotNull("Date label should be initialized.", controller.dateLabel.getText());

        // Assert: Date label is initialized with current date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String currentDate = formatter.format(LocalDate.now());
        assertEquals("Current date should be today's date",
            currentDate,
            controller.dateLabel.getText());

        // Assert: Combo box is populated
        assertFalse("Country combo box should not be empty",
            controller.countryCb.getItems().isEmpty());

        // Assert: The combo box has the first country selected
        assertEquals("First country should be selected in combo box",
            "Finland",
            controller.countryCb.getValue());
      } finally {
        latch.countDown();
      }
    });
    latch.await();
  }
}
