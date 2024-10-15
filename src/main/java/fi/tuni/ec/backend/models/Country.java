package fi.tuni.ec.backend.models;

public class Country {
  private final String id;
  private final String zone;
  private final String countryName;

  public Country(String id, String zone, String countryName) {
    this.id = id;
    this.zone = zone;
    this.countryName = countryName != null ? countryName : zone;
  }

  public String getId() {
    return id;
  }

  public String getZone() {
    return zone;
  }

  public String getCountryName() {
    return countryName;
  }
}
