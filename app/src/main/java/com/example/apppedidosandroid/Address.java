package com.example.apppedidosandroid;

public class Address {
    private String name;
    private String phone;
    private String addressLine1;
    private String addressLine2;

    public Address(String name, String phone, String addressLine1, String addressLine2) {
        this.name = name;
        this.phone = phone;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
    }

    public String getName() { 
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }
}
