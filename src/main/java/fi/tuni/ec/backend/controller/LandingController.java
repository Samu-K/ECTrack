package fi.tuni.ec.backend.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


/**
 * Controller for landing page.
 */
public class LandingController {
  private LocalDateTime date;
  private DateState ds;
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM");
  private final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");

  private enum DateState {
    DAY, WEEK, MONTH, YEAR, YTD
  }

  @FXML Label dateLabel;

  /**
   * Initializes the controller class.
   */
  @FXML
  public void initialize() {
    date = LocalDateTime.now();
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
        showDate();
        break;
      case WEEK:
        date = date.plusWeeks(offSet);
        showWeek();
        break;
      case MONTH:
        date = date.plusMonths(offSet);
        showMonth();
        break;
      case YEAR:
        date = date.plusYears(offSet);
        showYear();
        break;
      case YTD:
        date = date.plusYears(offSet);
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
  private List<LocalDateTime> getWeek(LocalDateTime date) {
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

  private String getWeekString(LocalDateTime date) {
    try {
      return getWeek(date).get(0).format(dateFormatter) + " - "
          + getWeek(date).get(1).format(dateFormatter);
    } catch (NullPointerException e) {
      System.out.println("Error: Date null");
      return null;
    }
  }

  @FXML
  public void setDatePrev() {
    setDate(-1);
  }

  @FXML
  public  void setDateNext() {
    setDate(1);
  }

  @FXML
  public void showDate() {
    dateLabel.setText(dateFormatter.format(date));
    ds = DateState.DAY;
  }

  @FXML
  public void showWeek() {
    dateLabel.setText(getWeekString(date));
    ds = DateState.WEEK;
  }

  @FXML
  public void showMonth() {
    dateLabel.setText(monthFormatter.format(date));
    ds = DateState.MONTH;
  }

  @FXML
  public void showYear() {
    dateLabel.setText(yearFormatter.format(date));
    ds = DateState.YEAR;
  }

  @FXML
  public void showYtd() {
    dateLabel.setText("NOT IMPLEMENTED");
    ds = DateState.YTD;
  }

}
