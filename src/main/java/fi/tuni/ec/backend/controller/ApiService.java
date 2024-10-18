package fi.tuni.ec.backend.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
/**
 * Service for fetching data from the API.
 * This class is responsible for fetching data from the API and parsing it into a
 * format that can be used by the application.
 */

public class ApiService {
  private OkHttpClient client;
  private Gson gson;

  public ApiService() {
    this.client = new OkHttpClient();
    this.gson = new Gson();
  }

  /**
  * Fetch electricity pricing data from the API.
  *
  * @param url The URL to fetch the data from
  * @return A list of maps containing the pricing data
  * @throws IOException If an error occurs while fetching the data
  */
  public List<Map<String, Object>> fetchElectricityPricing(String url) throws IOException {
    Request request = new Request.Builder().url(url).build();
    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Unexpected code: " + response);
      }
      String jsonData = response.body().string();

      // Parse the json data
      JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);

      // Extract the array of pricing data
      Type listType = new TypeToken<List<Map<String, Object>>>() {
        }.getType();

      return gson.fromJson(jsonObject.get("prices"), listType);

    }

  }

}
