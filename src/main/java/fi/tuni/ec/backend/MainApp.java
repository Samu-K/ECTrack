package fi.tuni.ec.backend;

import fi.tuni.ec.backend.controller.MainController;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The main class of the application.
 */
public class MainApp extends Application {
  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("ECTrack");
    new MainController(primaryStage);
  }

  public static void main(String[] args) {
    launch(args);
  }

}
