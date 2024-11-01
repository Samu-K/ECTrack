package fi.tuni.ec.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


/**
 * Service for fetching data from the API.
 */
public class ApiService {

  private static final String API_URL = "https://web-api.tp.entsoe.eu/api";
  private static String apiKey;

  // Map for storing area (country) codes
  public static final Map<String, String> COUNTRY_CODES = Map.of(
      "Finland", "10YFI-1--------U",
      "Germany", "10YDE-1--------W",
      "France", "10YFR-RTE------C",
      "Sweden", "10YSE-1--------K"
  );

  // Static block to load the API key from config.properties
  static {
    try (InputStream input = ApiService.class.getClassLoader()
        .getResourceAsStream("config.properties")) {
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
   * Fetch the electricity pricing data.
   */
  public List<Double> fetchPricing(String country, String periodStart, String periodEnd)
      throws Exception {
    String areaDomain = COUNTRY_CODES.get(country);
    String query = String.format(
        "?securityToken=%s&documentType=A44"
        + "&processType=A16&in_Domain=%s&out_Domain=%s&periodStart=%s&periodEnd=%s",
        getApiKey(), areaDomain, areaDomain, periodStart, periodEnd);
    URI uri = new URI(API_URL + query);
    URL url = uri.toURL();
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");

    if (connection.getResponseCode() == 200) {
      InputStream responseStream = connection.getInputStream();
      return parsePricingResponse(responseStream);
    } else {
      throw new IOException("Failed to fetch pricing data: HTTP " + connection.getResponseCode());
    }
  }

  /**
   * Fetch electricity usage data.
   */
  public List<Double> fetchUsage(String country, String periodStart, String periodEnd)
      throws Exception {
    String areaDomain = COUNTRY_CODES.get(country);
    String query = String.format(
        "?securityToken=%s&documentType=A65"
        + "&processType=A16&outBiddingZone_Domain=%s&periodStart=%s&periodEnd=%s",
        getApiKey(), areaDomain, periodStart, periodEnd);

    URI uri = new URI(API_URL + query);
    URL url = uri.toURL();
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");

    if (connection.getResponseCode() == 200) {
      InputStream responseStream = connection.getInputStream();
      return parseUsageResponse(responseStream);
    } else {
      throw new IOException("Failed to fetch usage data: HTTP " + connection.getResponseCode());
    }
  }

  /**
   * Parse pricing XML response into a list of doubles.
   */
  private List<Double> parsePricingResponse(InputStream responseStream) throws Exception {
    Document document = DocumentBuilderFactory.newInstance()
        .newDocumentBuilder().parse(responseStream);
    NodeList priceNodes = document.getElementsByTagName("price.amount");
    List<Double> prices = new ArrayList<>();
    for (int i = 0; i < priceNodes.getLength(); i++) {
      prices.add(Double.valueOf(priceNodes.item(i).getTextContent()));
    }
    return prices;
  }

  /**
   * Parse usage XML response into a list of doubles.
   */
  private List<Double> parseUsageResponse(InputStream responseStream) throws Exception {
    Document document = DocumentBuilderFactory.newInstance()
        .newDocumentBuilder().parse(responseStream);
    NodeList priceNodes = document.getElementsByTagName("quantity");
    List<Double> prices = new ArrayList<>();
    for (int i = 0; i < priceNodes.getLength(); i++) {
      prices.add(Double.valueOf(priceNodes.item(i).getTextContent()));
    }
    return prices;
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
