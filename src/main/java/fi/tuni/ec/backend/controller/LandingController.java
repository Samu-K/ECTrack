package fi.tuni.ec.backend.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    dateLabel.setText("NOT IMPLEMENTED");
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
