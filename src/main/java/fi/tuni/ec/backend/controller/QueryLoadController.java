package fi.tuni.ec.backend.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

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

    // Add hardcoded rows
    ObservableList<ConfigFile> configFiles = FXCollections.observableArrayList(
        new ConfigFile("ExampleConfig1", "11.2.2024", "param1"),
        new ConfigFile("ExampleConfig2", "11.5.2024", "param2"),
        new ConfigFile("ExampleConfig3", "11.3.2024", "param3")
    );

    // Set the items in the table
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
    System.out.println("TEST");
  }

  public String getQueryName() {
    return queryFileName;
  }
}
