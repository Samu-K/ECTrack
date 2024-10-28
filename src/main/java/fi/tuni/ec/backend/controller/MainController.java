package fi.tuni.ec.backend.controller;

import java.io.IOException;
// import java.time.LocalDate;
// import java.time.YearMonth;
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
   * Fetch electricity usage for a country 
   * (options atm: Finland, Germany, Sweden, France)
   * and time period.

   * @param country The country code 
   * @param startDate The start date of the period (format: "YYYYMMDDHHMM")
   * @param endDate The end date of the period (format: "YYYYMMDDHHMM")
   * 
   * @return List of electricity usage data
   */
  public List<Double> fetchUsage(String country, String startDate, String endDate) {
    try {
      return apiService.fetchUsage(country, startDate, endDate);
    } catch (Exception e) {
      System.out.println("Error fetching usage data: " + e.getMessage());
      return null;
    }
  }

  /**
   * Fetch electricity pricing for a country 
   * (options atm: Finland, Germany, Sweden, France)
   * and time period.

   * @param country The country code 
   * @param startDate The start date of the period (format: "YYYYMMDDHHMM")
   * @param endDate The end date of the period (format: "YYYYMMDDHHMM")
   * 
   * @return List of electricity pricing data
   */
  public List<Double> fetchPricing(String country, String startDate, String endDate) {
    try {
      return apiService.fetchPricing(country, startDate, endDate);
    } catch (Exception e) {
      System.out.println("Error fetching pricing data: " + e.getMessage());
      return null;
    }
  }

  // /**
  //  * Handle the daily view (fetch data for a specific day).
  //  */
  // public void handleDailyView(LocalDate date, String country) {
  //   try {
  //     apiService.fetchDataForDay(country, date);
  //   } catch (Exception e) {
  //     System.out.println("Error fetching data for day: " + e.getMessage());
  //   }
  // }

  // /**
  //  * Handle the weekly view (fetch data for a specific week).
  //  */
  // public void handleWeeklyView(LocalDate startDate, String country) {
  //   try {
  //     apiService.fetchDataForWeek(country, startDate);
  //   } catch (Exception e) {
  //     System.out.println("Error fetching weekly data: " + e.getMessage());
  //   }
  // }

  // /**
  //  * Handle the monthly view (fetch data for a specific month).
  //  */
  // public void handleMonthlyView(YearMonth month, String country) {
  //   try {
  //     apiService.fetchDataForMonth(country, month);
  //   } catch (Exception e) {
  //     System.out.println("Error fetching monthly data: " + e.getMessage());
  //   }
  // }




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
