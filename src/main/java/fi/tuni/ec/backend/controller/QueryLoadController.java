package fi.tuni.ec.backend.controller;

import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * Controller for query load popup.
 * Handles closing the popup/***

 */
public class QueryLoadController {
  @FXML
  private TableView<ConfigFile> tableView;
  @FXML
  private TableColumn<Object, String> name;
  @FXML
  private TableColumn<Object, String> mod;
  @FXML
  private TableColumn<Object, String> param;

  private String queryFileName;



  /**
   * Initializes the popup.
   * Sets up the columns and adds hardcoded rows.
   */
  @FXML
  public void initialize() {
    // Set up the columns to use the ConfigFile class properties
    name.setCellValueFactory(new PropertyValueFactory<>("name"));
    mod.setCellValueFactory(new PropertyValueFactory<>("mod"));
    param.setCellValueFactory(new PropertyValueFactory<>("param"));
  }

  /**
   * Fills the table with queries.
   *
   * @param queries The queries to fill the table with.
   */
  public void fillQueryTable(HashMap<String, Pair<String, String>> queries) {
    ObservableList<ConfigFile> configFiles = FXCollections.observableArrayList();
    queries.forEach((k, v) -> configFiles.add(new ConfigFile(k, v.getKey(), v.getValue())));
    tableView.setItems(configFiles);
  }

  /**
   * Closes the popup.
   *
   */
  public void closePopup() {
    Stage stage = (Stage) tableView.getScene().getWindow();
    stage.close();
  }

  /**
   * Loads the query.
   */
  public void loadQuery() {
    ConfigFile selectedFile = tableView.getSelectionModel().getSelectedItem();
    queryFileName = selectedFile.getName();
    closePopup();
  }

  public String getQueryName() {
    return queryFileName;
  }

  /**
   * Inner class for the TableView data.
   *
   */
  // Inner class for the TableView data
  public static class ConfigFile {
    private final String name;
    private final String mod;
    private final String param;

    /**
     * Constructor for the ConfigFile class.
     *
     * @param name The name of the config file.
     * @param mod The modification date of the config file.
     * @param param The parameter of the config file.
     */
    public ConfigFile(String name, String mod, String param) {
      this.name = name;
      this.mod = mod;
      this.param = param;
    }

    public String getName() {
      return name;
    }

    public String getMod() {
      return mod;
    }

    public String getParam() {
      return param;
    }

  }
}
