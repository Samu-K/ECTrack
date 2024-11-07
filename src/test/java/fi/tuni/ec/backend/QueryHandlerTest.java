package fi.tuni.ec.backend;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.util.Pair;
import junit.framework.TestCase;

/**
 * This class is used for testing QueryHandler.
 */
public class QueryHandlerTest extends TestCase {

  /**
   * Tests the saveQuery and loadQuery methods.
   *
   * @throws Exception if there is an error during testing
   */
  public void testSaveAndLoadQuery() throws Exception {
    File tempFile = File.createTempFile("testLoadQuery", ".csv");
    tempFile.deleteOnExit();

    QueryHandler handler = new QueryHandler(tempFile.getPath());

    String name = "testQuery";
    String modified = "2024-10-23";
    String params = "param1;param2";

    // Saves the query
    handler.saveQuery(name, modified, params);
    // Loads the query
    ArrayList<String> result = handler.loadQuery(name);

    // Assert: Confirms query has been saved and loaded successfully
    assertNotNull("Loaded query is null.", result);
    assertEquals(3, result.size());
    assertEquals(name, result.get(0));
    assertEquals(modified, result.get(1));
    assertEquals(params, result.get(2));

    if (tempFile.exists()) {
      tempFile.delete();
    }
  }

  /**
   * Tests the saveQueryToFile method.
   *
   * @throws Exception if there is an error during testing
   */
  public void testSaveQueryToFile() throws Exception {
    File tempFile = File.createTempFile("testFileSave", ".csv");
    tempFile.deleteOnExit();

    QueryHandler handler = new QueryHandler(tempFile.getPath());

    String name = "testQuery";
    Pair<String, String> query = new Pair<>("2024-10-23", "param1;param2");

    handler.saveQueryToFile(name, query);

    // Assert: Reads the file and confirms if its content was saved successfully
    Scanner scanner = new Scanner(tempFile);

    boolean queryFound = false;
    while (scanner.hasNextLine()) {
      String[] line = scanner.nextLine().split(",");
      String savedName = line[0];
      String savedModified = line[1];
      String savedParams = line[2];
      if (savedName.equals(name) && savedModified.equals(query.getKey()) && savedParams.equals(
          query.getValue())) {
        queryFound = true;
        break;
      }
    }
    scanner.close();

    assertTrue("Saved query was not found in the file.", queryFound);

    if (tempFile.exists()) {
      tempFile.delete();
    }
  }
}
