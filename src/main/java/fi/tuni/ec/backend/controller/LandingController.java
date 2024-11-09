package fi.tuni.ec.backend.controller;

import fi.tuni.ec.api.ApiData;
import fi.tuni.ec.api.ApiService;
import fi.tuni.ec.backend.QueryHandler;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Controller for landing page.
 * Populates filter comboboxes, handles landing page navigation elements.
 */
public class LandingController {
  private LocalDate date;
  private LocalDate curDate;
  private DateState ds;
  private QueryHandler queryHandler;

  private static final QueryLoadController cr = new QueryLoadController();
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM");
  private final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");

  private NumberAxis lineYAxis = new NumberAxis();
  private CategoryAxis lineXAxis = new CategoryAxis();
  private LineChart<String, Number> lineChart = new LineChart<>(lineXAxis, lineYAxis);
  private NumberAxis barYAxis = new NumberAxis();
  private CategoryAxis barXAxis = new CategoryAxis();
  private BarChart<String, Number> barChart = new BarChart<>(barXAxis, barYAxis);

  private final Alert invalidDateAlert = new Alert(
      Alert.AlertType.ERROR,
      "Date cannot be in the future",
      ButtonType.OK);
  private final Alert queryNotfoundAlert = new Alert(
      Alert.AlertType.ERROR,
      "Query not found",
      ButtonType.OK);

  private static final List<String> REGIONS = List.of(
      "Europe", "Asia", "Africa", "Americas", "Oceania");
  private static final List<String> ALL_COUNTRIES = List.of(
      "Finland", "Sweden", "Norway", "Denmark", "Iceland", "Estonia", "Latvia", "Lithuania"
  );

  // describes what time period is currently selected
  private enum DateState {
    DAY, WEEK, MONTH, YEAR, YTD
  }

  // FXML elements
  @FXML Label dateLabel;
  @FXML Button prevDateButton;
  @FXML Button nextDateButton;
  @FXML ComboBox<String> regionCb;
  @FXML ComboBox<String> countryCb;
  @FXML private StackPane graphPlaceholder;
  @FXML private StackPane graphPlaceholder2;

  /**
   * Initializes the controller. Sets date to current date and populates comboboxes.
   */
  @FXML
  public void initialize() {
    curDate = LocalDate.now();
    date = curDate;
    dateLabel.setText(dateFormatter.format(date));
    ds = DateState.DAY;
    queryHandler = new QueryHandler();

    regionCb.getItems().addAll(REGIONS);
    regionCb.getSelectionModel().selectFirst();
    countryCb.getItems().addAll(ALL_COUNTRIES);
    countryCb.getSelectionModel().selectFirst();

    // Take this from selected country after country selection implemented
    var country = "Finland";

    // Take these from selected dates
    var periodStart = date.format(DateTimeFormatter.ofPattern("yyyyMMdd0000"));
    var periodEnd = date.format(DateTimeFormatter.ofPattern("yyyyMMdd2300"));

    var priceData = fetchAndFormData(country, periodStart, periodEnd);

    if (priceData != null) {
      createGraph(priceData);
    } else {
      new Alert(Alert.AlertType.ERROR, "Error fetching data", ButtonType.OK);
    }
  }

  /**
   * Gives popup to user to enter query name.
   *
   * @return Name of the query.
   */
  private String queryNamePopup() {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Save query");
    dialog.setHeaderText("Save query");
    dialog.setContentText("Enter query name:");
    dialog.showAndWait();
    return dialog.getEditor().getText();
  }

  /**
   * Save current query to handler.
   * Gives popup to user to enter query name.
   */
  public void saveQuery() {
    String country = countryCb.getValue();
    String region = regionCb.getValue();
    String params = country + ";" + region;
    String name = queryNamePopup();
    queryHandler.saveQuery(name, curDate.toString(), params);
  }

  /**
   * Sets params for query to UI.
   *
   * @param name Name of the query
   */
  private void setQueryParams(String name) {
    ArrayList<String> query = queryHandler.loadQuery(name);
    if (query == null) {
      queryNotfoundAlert.showAndWait();
      return;
    }
    // ArrayList third value is params in format country;region
    String country = query.get(2).split(";")[0];
    String region = query.get(2).split(";")[1];
    countryCb.setValue(country);
    regionCb.setValue(region);
  }

  /**
   * Shows popup for loading queries.
   */
  private void showQueryLoadPopup() {
    // setup stage for popup
    Stage popupStage = new Stage();
    FXMLLoader ld = new FXMLLoader();

    // load fxml and set controller
    ld.setLocation(cr.getClass().getResource("queryPopup.fxml"));
    ld.setController(cr);

    // load stage
    try {
      GridPane popGrid = ld.load();
      Scene popupScene = new Scene(popGrid);
      popupStage.setScene(popupScene);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // fill queries
    cr.fillQueryTable(queryHandler.getQueries());

    // show window
    popupStage.showAndWait();
  }

  /**
   * Load query from handler.
   * Gives popup to user to enter query name.
   */
  public void loadQuery() {
    // let user select from saved queries
    showQueryLoadPopup();
    String name = cr.getQueryName();

    if (name.isEmpty()) {
      return;
    }

    // set params for search
    setQueryParams(name);
  }

  /**
   * Moves date based on current time period selected.
   *
   * @param offSet Determines if date moves forward or backward
   */
  private void setDate(int offSet) {
    LocalDate newDate;
    switch (ds) {
      case DAY:
        newDate = date.plusDays(offSet);
        if (newDate.isAfter(curDate)) {
          invalidDateAlert.showAndWait();
        } else {
          date = newDate;
        }
        showDate();
        break;
      case WEEK:
        newDate = date.plusWeeks(offSet);
        if (newDate.isAfter(curDate)) {
          invalidDateAlert.showAndWait();
        } else {
          date = newDate;
        }
        showWeek();
        break;
      case MONTH:
        newDate = date.plusMonths(offSet);
        if (newDate.isAfter(curDate)) {
          invalidDateAlert.showAndWait();
        } else {
          date = newDate;
        }
        showMonth();
        break;
      case YEAR:
        newDate = date.plusYears(offSet);
        if (newDate.isAfter(curDate)) {
          invalidDateAlert.showAndWait();
        } else {
          date = newDate;
        }
        showYear();
        break;
      case YTD:
        newDate = date.plusYears(offSet);
        if (newDate.isAfter(curDate)) {
          invalidDateAlert.showAndWait();
        } else {
          date = newDate;
        }
        showYtd();
        break;
      default:
        break;
    }
  }

  private void createGraph(List<ApiData> priceData) {
    clearCharts();

    updateDayGraph(priceData);
  }

  // Clear charts for clean update
  private void clearCharts() {
    graphPlaceholder.getChildren().clear();
    graphPlaceholder2.getChildren().clear();

    lineYAxis = new NumberAxis();
    lineXAxis = new CategoryAxis();
    barYAxis = new NumberAxis();
    barXAxis = new CategoryAxis();

    lineChart = new LineChart<>(lineXAxis, lineYAxis);
    barChart = new BarChart<>(barXAxis, barYAxis);

    lineYAxis.setLabel("€ / Kw");
    lineChart.setTitle("Electricity Prices");
    barYAxis.setLabel("kWh");
    barChart.setTitle("Electricity Usage");
    barChart.setCategoryGap(3);

    lineChart.getStylesheets().add(getClass().getResource("landing.css").toExternalForm());
    barChart.getStylesheets().add(getClass().getResource("landing.css").toExternalForm());

    XYChart.Series<String, Number> priceSeries = new XYChart.Series<>();
    XYChart.Series<String, Number> usageSeries = new XYChart.Series<>();
    priceSeries.setName("Electricity Prices");
    usageSeries.setName("Electricity Usage");

    lineChart.getData().add(priceSeries);
    barChart.getData().add(usageSeries);

    graphPlaceholder.getChildren().add(lineChart);
    graphPlaceholder2.getChildren().add(barChart);
  }

  /**
   * Returns a list containing the start and end date of the week.
   *
   * @param date The date to calculate the week from
   * @return List containing the start and end date of the week
   */
  private List<LocalDate> getWeek(LocalDate date) {
    LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);
    List<LocalDate> week = new ArrayList<>();
    for (int i = 0; i < 7; i++) {
      week.add(startOfWeek.plusDays(i));
    }
    return week;
  }

  /**
   * Create a string with the start and end date of the week.
   *
   * @param date The date to calculate the week from
   * @return String in format dd.MM.yyyy - dd.MM.yyyy
   */
  private String getWeekString(LocalDate date) {
    List<LocalDate> week = getWeek(date);
    try {
      return week.getFirst().format(dateFormatter) + " - " + week.getLast().format(dateFormatter);
    } catch (NullPointerException e) {
      System.out.println("Error: Date null");
      return null;
    }
  }

  /**
   * Returns a list containing the start of the year and given date.
   *
   * @param date The date to calculate the year from
   * @return List containing the start of the year and given date
   */
  private List<LocalDate> getYtd(LocalDate date) {
    String startDate = "01.01." + date.getYear();
    LocalDate start = LocalDate.parse(startDate, dateFormatter);
    return List.of(start, date);

  }

  /**
   * Creates a string with the start of the year and current date.
   *
   * @return String with format dd.MM.yyyy - dd.MM.yyyy
   */
  private String getYtdString() {
    List<LocalDate> ytd = getYtd(curDate);
    return ytd.get(0).format(dateFormatter) + " - "
        + ytd.get(1).format(dateFormatter);
  }

  @FXML
  public void setDatePrev() {
    setDate(-1);
  }

  @FXML
  public void setDateNext() {
    setDate(1);
  }

  /**
   * Sets date range to display as day.
   */
  @FXML
  public void showDate() {
    dateLabel.setText(dateFormatter.format(date));
    ds = DateState.DAY;
    nextDateButton.setVisible(true);
    prevDateButton.setVisible(true);
    clearCharts();
    updateGraph();
  }

  /**
   * Sets date range to display as week.
   */
  @FXML
  public void showWeek() {
    dateLabel.setText(getWeekString(date));
    ds = DateState.WEEK;
    nextDateButton.setVisible(true);
    prevDateButton.setVisible(true);
    clearCharts();
    updateGraph();
  }

  /**
   * Sets date range to display as month.
   */
  @FXML
  public void showMonth() {
    dateLabel.setText(monthFormatter.format(date));
    ds = DateState.MONTH;
    nextDateButton.setVisible(true);
    prevDateButton.setVisible(true);
    clearCharts();
    updateGraph();
  }

  /**
   * Sets date range to display as year.
   */
  @FXML
  public void showYear() {
    dateLabel.setText(yearFormatter.format(date));
    ds = DateState.YEAR;
    nextDateButton.setVisible(true);
    prevDateButton.setVisible(true);
    clearCharts();
    updateGraph();
  }

  /**
   * Sets date range to display as year to date.
   */
  @FXML
  public void showYtd() {
    dateLabel.setText(getYtdString());
    ds = DateState.YTD;
    nextDateButton.setVisible(false);
    prevDateButton.setVisible(false);
    clearCharts();
    updateGraph();
  }

  // Copied from MainController for now
  private List<ApiData> fetchAndFormData(String country, String periodStart, String periodEnd) {
    try {
      var apiService = new ApiService();

      var fetchedData = apiService.fetchData(country, periodStart, periodEnd);

      // Ensure data fetched from api is really between the chosen period
      // since api has returned data couple hours off from requested
      var formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
      var startTime = LocalDateTime.parse(periodStart, formatter);
      var endTime = LocalDateTime.parse(periodEnd, formatter);

      return fetchedData.stream()
          .filter(data -> !data.date.isBefore(startTime) && !data.date.isAfter(endTime))
          .collect(Collectors.toList());
    } catch (IOException e) {
      System.out.println("Error fetching data: " + e.getMessage());
      return null;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void updateGraph() {
    var country = countryCb.getValue();
    String periodStart, periodEnd;

    switch (ds) {
      case DAY -> {
        periodStart = date.format(DateTimeFormatter.ofPattern("yyyyMMdd0000"));
        periodEnd = date.format(DateTimeFormatter.ofPattern("yyyyMMdd2300"));
      }
      case WEEK -> {
        List<LocalDate> week = getWeek(date);
        periodStart = week.get(0).format(DateTimeFormatter.ofPattern("yyyyMMdd0000"));
        periodEnd = week.get(6).format(DateTimeFormatter.ofPattern("yyyyMMdd2300"));
      }
      case MONTH -> {
        periodStart = date.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyyMMdd0000"));
        periodEnd = date.withDayOfMonth(date.lengthOfMonth()).format(DateTimeFormatter.ofPattern("yyyyMMdd2300"));
      }
      case YEAR -> {
        periodStart = date.withDayOfYear(1).format(DateTimeFormatter.ofPattern("yyyyMMdd0000"));
        periodEnd = date.withDayOfYear(date.lengthOfYear()).format(DateTimeFormatter.ofPattern("yyyyMMdd2300"));
      }
      case YTD -> {
        periodStart = date.withDayOfYear(1).format(DateTimeFormatter.ofPattern("yyyyMMdd0000"));
        periodEnd = date.format(DateTimeFormatter.ofPattern("yyyyMMdd2300"));
      }
      default -> throw new IllegalStateException("Unexpected value: " + ds);
    }
    var priceData = fetchAndFormData(country, periodStart, periodEnd);

    if (priceData != null) {
      switch (ds) {
        case DAY -> updateDayGraph(priceData);
        case WEEK -> updateWeekGraph(priceData);
        case MONTH -> updateMonthGraph(priceData);
        case YEAR, YTD -> updateYearGraph(priceData);
      }
    } else {
      new Alert(
          Alert.AlertType.ERROR, "Error fetching data", ButtonType.OK);
    }
  }

  private void updateDayGraph(List<ApiData> priceData) {
    updateGraphData(priceData, lineChart.getData().get(0), barChart.getData().get(0), "hour");
  }

  private void updateWeekGraph(List<ApiData> priceData) {
    Map<LocalDate, List<ApiData>> groupByDate = priceData.stream()
        .collect(Collectors.groupingBy(data -> data.date.toLocalDate()));

    List<ApiData> dailyAverages = new ArrayList<>(getWeek(date).stream()
        .map(weekDate -> {
          var dailyData = groupByDate.getOrDefault(weekDate, Collections.emptyList());

          var temp = new ApiData();

          temp.date = weekDate.atStartOfDay();
          temp.interval = 1440;

          // If dates have no data or are in the future, replace with zero data. Otherwise, get avg
          if (!dailyData.isEmpty()) {
            temp.price = dailyData.stream()
                .mapToDouble(data -> data.price)
                .average()
                .orElse(0.0);
            temp.usage = dailyData.stream()
                .mapToDouble(data -> data.usage)
                .sum();
          } else {
            temp.price = 0.0;
            temp.usage = 0.0;
          }

          return temp;
        }).toList());

    dailyAverages.sort(Comparator.comparing(apiData -> apiData.date));

    updateGraphData(dailyAverages, lineChart.getData().get(0), barChart.getData().get(0), "day");

  }

  private void updateMonthGraph(List<ApiData> priceData) {
    Map<Integer, List<ApiData>> groupByDay = priceData.stream()
        .collect(Collectors.groupingBy(data -> data.date.getDayOfMonth()));

    List<ApiData> dailyAverages = new ArrayList<>();

    for (int day = 1; day <= date.lengthOfMonth(); day++) {
      List<ApiData> dailyData = groupByDay.getOrDefault(day, new ArrayList<>());

      var temp = new ApiData();

      temp.date = LocalDateTime.of(date.getYear(), date.getMonth(), day, 0, 0);
      temp.interval = 1440;

      // If dates have no data or are in the future, replace with zero data. Otherwise, get avg
      if (!dailyData.isEmpty()) {
        temp.price = dailyData.stream().mapToDouble(data -> data.price).average().orElse(0);
        temp.usage = dailyData.stream().mapToDouble(data -> data.usage).sum();
      } else {
        temp.price = 0;
        temp.usage = 0;
      }

      dailyAverages.add(temp);
    }

    dailyAverages.sort(Comparator.comparing(apiData -> apiData.date));

    updateGraphData(dailyAverages, lineChart.getData().get(0), barChart.getData().get(0), "dayOfMonth");
  }

  private void updateYearGraph(List<ApiData> priceData) {
    Map<Month, List<ApiData>> groupedByMonth = priceData.stream()
        .collect(Collectors.groupingBy(data -> data.date.getMonth()));

    List<ApiData> monthlyAverages = new ArrayList<>(Arrays.stream(Month.values())
        .map(month -> {
          List<ApiData> monthlyData = groupedByMonth.getOrDefault(month, new ArrayList<>());

          var temp = new ApiData();
          temp.date = LocalDateTime.of(date.getYear(), month, 1, 0, 0);

          // If dates have no data or are in the future, replace with zero data
          if (!monthlyData.isEmpty()) {
            temp.price = monthlyData.stream().mapToDouble(data -> data.price).average().orElse(0);
            temp.usage = monthlyData.stream().mapToDouble(data -> data.usage).sum();
          } else {
            temp.price = 0;
            temp.usage = 0;
          }

          temp.interval = 1440 * month.length(date.isLeapYear());
          return temp;
        }).toList());

    // Sort to ensure that graph displays data correctly
    monthlyAverages.sort(Comparator.comparing(apiData -> apiData.date));

    updateGraphData(monthlyAverages, lineChart.getData().get(0), barChart.getData().get(0), "month");

  }

  private void updateGraphData(List<ApiData> priceData, XYChart.Series<String, Number> priceSeries, XYChart.Series<String, Number> usageSeries, String periodType) {
    priceSeries.getData().clear();
    usageSeries.getData().clear();

    for (var data : priceData) {
      String xValue;
      switch (periodType) {
        case "hour" -> xValue = data.date.getHour() + ":00";
        case "day" -> xValue = data.date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        case "dayOfMonth" -> xValue = String.valueOf(data.date.getDayOfMonth());
        case "month" -> xValue = data.date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        default -> throw new IllegalArgumentException("Period type unexpected: " + periodType);
      }
      priceSeries.getData().add(new XYChart.Data<>(xValue, data.price));
      usageSeries.getData().add(new XYChart.Data<>(xValue, data.usage));
    }

    /*
    for (XYChart.Data<String, Number> data : usageSeries.getData()) {
      System.out.println("Date: " + data.getXValue() + ", Usage: " + data.getYValue());
    }
    */
  }
}

