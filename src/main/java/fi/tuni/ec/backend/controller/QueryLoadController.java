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
 * Shows a tabular view of the saved queries.
 */
public class QueryLoadController {
  @FXML
  private TableView<ConfigFile> queryTable;
  @FXML
  private TableColumn<Object, String> name;
  @FXML
  private TableColumn<Object, String> dateModified;
  @FXML
  private TableColumn<Object, String> params;

  private String queryFileName = "";



  /**
   * Initializes the popup.
   * Sets up the columns to use the ConfigFile class properties.
   */
  @FXML
  public void initialize() {
    // Set up the columns to use the ConfigFile class properties
    name.setCellValueFactory(new PropertyValueFactory<>("name"));
    dateModified.setCellValueFactory(new PropertyValueFactory<>("dateModified"));
    params.setCellValueFactory(new PropertyValueFactory<>("params"));
  }

  /**
   * Fills the table with queries.
   *
   * @param queries The queries to fill the table with.
   */
  public void fillQueryTable(HashMap<String, Pair<String, String>> queries) {
    ObservableList<ConfigFile> configFiles = FXCollections.observableArrayList();
    queries.forEach((k, v) -> configFiles.add(new ConfigFile(k, v.getKey(), v.getValue())));
    queryTable.setItems(configFiles);
  }

  /**
   * Closes the popup.
   */
  public void closePopup() {
    Stage stage = (Stage) queryTable.getScene().getWindow();
    stage.close();
  }

  /**
   * Gets the name of selected query and closes the popup.
   */
  public void loadQuery() {
    ConfigFile selectedFile = queryTable.getSelectionModel().getSelectedItem();
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
  public static class ConfigFile {
    private final String name;
    private final String dateModified;
    private final String params;

    /**
     * Constructor for the ConfigFile class.
     *
     * @param name The name of the config file.
     * @param dateModified The modification date of the config file.
     * @param params The parameters of the config file.
     */
    public ConfigFile(String name, String dateModified, String params) {
      this.name = name;
      this.dateModified = dateModified;
      this.params = params;
    }

    public String getName() {
      return name;
    }

    public String getDateModified() {
      return dateModified;
    }

    public String getParams() {
      return params;
    }

  }
}
