package fi.tuni.ec.backend.controller;


import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;

/**
 * Shared JavaFX initializer for all test classes.
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
