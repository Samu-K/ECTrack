package fi.tuni.ec.backend.controller;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Controller for main view.
 */
public class MainController {
  private Stage primaryStage;
  private BorderPane rootLayout;

  /**
   * Constructor for MainController.
   *
   * @param primaryStage The primary stage
   */
  public MainController(Stage primaryStage) {
    this.primaryStage = primaryStage;
    initRootLayout();
    showMainPage();
  }

  private void initRootLayout() {
    rootLayout = new BorderPane();
    Scene scene = new Scene(rootLayout);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * Show the main page.
   */
  public void showMainPage() {
    try {
      // Set size
      primaryStage.getScene().getWindow().setWidth(1000);
      primaryStage.getScene().getWindow().setHeight(650);

      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(MainController.class.getResource("landing.fxml"));
      BorderPane mainPage = loader.load();
      primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("logo.png")));
      rootLayout.setCenter(mainPage);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
