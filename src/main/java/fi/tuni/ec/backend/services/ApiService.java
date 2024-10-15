package fi.tuni.ec.backend.services;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import fi.tuni.ec.backend.models.Country;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ApiService {

  private static final String ELECTRICITYMAP_API_URL = "https://api.electricitymap.org";


  public List<String> getCountries() {
    var countries = fetchCountries();

    List<String> uniqueCountries = new ArrayList<>();

    for (var country : countries) {
      var countryName = country.getCountryName();

      if (!uniqueCountries.contains(countryName)) {
        uniqueCountries.add(countryName);
      }
    }

    Collections.sort(uniqueCountries);
    return uniqueCountries;
  }

  private List<Country> fetchCountries() {
    List<Country> countries = new ArrayList<>();

    try {
      var url = new URL(ELECTRICITYMAP_API_URL + "/v3/zones");
      var conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      var in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String inputLine;
      var content = new StringBuilder();

      while ((inputLine = in.readLine()) != null) {
        content.append(inputLine);
      }

      in.close();
      in.close();
      conn.disconnect();

      var jsonObject = Jsoner.deserialize(content.toString(), new JsonObject());

      for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
        var countryID = entry.getKey();
        var countryObject = (JsonObject) entry.getValue();
        var zoneName = (String) countryObject.get("zoneName");
        var countryName = countryObject.containsKey("countryName") ? (String) countryObject.get("countryName") : null;
        countries.add(new Country(countryID, zoneName, countryName));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return countries;
  }
}
