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
  }

  @FXML
  public void setDateNext() {
    date = date.plusDays(1);
    dateLabel.setText(dateFormatter.format(date));
  }

  @FXML
  public void setDatePrev() {
    date = date.minusDays(1);
    dateLabel.setText(dateFormatter.format(date));
  }

  @FXML
  public void showDate() {
    dateLabel.setText(dateFormatter.format(date));
  }

  @FXML
  public void showWeek() {
    dateLabel.setText("NOT IMPLEMENTED");
  }

  @FXML
  public void showMonth() {
    dateLabel.setText(monthFormatter.format(date));
  }

  @FXML
  public void showYear() {
    dateLabel.setText(yearFormatter.format(date));
  }


  @FXML
  public void showYtd() {
    dateLabel.setText("NOT IMPLEMENTED");
  }

}
