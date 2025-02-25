package com.example.apppedidosandroid;

public class Address {
    private String email;
    private String name;
    private String phone;
    private String street;
    private String streetNumber;
    private String portal;
    private String postalCode;
    private String city;

    public Address(String email, String name, String phone, String street, String streetNumber, String portal, String postalCode, String city) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.street = street;
        this.streetNumber = streetNumber;
        this.portal = portal;
        this.postalCode = postalCode;
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getStreet() {
        return street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public String getPortal() {
        return portal;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }


}