package fi.tuni.ec.backend.controller;


import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;

/**
 * Shared JavaFX initializer for all test classes.
 * Created due to multiple test classes requiring initialization of JFX toolkit,
 * but running into IllegalStateException errors as toolkit was initialized multiple times
 * when executing all tests, as JFX was initialized separately in each test class.
 */
public class JavaFxInitializer {

  private static boolean initialized = false;
  private static final CountDownLatch latch = new CountDownLatch(1);

  /**
   * Initializes JavaFX if it is not initialized already.
   *
   * @throws InterruptedException if JavaFX fails to be initialized.
   */
  public static void initializeJavaFx() throws InterruptedException {
    if (!initialized) {
      Platform.startup(() -> {
        latch.countDown();
      });
      latch.await();
      initialized = true;
    }
  }
}
