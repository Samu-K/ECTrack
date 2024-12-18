package fi.tuni.ec.backend.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import junit.framework.TestCase;

/**
 * This class is used for testing LandingController.
 */
public class LandingControllerTest extends TestCase {

  private LandingController controller;


  /**
   * Sets up class for each test.
   *
   * @throws Exception if something goes wrong with JavaFX.
   */
  protected void setUp() throws Exception {
    super.setUp();

    JavaFxInitializer.initializeJavaFx();

    CountDownLatch setupLatch = new CountDownLatch(1);
    Platform.runLater(() -> {
      try {
        controller = new LandingController();
        controller.dateLabel = new Label();
        controller.countryCb = new ComboBox<>();
        controller.nextDateButton = new Button();
        controller.prevDateButton = new Button();
        controller.initialize();
      } finally {
        setupLatch.countDown();
      }
    });
    setupLatch.await();
  }

  /**
   * Tests the initialization of the controller for the landing page.
   *
   * @throws Exception if something goes wrong with JavaFX.
   */
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
            "Germany",
            controller.countryCb.getValue());
      } finally {
        latch.countDown();
      }
    });
    latch.await();
  }

  /**
   * Tests setDispDate in an indirect way, as it's not a public function.
   */
  public void testSetDate() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    Platform.runLater(() -> {
      try {
        controller.setDatePrev();
        String controllerPastDate = controller.dateLabel.getText();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String pastDate = formatter.format(LocalDate.now().minusDays(1));
        assertEquals("Display date should be moved back one day.", pastDate, controllerPastDate);

        String currentDate = formatter.format(LocalDate.now());
        controller.setDateNext();
        String controllerDate = controller.dateLabel.getText();
        assertEquals("Display date should have moved forward to current date.", currentDate,
            controllerDate);
      } finally {
        latch.countDown();
      }
    });
    latch.await();
  }

  /**
   * Tests showMonth to have correct label and visible buttons.
   */
  public void testShowMonth() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    Platform.runLater(() -> {
      try {
        controller.showMonth();
        String monthText = controller.dateLabel.getText();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM");
        String currentMonth = monthFormatter.format(LocalDate.now());

        // Assert: Text label should not be null or empty
        assertNotNull("Month label should not be null.", controller.dateLabel.getText());
        assertFalse("Month label should not be empty.", controller.dateLabel.getText().isEmpty());
        // Assert: Text label has the month as text
        assertEquals("Month label should be correctly set.", currentMonth, monthText);
        // Assert: Buttons should be visible
        assertTrue("Next button should be visible.", controller.nextDateButton.isVisible());
        assertTrue("Prev button should be visible.", controller.prevDateButton.isVisible());
      } finally {
        latch.countDown();
      }
    });
    latch.await();
  }

  /**
   * Tests showYear to have correct label and visible buttons.
   */
  public void testShowYear() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    Platform.runLater(() -> {
      try {
        controller.showYear();
        String yearText = controller.dateLabel.getText();
        DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");
        String currentYear = yearFormatter.format(LocalDate.now());

        // Assert: Text label should not be null or empty
        assertNotNull("Year label should not be null.", controller.dateLabel.getText());
        assertFalse("Year label should not be empty.", controller.dateLabel.getText().isEmpty());
        // Assert: Text label has the month as text
        assertEquals("Year label should be correctly set.", currentYear, yearText);
        // Assert: Buttons should be visible
        assertTrue("Next button should be visible.", controller.nextDateButton.isVisible());
        assertTrue("Prev button should be visible.", controller.prevDateButton.isVisible());
      } finally {
        latch.countDown();
      }
    });
    latch.await();
  }

  /**
   * Tests showYtd to have correct label and button visibility.
   */
  public void testShowYtd() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    Platform.runLater(() -> {
      try {
        controller.showYtd();
        String ytdText = controller.dateLabel.getText();
        DateTimeFormatter ytdFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String startOfYear = "01.01." + LocalDate.now().getYear();
        String expectedYtdText = startOfYear + " - " + ytdFormatter.format(LocalDate.now());

        // Assert: Text label should not be null or empty
        assertNotNull("YTD label should not be null.", controller.dateLabel.getText());
        assertFalse("YTD label should not be empty.", controller.dateLabel.getText().isEmpty());
        // Assert: Text label has the correct year to date text
        assertEquals("YTD label should be correctly set.", expectedYtdText, ytdText);
        // Assert: Buttons should be hidden
        assertFalse("Next button should be hidden.", controller.nextDateButton.isVisible());
        assertFalse("Prev button should be hidden.", controller.prevDateButton.isVisible());
      } finally {
        latch.countDown();
      }
    });
    latch.await();
  }
}
