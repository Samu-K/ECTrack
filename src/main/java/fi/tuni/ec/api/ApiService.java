package fi.tuni.ec.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javafx.util.Pair;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * Service for fetching data from the API.
 */
public class ApiService {

  private static final String API_URL = "https://web-api.tp.entsoe.eu/api";
  private static String apiKey;
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd'T'HH:mm'Z'");

  // Map for storing area (country) codes
  public static final Map<String, String> COUNTRY_CODES = Map.of(
      "Finland", "10YFI-1--------U",
      "Germany", "10YDE-1--------W",
      "France", "10YFR-RTE------C",
      "Sweden", "10YSE-1--------K"
  );

  // Map for storing lattitude and longitude (Capital city)
  public static final Map<String, Pair<Double, Double>> COUNTRY_COORDINATES = Map.of(
      "Finland", new Pair<>(60.17, 24.94), // ~Helsinki
      "Germany", new Pair<>(52.52, 13.40), // ~Berlin
      "France", new Pair<>(48.86, 2.35), // ~Paris
      "Sweden", new Pair<>(59.33, 18.07) // ~Stockholm
  );

  public static final Map<String, String> COUNTRY_TIMEZONES = Map.of(
      "Finland", "Europe/Helsinki",
      "Germany", "Europe/Berlin",
      "France", "Europe/Paris",
      "Sweden", "Europe/Stockholm"
  );

  // Static block to load the API key from config.properties
  static {
    try (InputStream input = ApiService.class.getResourceAsStream("config.properties")) {
      Properties prop = new Properties();
      if (input == null) {
        throw new IOException("Could not find config.properties");
      }
      prop.load(input);
      apiKey = prop.getProperty("entsoe_api_key");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Helper method to get the API key
  public static String getApiKey() {
    return apiKey;
  }

  /**
   * Get the response stream from the API.
   *
   * @param query The query string
   *
   * @return InputStream
   * @throws Exception if an error occurs
   */
  private InputStream getResponseStream(String query) throws Exception {
    URI uri = new URI(API_URL + query);
    URL url = uri.toURL();
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");

    if (connection.getResponseCode() == 200) {
      return connection.getInputStream();
    } else {
      throw new IOException("Failed to fetch pricing data: HTTP " + connection.getResponseCode());
    }
  }

  /**
   * Fetch data from the API.
   *
   * @param country The country code
   * @param periodStart The start date of the period
   * @param periodEnd The end date of the period
   *
   * @return List of ApiData
   *
   * @throws Exception if an error occurs
   */
  public List<ApiData> fetchData(String country, String periodStart, String periodEnd)
      throws Exception {
    List<ApiData> dataList = new ArrayList<>();

    String areaDomain = COUNTRY_CODES.get(country);

    String priceQuery = String.format(
        "?securityToken=%s&documentType=A44"
            + "&processType=A16&in_Domain=%s&out_Domain=%s&periodStart=%s&periodEnd=%s",
        getApiKey(), areaDomain, areaDomain, periodStart, periodEnd);

    String usageQuery = String.format(
        "?securityToken=%s&documentType=A65"
            + "&processType=A16&outBiddingZone_Domain=%s&periodStart=%s&periodEnd=%s",
        getApiKey(), areaDomain, periodStart, periodEnd);

    InputStream priceStream = getResponseStream(priceQuery);
    InputStream usageStream = getResponseStream(usageQuery);

    Pair<Double, Double> coordinates = COUNTRY_COORDINATES.get(country);
    Map<String, Double> temperatureData = fetchTemperatureData(
        coordinates.getKey(), coordinates.getValue(), periodStart, periodEnd,
        COUNTRY_TIMEZONES.get(country));

    List<ApiData> priceData = parseResponse(priceStream, "price", temperatureData);
    List<ApiData> usageData = parseResponse(usageStream, "usage", null);

    priceStream.close();
    usageStream.close();

    // Combine the two lists
    for (int i = 0; i < priceData.size(); i++) {
      ApiData data = new ApiData();
      data.price = priceData.get(i).price;
      data.usage = usageData.get(i).usage;
      data.date = priceData.get(i).date;
      data.interval = priceData.get(i).interval;
      data.temperatureMean = priceData.get(i).temperatureMean;
      dataList.add(data);
    }

    return dataList;
  }

  /**
   * Fetch daily average temperature data for a given location and period.
   *
   * @param latitude The latitude of the location
   * @param longitude The longitude of the location
   * @param periodStart The start date of the period
   * @param periodEnd The end date of the period
   * @param timezone The timezone of the location
   * @return A map with dates as keys and average temperatures as values
   * @throws Exception if an error occurs
   */
  public Map<String, Double> fetchTemperatureData(
      double latitude,
      double longitude,
      String periodStart,
      String periodEnd,
      String timezone
  ) throws Exception {
    String urlTemplate = "https://archive-api.open-meteo.com/v1/archive?latitude=%f&longitude=%f"
        + "&start_date=%s&end_date=%s&timezone=%s"
        + "&daily=temperature_2m_min&daily=temperature_2m_max";
    String apiUrl = String.format(
        urlTemplate, latitude, longitude, periodStart, periodEnd, timezone);

    URI uri = new URI(apiUrl);
    URL url = uri.toURL();
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");

    if (connection.getResponseCode() != 200) {
      int responseCode = connection.getResponseCode();
      throw new IOException("Failed to fetch temperature data: HTTP " + responseCode);
    }

    // Parse JSON response
    InputStreamReader reader = new InputStreamReader(connection.getInputStream());
    JsonObject responseJson = JsonParser.parseReader(reader).getAsJsonObject();
    JsonObject dailyData = responseJson.getAsJsonObject("daily");
    JsonArray dates = dailyData.getAsJsonArray("time");
    JsonArray minTemps = dailyData.getAsJsonArray("temperature_2m_min");
    JsonArray maxTemps = dailyData.getAsJsonArray("temperature_2m_max");

    Map<String, Double> temperatureMap = new LinkedHashMap<>();

    for (int i = 0; i < dates.size(); i++) {
      double min = minTemps.get(i).getAsDouble();
      double max = maxTemps.get(i).getAsDouble();
      double mean = (min + max) / 2; // Calculate the mean temperature
      temperatureMap.put(dates.get(i).getAsString(), mean);
    }

    return temperatureMap;
  }


  /**
   * Parse response into a list of ApiData, integrating temperature data.
   *
   * @param responseStream The response stream from API
   * @param type The type of data to parse (price or usage)
   * @param temperatureData Map of the temperature data keyed by date (nullable if not needed)
   * @return List of ApiData
   * @throws Exception if an error occurs
   */
  private List<ApiData> parseResponse(InputStream responseStream, String type,
      Map<String, Double> temperatureData) throws Exception {

    String dataString = switch (type) {
      case "price" -> "price.amount";
      case "usage" -> "quantity";
      default -> throw new IllegalArgumentException("Invalid type: " + type);
    };

    Document document = DocumentBuilderFactory.newInstance()
        .newDocumentBuilder().parse(responseStream);
    List<ApiData> dataList = new ArrayList<>();

    // Define namespaces
    document.getDocumentElement().normalize();

    // Get Period nodes
    NodeList periodList = document.getElementsByTagName("Period");
    for (int i = 0; i < periodList.getLength(); i++) {
      // First two nodes are metadata, the rest are price points
      Element periodElement = (Element) periodList.item(i);

      // Start date of the period
      LocalDateTime startDate = LocalDateTime.parse(
          periodElement.getElementsByTagName("start").item(0).getTextContent(),
          formatter);

      // Resolution of the period in minutes
      int interval = Integer.parseInt(
          periodElement.getElementsByTagName("resolution").item(0)
              .getTextContent().replaceAll("\\D", ""));

      // Get price points
      NodeList points = periodElement.getElementsByTagName("Point");
      for (int j = 0; j < points.getLength(); j++) {
        Element pointElement = (Element) points.item(j);
        double dataPoint = Double.parseDouble(pointElement.getElementsByTagName(dataString)
            .item(0).getTextContent());

        // Calculate the date of the price point based on the start date and interval
        LocalDateTime date = startDate.plusMinutes((long) interval * (j - 1));

        ApiData data = new ApiData();
        switch (type) {
          case "price" -> data.price = dataPoint;
          case "usage" -> data.usage = dataPoint;
          default -> throw new IllegalArgumentException("Invalid type: " + type);
        }
        data.date = date;
        data.interval = interval;

        String dateKey = date.toLocalDate().toString();

        // Add temperature data if available
        if (temperatureData != null && temperatureData.containsKey(dateKey)) {
          data.temperatureMean = temperatureData.get(dateKey);
        }

        dataList.add(data);
      }
    }
    return dataList;

  }
}