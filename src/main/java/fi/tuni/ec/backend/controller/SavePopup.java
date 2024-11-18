package fi.tuni.ec.backend.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for save popup.
 * Handles saving queries.
 */
public class SavePopup {
  @FXML
  private TextField queryName;
  @FXML
  private CheckBox saveTime;
  private String name = "";
  private boolean saveTimeSelected = false;

  /**
   * Initializes the popup.
   */
  @FXML
  public void initialize() {
    queryName.setPromptText("Enter query name");
  }

  /**
   * Save query and close popup.
   */
  @FXML
  public void save() {
    name = queryName.getText();
    saveTimeSelected = saveTime.isSelected();

    Stage stage = (Stage) queryName.getScene().getWindow();
    stage.close();
  }

  /**
   * returns the name of the query.
   *
   * @return name of the query
   */
  public String getName() {
    return name;
  }

  /**
   * returns if the time should be saved.
   *
   * @return if the time should be saved
   */
  @FXML
  public boolean getSaveTimeSelected() {
    return saveTimeSelected;
  }
}
