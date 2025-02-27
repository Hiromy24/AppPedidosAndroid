package com.example.apppedidosandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DaoAddress {
    private DatabaseHelper dbHelper;

    public DaoAddress(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public boolean guardarAddress(Address address) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", address.getEmail());
        values.put("name", address.getName());
        values.put("phone", address.getPhone());
        values.put("street", address.getStreet());
        values.put("streetNumber", address.getStreetNumber());
        values.put("portal", address.getPortal());
        values.put("postalCode", address.getPostalCode());
        values.put("city", address.getCity());

        long result = db.insert("Address", null, values);
        db.close();
        return result != -1;
    }

    public List<Address> getAllAddresses(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Address> addresses = new ArrayList<>();
        Cursor cursor = db.query("Address", null, "email = ?", new String[]{email}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Address address = new Address(
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        cursor.getString(cursor.getColumnIndexOrThrow("street")),
                        cursor.getString(cursor.getColumnIndexOrThrow("streetNumber")),
                        cursor.getString(cursor.getColumnIndexOrThrow("portal")),
                        cursor.getString(cursor.getColumnIndexOrThrow("postalCode")),
                        cursor.getString(cursor.getColumnIndexOrThrow("city"))
                );
                addresses.add(address);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return addresses;
    }

    public void borrarAddress(Address address) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Address", "email = ? AND street = ?", new String[]{address.getEmail(), address.getStreet()});
        db.close();
    }

    public void modificarAddress(Address address, String newStreet, String newStreetNumber, String newPortal, String newPostalCode, String newCity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("street", newStreet);
        values.put("streetNumber", newStreetNumber);
        values.put("portal", newPortal);
        values.put("postalCode", newPostalCode);
        values.put("city", newCity);

        db.update("Address", values, "email = ? AND street = ?", new String[]{address.getEmail(), address.getStreet()});
        db.close();
    }
}