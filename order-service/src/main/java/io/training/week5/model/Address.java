package io.training.week5.model;

public class Address {
  private String building;
  private String street;
  private String city;
  private String state;
  private String zip;
  private String country;

  public Address(String building, String street, String city, String state, String zip,
      String country) {
    this.building = building;
    this.street = street;
    this.city = city;
    this.state = state;
    this.zip = zip;
    this.country = country;
  }

  public Address() {}

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getBuilding() {
    return building;
  }

  public void setBuilding(String building) {
    this.building = building;
  }

  public String getCity() { return city; }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

}
