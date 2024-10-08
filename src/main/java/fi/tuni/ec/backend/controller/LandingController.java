package fi.tuni.ec.backend.controller;

import fi.tuni.ec.backend.QueryHandler;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;

/**
 * Controller for landing page.
 * Populates filter comboboxes, handles landing page navigation elements.
 */
public class LandingController {
  private LocalDate date;
  private LocalDate curDate;
  private DateState ds;
  private QueryHandler queryHandler;

  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM");
  private final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");

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
   *
   */
  public void saveQuery() {
    String country = countryCb.getValue();
    String region = regionCb.getValue();
    String params = country + ";" + region;
    String name = queryNamePopup();
    queryHandler.saveQuery(name, curDate.toString(), params);
  }

  /**
   * Load query from handler.
   * Gives popup to user to enter query name.
   *
   */
  public void loadQuery() {
    String name = queryNamePopup();
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

  /**
   * Returns a list containing the start and end date of the week.
   *
   * @param date The date to calculate the week from
   *
   * @return List containing the start and end date of the week
   */
  private List<LocalDate> getWeek(LocalDate date) {
    // Gets the day of the week (in english)
    DateTimeFormatter weekDayFormatter = DateTimeFormatter.ofPattern("E");
    String weekDay = weekDayFormatter.format(date);
    // This needs to be refactored to contain
    // no magic numbers and hardcoded value
    return switch (weekDay) {
      case "Mon" -> List.of(date, date.plusDays(6));
      case "Tue" -> List.of(date.minusDays(1), date.plusDays(5));
      case "Wed" -> List.of(date.minusDays(2), date.plusDays(4));
      case "Thu" -> List.of(date.minusDays(3), date.plusDays(3));
      case "Fri" -> List.of(date.minusDays(4), date.plusDays(2));
      case "Sat" -> List.of(date.minusDays(5), date.plusDays(1));
      case "Sun" -> List.of(date.minusDays(6), date);
      default -> null;
    };
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
      return week.get(0).format(dateFormatter) + " - "
          + week.get(1).format(dateFormatter);
    } catch (NullPointerException e) {
      System.out.println("Error: Date null");
      return null;
    }
  }

  /**
   * Returns a list containing the start of the year and given date.
   *
   * @param date The date to calculate the year from
   *
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
  public  void setDateNext() {
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
  }

}
