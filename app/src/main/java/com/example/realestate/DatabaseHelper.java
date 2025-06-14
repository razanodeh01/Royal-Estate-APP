/**
 * Description:
 * The `DatabaseHelper` class is responsible for managing all database operations in the Real Estate agency application.
 * It extends `SQLiteOpenHelper` and handles the creation, upgrade, and CRUD operations for multiple tables
 * including users, properties, favorites, and reservations.
 */

package com.example.realestate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "RealEstateDB";
    private static final int DATABASE_VERSION = 4;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_COUNTRY = "country";
    private static final String COLUMN_CITY = "city";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_PROFILE_PICTURE = "profile_picture";
    private static final String COLUMN_ROLE = "role"; // Added role column


    private static final String TABLE_PROPERTIES = "properties";
    private static final String COLUMN_PROPERTY_ID = "property_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_AREA = "area";
    private static final String COLUMN_BEDROOMS = "bedrooms";
    private static final String COLUMN_BATHROOMS = "bathrooms";
    private static final String COLUMN_IMAGE_URL = "image_url";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IS_SPECIAL_OFFER = "is_special_offer"; // Added for special offers


    private static final String TABLE_FAVORITES = "favorites";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_FAVORITE_PROPERTY_ID = "property_id";


    private static final String TABLE_RESERVATIONS = "reservations";
    private static final String COLUMN_RESERVATION_ID = "reservation_id";
    private static final String COLUMN_RESERVATION_DATE = "reservation_date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_EMAIL + " TEXT UNIQUE,"
                + COLUMN_FIRST_NAME + " TEXT,"
                + COLUMN_LAST_NAME + " TEXT,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_GENDER + " TEXT,"
                + COLUMN_COUNTRY + " TEXT,"
                + COLUMN_CITY + " TEXT,"
                + COLUMN_PHONE + " TEXT,"
                + COLUMN_PROFILE_PICTURE + " TEXT,"
                + COLUMN_ROLE + " TEXT DEFAULT 'user'"
                + ")";
        db.execSQL(createUsersTable);

        String createPropertiesTable = "CREATE TABLE " + TABLE_PROPERTIES + "("
                + COLUMN_PROPERTY_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_TYPE + " TEXT,"
                + COLUMN_PRICE + " REAL,"
                + COLUMN_LOCATION + " TEXT,"
                + COLUMN_AREA + " TEXT,"
                + COLUMN_BEDROOMS + " INTEGER,"
                + COLUMN_BATHROOMS + " INTEGER,"
                + COLUMN_IMAGE_URL + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_IS_SPECIAL_OFFER + " INTEGER DEFAULT 0"
                + ")";
        db.execSQL(createPropertiesTable);

        String createFavoritesTable = "CREATE TABLE " + TABLE_FAVORITES + "("
                + COLUMN_USER_EMAIL + " TEXT,"
                + COLUMN_FAVORITE_PROPERTY_ID + " INTEGER,"
                + "PRIMARY KEY (" + COLUMN_USER_EMAIL + "," + COLUMN_FAVORITE_PROPERTY_ID + "),"
                + "FOREIGN KEY (" + COLUMN_USER_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_EMAIL + "),"
                + "FOREIGN KEY (" + COLUMN_FAVORITE_PROPERTY_ID + ") REFERENCES " + TABLE_PROPERTIES + "(" + COLUMN_PROPERTY_ID + ")"
                + ")";
        db.execSQL(createFavoritesTable);

        String createReservationsTable = "CREATE TABLE " + TABLE_RESERVATIONS + "("
                + COLUMN_RESERVATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_EMAIL + " TEXT,"
                + COLUMN_FAVORITE_PROPERTY_ID + " INTEGER,"
                + COLUMN_RESERVATION_DATE + " TEXT,"
                + "FOREIGN KEY (" + COLUMN_USER_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_EMAIL + "),"
                + "FOREIGN KEY (" + COLUMN_FAVORITE_PROPERTY_ID + ") REFERENCES " + TABLE_PROPERTIES + "(" + COLUMN_PROPERTY_ID + ")"
                + ")";
        db.execSQL(createReservationsTable);

        // Insert static admin account
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_EMAIL, "admin@admin.com");
        adminValues.put(COLUMN_FIRST_NAME, "Admin");
        adminValues.put(COLUMN_LAST_NAME, "User");
        adminValues.put(COLUMN_PASSWORD, "Admin123!");
        adminValues.put(COLUMN_GENDER, "Other");
        adminValues.put(COLUMN_COUNTRY, "Palestine");
        adminValues.put(COLUMN_CITY, "Ramallah");
        adminValues.put(COLUMN_PHONE, "+970599000000");
        adminValues.put(COLUMN_ROLE, "admin");
        db.insert(TABLE_USERS, null, adminValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_PROFILE_PICTURE + " TEXT");
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_ROLE + " TEXT DEFAULT 'user'");
            db.execSQL("ALTER TABLE " + TABLE_PROPERTIES + " ADD COLUMN " + COLUMN_IS_SPECIAL_OFFER + " INTEGER DEFAULT 0");
            // Insert static admin account during upgrade
            ContentValues adminValues = new ContentValues();
            adminValues.put(COLUMN_EMAIL, "admin@admin.com");
            adminValues.put(COLUMN_FIRST_NAME, "Admin");
            adminValues.put(COLUMN_LAST_NAME, "User");
            adminValues.put(COLUMN_PASSWORD, "Admin123!");
            adminValues.put(COLUMN_GENDER, "Other");
            adminValues.put(COLUMN_COUNTRY, "Palestine");
            adminValues.put(COLUMN_CITY, "Ramallah");
            adminValues.put(COLUMN_PHONE, "+970599000000");
            adminValues.put(COLUMN_ROLE, "admin");
            db.insert(TABLE_USERS, null, adminValues);
        } else {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPERTIES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
    }

    public boolean insertUser(String email, String firstName, String lastName, String password, String gender, String country, String city, String phone, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_COUNTRY, country);
        values.put(COLUMN_CITY, city);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_PROFILE_PICTURE, (String) null);
        values.put(COLUMN_ROLE, role);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkUser(String email, String password, String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=? AND " + COLUMN_ROLE + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password, role});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=?";
        return db.rawQuery(query, new String[]{email});
    }

    public boolean updateFirstName(String email, String firstName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, firstName);
        int result = db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        return result > 0;
    }

    public boolean updateLastName(String email, String lastName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LAST_NAME, lastName);
        int result = db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        return result > 0;
    }

    public boolean updatePhone(String email, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE, phone);
        int result = db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        return result > 0;
    }

    public boolean updatePassword(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, password);
        int result = db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        return result > 0;
    }

    public boolean updateProfilePicture(String email, String profilePicture) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROFILE_PICTURE, profilePicture);
        int result = db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        return result > 0;
    }

    public boolean verifyPassword(String email, String password) {
        return checkUser(email, password, "user") || checkUser(email, password, "admin");
    }

    public boolean insertProperty(int id, String title, String type, double price, String location, String area, int bedrooms, int bathrooms, String imageUrl, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROPERTY_ID, id);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_LOCATION, location);
        values.put(COLUMN_AREA, area);
        values.put(COLUMN_BEDROOMS, bedrooms);
        values.put(COLUMN_BATHROOMS, bathrooms);
        values.put(COLUMN_IMAGE_URL, imageUrl);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_IS_SPECIAL_OFFER, 0);
        long result = db.insert(TABLE_PROPERTIES, null, values);
        return result != -1;
    }

    public Cursor getPropertiesByType(String type) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PROPERTIES + " WHERE " + COLUMN_TYPE + "=?";
        return db.rawQuery(query, new String[]{type});
    }

    public boolean addToFavorites(String userEmail, int propertyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, userEmail);
        values.put(COLUMN_FAVORITE_PROPERTY_ID, propertyId);
        long result = db.insert(TABLE_FAVORITES, null, values);
        return result != -1;
    }

    public boolean addReservation(String userEmail, int propertyId, String dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RESERVATIONS + " WHERE " + COLUMN_USER_EMAIL + "=? AND " + COLUMN_FAVORITE_PROPERTY_ID + "=?",
                new String[]{userEmail, String.valueOf(propertyId)});
        boolean alreadyReserved = cursor.moveToFirst();
        cursor.close();
        if (alreadyReserved) {
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, userEmail);
        values.put(COLUMN_FAVORITE_PROPERTY_ID, propertyId);
        values.put(COLUMN_RESERVATION_DATE, dateTime);
        long result = db.insert(TABLE_RESERVATIONS, null, values);
        return result != -1;
    }

    public Cursor getReservationsByUser(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT p.*, r." + COLUMN_RESERVATION_DATE + " FROM " + TABLE_PROPERTIES + " p " +
                "INNER JOIN " + TABLE_RESERVATIONS + " r ON p." + COLUMN_PROPERTY_ID + " = r." + COLUMN_FAVORITE_PROPERTY_ID +
                " WHERE r." + COLUMN_USER_EMAIL + "=?";
        return db.rawQuery(query, new String[]{userEmail});
    }

    public Cursor getFavoritesByUser(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT p.* FROM " + TABLE_PROPERTIES + " p " +
                "INNER JOIN " + TABLE_FAVORITES + " f ON p." + COLUMN_PROPERTY_ID + " = f." + COLUMN_FAVORITE_PROPERTY_ID +
                " WHERE f." + COLUMN_USER_EMAIL + "=?";
        return db.rawQuery(query, new String[]{userEmail});
    }

    public boolean removeFromFavorites(String userEmail, int propertyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_FAVORITES,
                COLUMN_USER_EMAIL + "=? AND " + COLUMN_FAVORITE_PROPERTY_ID + "=?",
                new String[]{userEmail, String.valueOf(propertyId)});
        return result > 0;
    }

    public boolean isPropertyReserved(String userEmail, int propertyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_RESERVATIONS + " WHERE " + COLUMN_USER_EMAIL + "=? AND " + COLUMN_FAVORITE_PROPERTY_ID + "=?",
                new String[]{userEmail, String.valueOf(propertyId)});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Admin methods
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_ROLE + "='user'", null);
    }

    public boolean deleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES, COLUMN_USER_EMAIL + "=?", new String[]{email});
        db.delete(TABLE_RESERVATIONS, COLUMN_USER_EMAIL + "=?", new String[]{email});
        int result = db.delete(TABLE_USERS, COLUMN_EMAIL + "=?", new String[]{email});
        return result > 0;
    }

    public long getUserCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS + " WHERE " + COLUMN_ROLE + "='user'", null);
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        cursor.close();
        return count;
    }

    public long getReservationCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RESERVATIONS, null);
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        cursor.close();
        return count;
    }

    public Cursor getTopReservingCountries() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT u." + COLUMN_COUNTRY + ", COUNT(r." + COLUMN_RESERVATION_ID + ") as reservation_count " +
                "FROM " + TABLE_USERS + " u " +
                "LEFT JOIN " + TABLE_RESERVATIONS + " r ON u." + COLUMN_EMAIL + "=r." + COLUMN_USER_EMAIL +
                " WHERE u." + COLUMN_ROLE + "='user' " +
                "GROUP BY u." + COLUMN_COUNTRY + " ORDER BY reservation_count DESC LIMIT 5";
        return db.rawQuery(query, null);
    }

    public Cursor getGenderDistribution() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_GENDER + ", COUNT(*) as count FROM " + TABLE_USERS +
                " WHERE " + COLUMN_ROLE + "='user' GROUP BY " + COLUMN_GENDER;
        return db.rawQuery(query, null);
    }

    public Cursor getAllReservations() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT r." + COLUMN_RESERVATION_ID + ", r." + COLUMN_USER_EMAIL + ", r." + COLUMN_FAVORITE_PROPERTY_ID + ", r." + COLUMN_RESERVATION_DATE +
                ", u." + COLUMN_FIRST_NAME + ", u." + COLUMN_LAST_NAME + ", p." + COLUMN_TITLE +
                " FROM " + TABLE_RESERVATIONS + " r" +
                " INNER JOIN " + TABLE_USERS + " u ON r." + COLUMN_USER_EMAIL + "=u." + COLUMN_EMAIL +
                " INNER JOIN " + TABLE_PROPERTIES + " p ON r." + COLUMN_FAVORITE_PROPERTY_ID + "=p." + COLUMN_PROPERTY_ID;
        return db.rawQuery(query, null);
    }

    public boolean setSpecialOffer(int propertyId, boolean isSpecialOffer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_SPECIAL_OFFER, isSpecialOffer ? 1 : 0);
        int result = db.update(TABLE_PROPERTIES, values, COLUMN_PROPERTY_ID + "=?", new String[]{String.valueOf(propertyId)});
        return result > 0;
    }

    public Cursor getSpecialOfferProperties() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PROPERTIES + " WHERE " + COLUMN_IS_SPECIAL_OFFER + "=1", null);
    }

    public Cursor getAllProperties() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PROPERTIES, null);
    }
    public boolean isPropertyFavorited(String userEmail, int propertyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_FAVORITES + " WHERE " + COLUMN_USER_EMAIL + "=? AND " + COLUMN_FAVORITE_PROPERTY_ID + "=?",
                new String[]{userEmail, String.valueOf(propertyId)});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
}