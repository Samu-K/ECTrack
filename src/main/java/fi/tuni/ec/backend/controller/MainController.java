package fi.tuni.ec.backend.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
  private ApiService apiService;

  /**
   * Constructor for MainController.
   *
   * @param primaryStage The primary stage
   */
  public MainController(Stage primaryStage) {
    this.primaryStage = primaryStage;
    this.apiService = new ApiService();
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
   * Method to fetch electricity pricing data from the API.
   *
   * For now just print the data in a nice format.
   */
  public void fetchData() {
    String url = "https://api.porssisahko.net/v1/latest-prices.json";
    try {
      // Fetch the data
      List<Map<String, Object>> pricingData = apiService.fetchElectricityPricing(url);

      // For now just print the data in a nice format
      pricingData.forEach(System.out::println);

    } catch (IOException e) {
      System.out.println("Error fetching data: " + e.getMessage());
    }
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
