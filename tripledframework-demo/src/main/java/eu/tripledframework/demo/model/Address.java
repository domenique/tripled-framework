package eu.tripledframework.demo.model;

public class Address {

  private String street;
  private String city;
  private String postalCode;
  private String country;

  public Address(String street, String city, String postalCode, String country) {
    this.street = street;
    this.city = city;
    this.postalCode = postalCode;
    this.country = country;
  }
}
