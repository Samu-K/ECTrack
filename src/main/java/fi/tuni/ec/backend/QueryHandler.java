package fi.tuni.ec.backend;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import javafx.util.Pair;

/**
 * Handles saving and loading queries for the app.
 *
 */
public class QueryHandler {

  private final File queryFile;
  private final String[] headers = {"Name", "Modified", "Parameters"};
  private HashMap<String, Pair<String, String>> queries;

  /**
   * Initialize the file with headers.
   */
  private void initFile() {
    try {
      FileWriter writer = new FileWriter(queryFile);
      writer.append(String.join(",", headers));
      writer.append("\n");
      writer.flush();
      writer.close();
    } catch (Exception e) {
      System.out.println("Error initializing file: " + e.getMessage());
    }
  }

  /**
   * Constructor for QueryHandler.
   *  Initializes the queryFile and creates it if it doesn't exist.
   *  Default path is savedQueries.csv.
   */
  public QueryHandler() {
    this.queryFile = new File("savedQueries.csv");
    this.queries = new HashMap<>();
    if (!queryFile.exists()) {
      initFile();
    } else {
      loadQueries();
    }
  }

  /**
   * Constructor for QueryHandler.
   * Initializes the queryFile and creates it if it doesn't exist.
   *
   * @param path Path to the file.
   */
  public QueryHandler(String path) {
    this.queryFile = new File(path);
    this.queries = new HashMap<>();
    if (!queryFile.exists()) {
      initFile();
    } else {
      loadQueries();
    }
  }

  /**
   * Save the queries to the file.
   */
  public void saveQueries() {
    try {
      FileWriter writer = new FileWriter(queryFile);
      for (HashMap.Entry<String, Pair<String, String>> entry : queries.entrySet()) {
        writer.append(entry.getKey());
        writer.append(",");
        writer.append(entry.getValue().getKey());
        writer.append(",");
        writer.append(entry.getValue().getValue());
        writer.append("\n");
      }
      writer.flush();
      writer.close();
    } catch (Exception e) {
      System.out.println("Error saving queries: " + e.getMessage());
    }
  }

  private void loadQueries() {
    // Load queries from file
    try {
      Scanner scanner = new Scanner(queryFile);
      scanner.nextLine(); // Skip headers
      while (scanner.hasNextLine()) {
        String[] line = scanner.nextLine().split(",");
        queries.put(line[0], new Pair<>(line[1], line[2]));
      }
      scanner.close();
    } catch (Exception e) {
      System.out.println("Error loading queries: " + e.getMessage());
    }
  }

  /**
   * Save query to the file.
   *
   * @param name Name of the query
   *
   * @param modified Date the query was last modified
   *
   * @param params Parameters of the query
   */
  public void saveQuery(String name, String modified, String params) {
    queries.put(name, new Pair<>(modified, params));
  }

  /**
   * Load query from name.
   *
   * @param name Name of the query to load
   * @return ArrayList containing the name, modify date and parameters of the query
   */
  public ArrayList<String> loadQuery(String name) {
    Pair<String, String> query = queries.get(name);
    if (query != null) {
      return new ArrayList<>(List.of(name, query.getKey(), query.getValue()));
    } else {
      System.out.println("Query not found");
    }
    return null;
  }

  public void deleteQuery(String name) {
    queries.remove(name);
  }


}
