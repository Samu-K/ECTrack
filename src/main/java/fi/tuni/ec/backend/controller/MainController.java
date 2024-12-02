package fi.tuni.ec.backend.controller;

import fi.tuni.ec.api.ApiData;
import fi.tuni.ec.api.ApiService;
import java.io.IOException;
import java.util.List;
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
   * Fetch electricity data for a country
   * (options atm: Finland, Germany, Sweden, France)
   * and time period.

   * @param country The country code 
   * @param startDate The start date of the period (format: "YYYYMMDDHHMM")
   * @param endDate The end date of the period (format: "YYYYMMDDHHMM")
   * 
   * @return List of electricity data
   */
  public List<ApiData> fetchData(String country, String startDate, String endDate) {
    try {
      return apiService.fetchData(country, startDate, endDate);
    } catch (Exception e) {
      System.out.println("Error fetching usage data: " + e.getMessage());
      return null;
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
