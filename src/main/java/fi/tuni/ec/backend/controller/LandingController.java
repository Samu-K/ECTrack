package fi.tuni.ec.backend.controller;

import fi.tuni.ec.api.ApiData;
import fi.tuni.ec.api.ApiService;
import fi.tuni.ec.backend.QueryHandler;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
  private LocalDate dispDate;
  private LocalDate curDate;
  private DateState ds;
  private QueryHandler queryHandler;

  private static final SavePopup saveHandler = new SavePopup();
  private static final QueryLoadController cr = new QueryLoadController();
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM");
  private final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");

  private NumberAxis lineY = new NumberAxis();
  private CategoryAxis lineX = new CategoryAxis();
  private LineChart<String, Number> lineChart = new LineChart<>(lineX, lineY);
  private NumberAxis barY = new NumberAxis();
  private CategoryAxis barX = new CategoryAxis();
  private BarChart<String, Number> barChart = new BarChart<>(barX, barY);

  private final Alert invalidDateAlert = new Alert(
      Alert.AlertType.ERROR,
      "Date cannot be in the future",
      ButtonType.OK);
  private final Alert queryNotfoundAlert = new Alert(
      Alert.AlertType.ERROR,
      "Query not found",
      ButtonType.OK);

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
  @FXML ComboBox<String> countryCb;
  @FXML private StackPane graphPlaceholder;
  @FXML private StackPane graphPlaceholder2;

  /**
   * Initializes the controller. Sets date to current date and populates comboboxes.
   */
  @FXML
  public void initialize() {
    curDate = LocalDate.now();
    dispDate = curDate;
    dateLabel.setText(dateFormatter.format(dispDate));
    ds = DateState.DAY;
    queryHandler = new QueryHandler();

    countryCb.getItems().addAll(ALL_COUNTRIES);
    countryCb.getSelectionModel().selectFirst();

    // Take this from selected country after country selection implemented
    var country = "Finland";

    // Take these from selected dates
    var periodStart = dispDate.format(DateTimeFormatter.ofPattern("yyyyMMdd0000"));
    var periodEnd = dispDate.format(DateTimeFormatter.ofPattern("yyyyMMdd2300"));

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
    String dateType = ds.toString();

    // show popup for saving query
    showQuerySave();

    // setup params
    String params = country;
    if (saveHandler.getSaveTimeSelected()) {
      params +=  ";" + dispDate + ";" + dateType;
    }

    queryHandler.saveQuery(saveHandler.getName(), curDate.toString(), params);
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

    // ArrayList third value is params in format country;date;dateType
    String[] spl = query.get(2).split(";");

    String country = spl[0];
    countryCb.setValue(country);

    // if date saved
    if (spl.length > 1) {
      String date =  spl[1];
      String dateType = spl[2];
      dispDate = LocalDate.parse(date);
      ds = DateState.valueOf(dateType);
      setDispDate(0);
    }
  }

  /**
   * Shows popup for saving queries.
   */
  private void showQuerySave() {
    // setup stage for popup
    Stage popupStage = new Stage();
    FXMLLoader ld = new FXMLLoader();

    // load fxml and set controller
    ld.setLocation(saveHandler.getClass().getResource("savePopup.fxml"));
    ld.setController(saveHandler);

    // load stage
    try {
      GridPane popGrid = ld.load();
      Scene popupScene = new Scene(popGrid);
      popupStage.setScene(popupScene);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // show window
    popupStage.showAndWait();
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
  private void setDispDate(int offSet) {
    LocalDate newDate;
    switch (ds) {
      case DAY:
        newDate = dispDate.plusDays(offSet);
        if (newDate.isAfter(curDate)) {
          invalidDateAlert.showAndWait();
        } else {
          dispDate = newDate;
        }
        showDate();
        break;
      case WEEK:
        newDate = dispDate.plusWeeks(offSet);
        if (newDate.isAfter(curDate)) {
          invalidDateAlert.showAndWait();
        } else {
          dispDate = newDate;
        }
        showWeek();
        break;
      case MONTH:
        newDate = dispDate.plusMonths(offSet);
        if (newDate.isAfter(curDate)) {
          invalidDateAlert.showAndWait();
        } else {
          dispDate = newDate;
        }
        showMonth();
        break;
      case YEAR:
        newDate = dispDate.plusYears(offSet);
        if (newDate.isAfter(curDate)) {
          invalidDateAlert.showAndWait();
        } else {
          dispDate = newDate;
        }
        showYear();
        break;
      case YTD:
        newDate = dispDate.plusYears(offSet);
        if (newDate.isAfter(curDate)) {
          invalidDateAlert.showAndWait();
        } else {
          dispDate = newDate;
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

    lineY = new NumberAxis();
    lineX = new CategoryAxis();
    barY = new NumberAxis();
    barX = new CategoryAxis();

    lineChart = new LineChart<>(lineX, lineY);
    barChart = new BarChart<>(barX, barY);

    lineY.setLabel("€ / Kw");
    lineChart.setTitle("Electricity Prices");
    barY.setLabel("kWh");
    barChart.setTitle("Electricity Usage");
    barChart.setCategoryGap(3);

    lineChart.getStylesheets().add(
        Objects.requireNonNull(getClass().getResource("landing.css")).toExternalForm());
    barChart.getStylesheets().add(
        Objects.requireNonNull(getClass().getResource("landing.css")).toExternalForm());

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
      assert week != null;
      return week.getFirst().format(dateFormatter) + " - "
          + week.getFirst().format(dateFormatter);
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

  /**
    * Sets date range to display as previous.
    */
  @FXML
  public void setDatePrev() {
    setDispDate(-1);
  }

  /**

   * Sets date range to display as next.
   */
  @FXML
  public  void setDateNext() {
    setDispDate(1);
  }

  /**
   * Sets date range to display as day.
   */
  @FXML
  public void showDate() {
    dateLabel.setText(dateFormatter.format(dispDate));
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
    dateLabel.setText(getWeekString(dispDate));
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
    dateLabel.setText(monthFormatter.format(dispDate));
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
    dateLabel.setText(yearFormatter.format(dispDate));
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
    String country = countryCb.getValue();
    String periodStart;
    String periodEnd;
    List<ApiData> priceData = new ArrayList<>();

    switch (ds) {
      case DAY -> {
        periodStart = dispDate.format(DateTimeFormatter.ofPattern("yyyyMMdd0000"));
        periodEnd = dispDate.format(DateTimeFormatter.ofPattern("yyyyMMdd2300"));
      }
      case WEEK -> {
        List<LocalDate> week = getWeek(dispDate);
        periodStart = week.get(0).format(DateTimeFormatter.ofPattern("yyyyMMdd0000"));
        periodEnd = week.get(6).format(DateTimeFormatter.ofPattern("yyyyMMdd2300"));
      }
      case MONTH -> {
        periodStart = dispDate.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyyMMdd0000"));
        periodEnd = dispDate.withDayOfMonth(
            dispDate.lengthOfMonth()).format(DateTimeFormatter.ofPattern("yyyyMMdd2300")
        );
      }
      case YEAR -> {
        periodStart = dispDate.withDayOfYear(1).format(DateTimeFormatter.ofPattern("yyyyMMdd0000"));
        periodEnd = dispDate.withDayOfYear(
            dispDate.lengthOfYear()).format(DateTimeFormatter.ofPattern("yyyyMMdd2300")
        );
      }
      case YTD -> {
        periodStart = dispDate.withDayOfYear(1).format(DateTimeFormatter.ofPattern("yyyyMMdd0000"));
        periodEnd = dispDate.format(DateTimeFormatter.ofPattern("yyyyMMdd2300"));
      }
      default -> throw new IllegalStateException("Unexpected value: " + ds);
    }

    // For year and ytd, split range into 3 month sequences
    if (ds == DateState.YEAR || ds == DateState.YTD) {
      List<String[]> periods = splitYearlyRange(periodStart, periodEnd);

      for (String[] period : periods) {
        periodStart = period[0];
        periodEnd = period[1];
        List<ApiData> chunkData = fetchAndFormData(country, periodStart, periodEnd);
        if (chunkData != null) {
          priceData.addAll(chunkData);
        }
      }
    } else {
      priceData = fetchAndFormData(country, periodStart, periodEnd);
    }

    if (priceData != null) {
      switch (ds) {
        case DAY -> updateDayGraph(priceData);
        case WEEK -> updateWeekGraph(priceData);
        case MONTH -> updateMonthGraph(priceData);
        case YEAR, YTD -> updateYearGraph(priceData);
        default -> {
          throw new IllegalStateException("Unexpected value: " + ds);
        }
      }
    } else {
      new Alert(
          Alert.AlertType.ERROR, "Error fetching data", ButtonType.OK);
    }
  }

  // Split year range into 3 months to avoid huge data fetches.
  public static List<String[]> splitYearlyRange(String periodStart, String periodEnd) {
    List<String[]> periods = new ArrayList<>();
    var dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    var start = LocalDate.parse(periodStart.substring(0, 8), dateFormatter);
    var end = LocalDate.parse(periodEnd.substring(0, 8), dateFormatter);

    while (start.isBefore(end)) {
      var tempEnd = start.plusMonths(3).minusDays(1);

      if (tempEnd.isAfter(end)) {
        tempEnd = end;
      }

      periods.add(new String[]{start.format(dateFormatter) + "0000", tempEnd.format(dateFormatter) + "2300"});
      start = tempEnd.plusDays(1);
    }
    return periods;
  }

  private void updateDayGraph(List<ApiData> priceData) {
    updateGraphData(
        priceData,
        lineChart.getData().getFirst(),
        barChart.getData().getFirst(),
        "hour");
  }

  private void updateWeekGraph(List<ApiData> priceData) {
    Map<LocalDate, List<ApiData>> groupByDate = priceData.stream()
        .collect(Collectors.groupingBy(data -> data.date.toLocalDate()));

    List<ApiData> dailyAverages = new ArrayList<>(getWeek(dispDate).stream()
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

    updateGraphData(
        dailyAverages,
        lineChart.getData().getFirst(),
        barChart.getData().getFirst(),
        "day");

  }

  private void updateMonthGraph(List<ApiData> priceData) {
    Map<Integer, List<ApiData>> groupByDay = priceData.stream()
        .collect(Collectors.groupingBy(data -> data.date.getDayOfMonth()));

    List<ApiData> dailyAverages = new ArrayList<>();

    for (int day = 1; day <= dispDate.lengthOfMonth(); day++) {
      List<ApiData> dailyData = groupByDay.getOrDefault(day, new ArrayList<>());

      var temp = new ApiData();

      temp.date = LocalDateTime.of(dispDate.getYear(), dispDate.getMonth(), day, 0, 0);
      temp.interval = 1440;

      // If dates have no data or are in the future, replace with zero data. Otherwise, get avg
      setNullToZero(dailyData, temp);

      dailyAverages.add(temp);
    }

    dailyAverages.sort(Comparator.comparing(apiData -> apiData.date));

    updateGraphData(
        dailyAverages,
        lineChart.getData().getFirst(),
        barChart.getData().getFirst(),
        "dayOfMonth");
  }

  private void setNullToZero(List<ApiData> dailyData, ApiData temp) {
    if (!dailyData.isEmpty()) {
      temp.price = dailyData.stream().mapToDouble(data -> data.price).average().orElse(0);
      temp.usage = dailyData.stream().mapToDouble(data -> data.usage).sum();
    } else {
      temp.price = 0;
      temp.usage = 0;
    }
  }

  private void updateYearGraph(List<ApiData> priceData) {
    Map<Month, List<ApiData>> groupedByMonth = priceData.stream()
        .collect(Collectors.groupingBy(data -> data.date.getMonth()));

    List<ApiData> monthlyAverages = new ArrayList<>(Arrays.stream(Month.values())
        .map(month -> {
          List<ApiData> monthlyData = groupedByMonth.getOrDefault(month, new ArrayList<>());

          var temp = new ApiData();
          temp.date = LocalDateTime.of(dispDate.getYear(), month, 1, 0, 0);

          // If dates have no data or are in the future, replace with zero data
          setNullToZero(monthlyData, temp);

          temp.interval = 1440 * month.length(dispDate.isLeapYear());
          return temp;
        }).toList());

    // Sort to ensure that graph displays data correctly
    monthlyAverages.sort(Comparator.comparing(apiData -> apiData.date));

    updateGraphData(
        monthlyAverages,
        lineChart.getData().getFirst(),
        barChart.getData().getFirst(),
        "month");

  }

  private void updateGraphData(List<ApiData> priceData,
                               XYChart.Series<String, Number> priceSeries,
                               XYChart.Series<String, Number> usageSeries,
                               String periodType) {

    priceSeries.getData().clear();
    usageSeries.getData().clear();

    for (var data : priceData) {
      String dataString;
      switch (periodType) {
        case "hour" -> dataString = data.date.getHour() + ":00";
        case "day" -> dataString = data.date.getDayOfWeek().getDisplayName(
            TextStyle.SHORT, Locale.ENGLISH
        );
        case "dayOfMonth" -> dataString = String.valueOf(data.date.getDayOfMonth());
        case "month" -> dataString = data.date.getMonth().getDisplayName(
            TextStyle.SHORT, Locale.ENGLISH
        );
        default -> throw new IllegalArgumentException("Period type unexpected: " + periodType);
      }
      priceSeries.getData().add(new XYChart.Data<>(dataString, data.price));
      usageSeries.getData().add(new XYChart.Data<>(dataString, data.usage));
    }
  }
}

