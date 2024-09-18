package fi.tuni.ec.backend.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

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

      FXMLLoader loader =
          new FXMLLoader(getClass().getResource("/fi/tuni/ec/front/MainPage.fxml"));
      Pane mainPage = loader.load();
      rootLayout.setCenter(mainPage);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
