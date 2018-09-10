package com.mamahome360.mamahomele.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "LocationManagerLE.db";

    // User table name
    private static final String TABLE_USER_LOCATION = "Locations";

    // User Table Columns names
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_LATLNG = "lat_lng";
    private static final String COLUMN_SNAP_LATLNG = "snap_latlng";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_KM = "km";
    private static  final String COLUMN_TO_CHECK_FL ="to_check_fl";
    private static final  String COLUMN_TIME = "time";
    //private static final String COLUMN_USER_PASSWORD = "user_password";

    // create table sql query
    private String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_LOCATION + "(" +COLUMN_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_ID + " INT," + COLUMN_LATLNG + " TEXT,"
          + COLUMN_SNAP_LATLNG + " TEXT,"
          + COLUMN_DATE + " DATE,"+ COLUMN_KM +" TEXT,"+ COLUMN_TO_CHECK_FL +" BOOLEAN," + COLUMN_TIME + " TEXT)";

    // drop table sql query
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER_LOCATION;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE);

        // Create tables again
        onCreate(db);
    }

    public void addUserLocation(String UserID, String LatLng, String snap_LatLng, String CurrentDate,String time, String KM) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, UserID);
        values.put(COLUMN_LATLNG, LatLng);
        values.put(COLUMN_SNAP_LATLNG,snap_LatLng);
        values.put(COLUMN_DATE, CurrentDate);
        values.put(COLUMN_KM, KM);
        values.put(COLUMN_TO_CHECK_FL,Boolean.TRUE);
        values.put(COLUMN_TIME,time);
        // Inserting Row
        db.insert(TABLE_USER_LOCATION, null, values);
        db.close();
    }

    public void updateUserLocation(String LatLng,String snap_LatLng, String CurrentDate,String time, String KM) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LATLNG, LatLng);
        values.put(COLUMN_SNAP_LATLNG,snap_LatLng);
        values.put(COLUMN_TIME,time);
        values.put(COLUMN_KM,KM);
        // updating row
//        db.update(TABLE_USER_LOCATION, values, COLUMN_DATE + "= "+CurrentDate,
//                null);

        String UpdateQuery = "UPDATE "+ TABLE_USER_LOCATION +" SET "+ COLUMN_LATLNG +
                " = '"+ LatLng +"', "+ COLUMN_SNAP_LATLNG +
                " = '"+ snap_LatLng + "', "+ COLUMN_TIME +" = '"+ time +"', "+ COLUMN_KM +" = '"+ KM +"' WHERE "+ COLUMN_DATE +"='"+CurrentDate+"'";
        db.execSQL(UpdateQuery);
        db.close();
    }
    public void updateUserFl(boolean check,String CurrentDate ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TO_CHECK_FL, check);
        // updating row
//        db.update(TABLE_USER_LOCATION, values, COLUMN_DATE + "= "+CurrentDate,
//                null);

        String UpdateQuery = "UPDATE "+ TABLE_USER_LOCATION +" SET "+ COLUMN_TO_CHECK_FL +
                " = '"+ check  +"' WHERE "+ COLUMN_DATE +"='"+CurrentDate+"'";
        db.execSQL(UpdateQuery);
        db.close();
    }

    public void deleteOldRecord(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM Locations WHERE date <= date('now','-1 day')";
        db.execSQL(sql);
    }

    public boolean checkDateExist(String CurrentDate) {

        /*SQLiteDatabase sqldb = this.getReadableDatabase();
        Cursor cursor = null;
        String query = "Select "+ COLUMN_LATLNG +" from " + TABLE_USER_LOCATION + " where " + COLUMN_DATE + " = " + CurrentDate;
        cursor = sqldb.rawQuery(query, null);
        if(cursor.getCount() <= 0){

            return false;
        }
        else
        {

            cursor.close();
            return true;

        }*/

        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_DATE + " = ?";

        // selection argument
        String[] selectionArgs = {CurrentDate};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';*/

        Cursor cursor = db.query(TABLE_USER_LOCATION, //Table to query
                null,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();


        if (cursorCount > 0) {
            cursor.moveToFirst();
            cursor.close();
            db.close();
            return true;
        }
        else{
            cursor.close();
            db.close();
            return false;
        }



    }


    public String getColumnLatlng(String CurrentDate){

        // array of columns to fetch
        String LatLng = "";
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_DATE + " = ?";

        // selection argument
        String[] selectionArgs = {CurrentDate};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';*/

        Cursor cursor = db.query(TABLE_USER_LOCATION, //Table to query
                null,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();


        if (cursorCount > 0) {
            cursor.moveToFirst();
            LatLng = cursor.getString(cursor.getColumnIndex(COLUMN_LATLNG));
            cursor.close();
            db.close();
            return LatLng;
        }
        else{
            cursor.close();
            db.close();
            return LatLng;
        }

    }

    public String getColumnTOCheck(String CurrentDate){

        // array of columns to fetch
        String LatLng = "1";
      //  boolean fl_check = true;

        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_DATE + " = ?";

        // selection argument
        String[] selectionArgs = {CurrentDate};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';*/

        Cursor cursor = db.query(TABLE_USER_LOCATION, //Table to query
                null,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();


        if (cursorCount > 0) {
            cursor.moveToFirst();
            LatLng = cursor.getString(cursor.getColumnIndex(COLUMN_TO_CHECK_FL));
            Log.e("Checking1",LatLng);
            cursor.close();
            db.close();
            return LatLng;
        }
        else{
            cursor.close();
            db.close();
            return LatLng;
        }

    }

    public String getColumnSnapLatlng(String CurrentDate){

        // array of columns to fetch
        String LatLng = "";
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_DATE + " = ?";

        // selection argument
        String[] selectionArgs = {CurrentDate};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';*/

        Cursor cursor = db.query(TABLE_USER_LOCATION, //Table to query
                null,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();


        if (cursorCount > 0) {
            cursor.moveToFirst();
            LatLng = cursor.getString(cursor.getColumnIndex(COLUMN_SNAP_LATLNG));
            cursor.close();
            db.close();
            return LatLng;
        }
        else{
            cursor.close();
            db.close();
            return LatLng;
        }

    }

    public String getColumnTime(String CurrentDate){

        // array of columns to fetch
        String LatLng = "";

        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_DATE + " = ?";

        // selection argument
        String[] selectionArgs = {CurrentDate};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';*/

        Cursor cursor = db.query(TABLE_USER_LOCATION, //Table to query
                null,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();


        if (cursorCount > 0) {
            cursor.moveToFirst();
            LatLng = cursor.getString(cursor.getColumnIndex(COLUMN_TIME));
            cursor.close();
            db.close();
            return LatLng;
        }
        else{
            cursor.close();
            db.close();
            return LatLng;
        }

    }

    public String getColumnKm(String CurrentDate){

        // array of columns to fetch
        String KM = "";
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_DATE + " = ?";

        // selection argument
        String[] selectionArgs = {CurrentDate};

        Cursor cursor = db.query(TABLE_USER_LOCATION, //Table to query
                null,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();


        if (cursorCount > 0) {
            cursor.moveToFirst();
            KM = cursor.getString(cursor.getColumnIndex(COLUMN_KM));
            cursor.close();
            db.close();
            return KM;
        }
        else{
            cursor.close();
            db.close();
            return KM;
        }

    }
}
