package fi.tuni.ec.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
    List<ApiData> priceData = parseResponse(priceStream, "price");
    List<ApiData> usageData = parseResponse(usageStream, "usage");
    priceStream.close();
    usageStream.close();

    // Combine the two lists
    int len = Math.min(priceData.size(), usageData.size());

    for (int i = 0; i < len; i++) {
      ApiData data = new ApiData();
      data.price = priceData.get(i).price;
      data.usage = usageData.get(i).usage;
      data.date = priceData.get(i).date;
      data.interval = priceData.get(i).interval;
      dataList.add(data);
    }

    return dataList;
  }

  /**
   * Parse response into a list of ApiData.
   */
  private List<ApiData> parseResponse(InputStream responseStream, String type) throws Exception {
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

        dataList.add(data);
      }
    }
    return dataList;

  }

  // /**
  //  * Fetch data for a specific day.
  //  */
  // public void fetchDataForDay(String country, LocalDate date) throws Exception {
  //   String periodStart = date.format(DateTimeFormatter.ofPattern("yyyyMMdd0000"));
  //   String periodEnd = date.format(DateTimeFormatter.ofPattern("yyyyMMdd2300"));
  //   List<Double> pricingData = fetchPricing(country, periodStart, periodEnd);
  //   List<Double> usageData = fetchUsage(country, periodStart, periodEnd);
  //   System.out.println("Pricing length: " + pricingData.size());
  //   System.out.println("Usage length: " + usageData.size());
  //   System.out.println("Pricing data: " + pricingData);
  //   System.out.println("Usage data: " + usageData);
  // }

  // /**
  //  * Fetch data for a specific week.
  //  */
  // public void fetchDataForWeek(String country, LocalDate startDate) throws Exception {
  //   LocalDate endOfWeek = startDate.plusDays(6); // Assuming week starts on Monday
  //   String periodStart = startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd0000"));
  //   String periodEnd = endOfWeek.format(DateTimeFormatter.ofPattern("yyyyMMdd2300"));
  //   List<Double> pricingData = fetchPricing(country, periodStart, periodEnd);
  //   List<Double> usageData = fetchUsage(country, periodStart, periodEnd);
  //   System.out.println("Weekly Pricing data: " + pricingData);
  //   System.out.println("Weekly Usage data: " + usageData);
  // }

  // /**
  //  * Fetch data for specific month.
  //  */
  // public void fetchDataForMonth(String country, YearMonth month) throws Exception {
  //   LocalDate firstDay = month.atDay(1);
  //   LocalDate lastDay = month.atEndOfMonth();
  //   String periodStart = firstDay.format(DateTimeFormatter.ofPattern("yyyyMMdd0000"));
  //   String periodEnd = lastDay.format(DateTimeFormatter.ofPattern("yyyyMMdd2300"));
  //   List<Double> pricingData = fetchPricing(country, periodStart, periodEnd);
  //   List<Double> usageData = fetchUsage(country, periodStart, periodEnd);
  //   System.out.println("Monthly Pricing data: " + pricingData);
  //   System.out.println("Monthly Usage data: " + usageData);
  // }
}
