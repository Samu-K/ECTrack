package fi.tuni.ec.backend.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * Controller for query load popup.
 * Handles closing the popup/***

 */
public class QueryLoadController {

  @FXML
  private TableView<Object> tableView;

  public void closePopup(ActionEvent actionEvent) {
    Stage stage = (Stage) tableView.getScene().getWindow();
    stage.close();
  }
}
