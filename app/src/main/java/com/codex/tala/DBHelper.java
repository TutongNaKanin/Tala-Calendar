package com.codex.tala;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.time.LocalDate;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Tala.db";
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_USER_NAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    public static final String TABLE_EVENTS = "events";
    public static final String COLUMN_EVENT_ID = "event_id";
    public static final String COLUMN_EVENT_TITLE = "title";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_END_TIME = "end_time";

    public static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + "("
                    + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_EMAIL + " TEXT UNIQUE,"
                    + COLUMN_USER_NAME + " TEXT,"
                    + COLUMN_PASSWORD + " TEXT"
                    + ")";

    public static final String CREATE_TABLE_EVENTS =
            "CREATE TABLE " + TABLE_EVENTS + "("
                    + COLUMN_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_USER_ID + " INTEGER,"
                    + COLUMN_EVENT_TITLE + " TEXT,"
                    + COLUMN_START_DATE + " TEXT,"
                    + COLUMN_END_DATE + " TEXT,"
                    + COLUMN_START_TIME + " TEXT,"
                    + COLUMN_END_TIME + " TEXT,"
                    + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
                    + ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_EVENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    public Boolean insertUsersData(String email, String username, String pass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_EMAIL, email);
        cv.put(COLUMN_USER_NAME, username);
        cv.put(COLUMN_PASSWORD, pass);
        long result = db.insert(TABLE_USERS, null, cv);
        return result != -1;
    }

    public boolean insertEventData(int userId, String title, String startDate, String endDate, String startTime, String endTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USER_ID, userId);
        cv.put(COLUMN_EVENT_TITLE, title);
        cv.put(COLUMN_START_DATE, startDate);
        cv.put(COLUMN_END_DATE, endDate);
        cv.put(COLUMN_START_TIME, startTime);
        cv.put(COLUMN_END_TIME, endTime);
        long result = db.insert(TABLE_EVENTS, null, cv);
        return result != -1;
    }

    public int getUserId(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userId = -1;

        Cursor cursor = db.rawQuery("SELECT user_id FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?", new String[]{email, password});
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
        }
        cursor.close();
        
        return userId;
    }

    public Boolean checkemail(String email) { //checks if email exists in the database
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?", new String[]{email});
        boolean emailExists = cursor.getCount() > 0;
        cursor.close();
        return emailExists;
    }

    public Boolean checkemailpass(String email, String password) { //checks if email and password combination exists in the database
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?", new String[]{email, password});
        boolean emailExists = cursor.getCount() > 0;
        cursor.close();
        return emailExists;
    }

    public Cursor getEventDataForDate(int userId, LocalDate date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                COLUMN_EVENT_TITLE,
                COLUMN_START_DATE,
                COLUMN_END_DATE,
                COLUMN_START_TIME,
                COLUMN_END_TIME
        };
        String selection = COLUMN_USER_ID + " = ? AND " +
                "? BETWEEN " + COLUMN_START_DATE + " AND " + COLUMN_END_DATE;
        String formattedDate = date.toString();
        String[] selectionArgs = {String.valueOf(userId), formattedDate};
        return db.query(TABLE_EVENTS, projection, selection, selectionArgs, null, null, null);
    }

    public String getUsername(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String username = "";

        if (db == null || userId == -1) {
            return null;
        }

        String[] projection = {COLUMN_USER_NAME};
        String selection = COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS, projection, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int usernameIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_NAME);
                username = cursor.getString(usernameIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return username;
    }

    public String getEmail(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String email = "";

        if (db == null || userId == -1) {
            return null;
        }

        String[] projection = {COLUMN_EMAIL};
        String selection = COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS, projection, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int emailIndex = cursor.getColumnIndexOrThrow(COLUMN_EMAIL);
                email = cursor.getString(emailIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return email;
    }

    public boolean checkCalendarEventExists(int userId, LocalDate date) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean eventExists = false;

        if (db == null || date == null) {
            return false;
        }

        String dateString = date.toString();
        String[] projection = {COLUMN_EVENT_ID};
        String selection = COLUMN_USER_ID + " = ? AND (? BETWEEN " + COLUMN_START_DATE + " AND " + COLUMN_END_DATE + ")";
        String[] selectionArgs = {String.valueOf(userId), dateString};
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_EVENTS, projection, selection, selectionArgs, null, null, null);
            eventExists = cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return eventExists;
    }
}
