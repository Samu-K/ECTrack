package fi.tuni.ec.backend.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


/**
 * Controller for landing page.
 */
public class LandingController {
  private LocalDate date;
  private LocalDate curDate;
  private DateState ds;
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM");
  private final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");

  private enum DateState {
    DAY, WEEK, MONTH, YEAR, YTD
  }

  @FXML Label dateLabel;
  @FXML Button prevDateButton;
  @FXML Button nextDateButton;

  /**
   * Initializes the controller class.
   */
  @FXML
  public void initialize() {
    curDate = LocalDate.now();
    date = curDate;
    dateLabel.setText(dateFormatter.format(date));
    ds = DateState.DAY;
  }

  /**
   * Moves date based on current time period selected.
   *
   * @param offSet Determines if date moves forward or backward
   */
  private void setDate(int offSet) {
    switch (ds) {
      case DAY:
        date = date.plusDays(offSet);
        if (date.isAfter(curDate)) {
          throw new IllegalArgumentException("Date cannot be in the future");
        }
        showDate();
        break;
      case WEEK:
        date = date.plusWeeks(offSet);
        if (date.isAfter(curDate)) {
          throw new IllegalArgumentException("Date cannot be in the future");
        }
        showWeek();
        break;
      case MONTH:
        date = date.plusMonths(offSet);
        if (date.isAfter(curDate)) {
          throw new IllegalArgumentException("Date cannot be in the future");
        }
        showMonth();
        break;
      case YEAR:
        date = date.plusYears(offSet);
        if (date.isAfter(curDate)) {
          throw new IllegalArgumentException("Date cannot be in the future");
        }
        showYear();
        break;
      case YTD:
        date = date.plusYears(offSet);
        if (date.isAfter(curDate)) {
          throw new IllegalArgumentException("Date cannot be in the future");
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

  private String getWeekString(LocalDate date) {
    try {
      return getWeek(date).get(0).format(dateFormatter) + " - "
          + getWeek(date).get(1).format(dateFormatter);
    } catch (NullPointerException e) {
      System.out.println("Error: Date null");
      return null;
    }
  }

  private List<LocalDate> getYtd(LocalDate date) {
    String startDate = "01.01." + date.getYear();
    LocalDate start = LocalDate.parse(startDate, dateFormatter);
    return List.of(start, date);

  }

  private String getYtdString() {
    return getYtd(curDate).get(0).format(dateFormatter) + " - "
        + getYtd(curDate).get(1).format(dateFormatter);
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
