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
  private int dateState;
  private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM");
  private DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");

  @FXML Label dateLabel;

  /**
   * Initializes the controller class.
   */
  @FXML
  public void initialize() {
    date = LocalDateTime.now();
    dateLabel.setText(dateFormatter.format(date));
    dateState = 0;
  }

  /**
   * Controls "next" button for date.
   */
  @FXML
  public void setDateNext() {
    if (dateState == 0) {
      date = date.plusDays(1);
      showDate();
    } else if (dateState == 1) {
      date = date.plusWeeks(1);
      showWeek();
    } else if (dateState == 2) {
      date = date.plusMonths(1);
      showMonth();
    } else if (dateState == 3) {
      date = date.plusYears(1);
      showYear();
    } else if (dateState == 4) {
      date = date.plusYears(1);
      showYtd();
    }
  }

  @FXML
  public void setDatePrev() {
    date = date.minusDays(1);
    dateLabel.setText(dateFormatter.format(date));
  }

  @FXML
  public void showDate() {
    dateLabel.setText(dateFormatter.format(date));
    dateState = 0;
  }

  @FXML
  public void showWeek() {
    dateLabel.setText("NOT IMPLEMENTED");
    dateState = 1;
  }

  @FXML
  public void showMonth() {
    dateLabel.setText(monthFormatter.format(date));
    dateState = 2;
  }

  @FXML
  public void showYear() {
    dateLabel.setText(yearFormatter.format(date));
    dateState = 3;
  }


  @FXML
  public void showYtd() {
    dateLabel.setText("NOT IMPLEMENTED");
    dateState = 4;
  }

}
