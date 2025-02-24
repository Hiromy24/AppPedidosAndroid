package com.example.apppedidosandroid;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressDAO {

    private static final String URL = "jdbc:mysql://your-aws-endpoint:3306/your-database";
    private static final String USER = "your-username";
    private static final String PASSWORD = "your-password";

    public void saveAddress(Address address) {
        String query = "INSERT INTO addresses (email, name, phone, street, streetNumber, portal, postalCode, cityProvince) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, address.getEmail());
            statement.setString(2, address.getName());
            statement.setString(3, address.getPhone());
            statement.setString(4, address.getStreet());
            statement.setString(5, address.getStreetNumber());
            statement.setString(6, address.getPortal());
            statement.setString(7, address.getPostalCode());
            statement.setString(8, address.getCityProvince());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Address> getAllAddresses(String email) {
        List<Address> addresses = new ArrayList<>();
        String query = "SELECT * FROM addresses WHERE email = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Address address = new Address(
                        resultSet.getString("email"),
                        resultSet.getString("name"),
                        resultSet.getString("phone"),
                        resultSet.getString("street"),
                        resultSet.getString("streetNumber"),
                        resultSet.getString("portal"),
                        resultSet.getString("postalCode"),
                        resultSet.getString("cityProvince")
                );
                addresses.add(address);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    public void updateAddressName(String email, String newName) {
        String query = "UPDATE addresses SET name = ? WHERE email = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, newName);
            statement.setString(2, email);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateAddressPhone(String email, String newPhone) {
        String query = "UPDATE addresses SET phone = ? WHERE email = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, newPhone);
            statement.setString(2, email);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateAddressDetails(String email, String newStreet, String newStreetNumber, String newPortal, String newPostalCode, String newCityProvince) {
        String query = "UPDATE addresses SET street = ?, streetNumber = ?, portal = ?, postalCode = ?, cityProvince = ? WHERE email = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, newStreet);
            statement.setString(2, newStreetNumber);
            statement.setString(3, newPortal);
            statement.setString(4, newPostalCode);
            statement.setString(5, newCityProvince);
            statement.setString(6, email);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}