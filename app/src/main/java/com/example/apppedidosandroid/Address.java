package com.example.apppedidosandroid;

public class Address {
    private String fullName;
    private String phone;
    private String street;
    private String streetNumber;
    private String portal;
    private String postalCode;
    private String city;

    public Address() {
    }

    public Address(String fullName, String phone, String street, String streetNumber, String portal, String postalCode, String city) {
        this.fullName = fullName;
        this.phone = phone;
        this.street = street;
        this.streetNumber = streetNumber;
        this.portal = portal;
        this.postalCode = postalCode;
        this.city = city;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getPortal() {
        return portal;
    }

    public void setPortal(String portal) {
        this.portal = portal;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
