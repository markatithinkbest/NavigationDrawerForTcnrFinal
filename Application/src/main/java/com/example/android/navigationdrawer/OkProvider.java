package com.example.android.navigationdrawer;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.widget.Toast;

import java.util.HashMap;

public class OkProvider extends ContentProvider {

    // ### NEED TO CHANGE TO YOUR DOMAIN
    // static final String PROVIDER_NAME =
    // "com.ithinkbest.tcnr18.finaltwo.MembersProvider";
    static final String PROVIDER_NAME = "com.example.android.navigationdrawer.OkProvider";

    private static final String SUB1 = "sub1";
    private static final String URL = "content://" + PROVIDER_NAME + "/" + SUB1;
    static final Uri CONTENT_URI = Uri.parse(URL);

    // `id` int(11) NOT NULL auto_increment,
    // `username` varchar(20) NOT NULL,
    // `password` varchar(32) NOT NULL,
    // `email` varchar(30) NOT NULL,
    // `authcode` varchar(8) default NULL,
    // `state` char(1) NOT NULL default '0',
    // `gup` varchar(10) NOT NULL,
    // `address1` double default NULL,
    // `address2` double default NULL,

    static final int uriCode = 1;

    private static HashMap<String, String> values;

    // Used to match uris with Content Providers
    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, SUB1, uriCode);
    }

    private SQLiteDatabase sqlDB;
    static final String CAT00 = "旅館業";
    static final String CAT01 = "美容美髮業";
    static final String CAT00_JSON="http://data.taipei.gov.tw/opendata/apply/json/QTdBNEQ5NkQtQkM3MS00QUI2LUJENTctODI0QTM5MkIwMUZE";
    static final String CAT01_JSON="http://data.taipei.gov.tw/opendata/apply/json/NzQzQjFGQjUtNzUxMi00RkUxLUIwQ0UtOUQxNjQ4MkExMDBD";
    public final static String[] CATXX= {CAT00,CAT01};
    public final static String[] CATXX_JSON= {CAT00_JSON,CAT01_JSON};

    DatabaseHelper dbHelper=null;



//    * 標題 name
//    * Ok認證類別 certification_category
//    * 連絡電話 tel
//    * 顯示用地址 display_addr
//    * 系統辨識用地址 poi_addr

    static final String COLUMN_ID = "_id"; // local ID
    static final String COLUMN_NAME = "name";
    static final String COLUMN_CERTIFICATION_CATEGORY = "certification_category";
    static final String COLUMN_TEL = "tel";
    static final String COLUMN_DISPLAY_ADDR = "display_addr";
    static final String COLUMN_POI_ADDR = "poi_addr";

    //
    static final String COLUMN_ADDR_DIST = "addr_dist";


    static private final String DATABASE_NAME = "taipei.db"; // YOUR DESIRED DATABASE
    static private final String TABLE_NAME = "ok"; // YOUR DESIRED TABLE
    static private final int DATABASE_VERSION = 4; // ### need to increase when change

    static private final String COL0 = COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT";
    static private final String COL1 = COLUMN_NAME + " TEXT NOT NULL ";
    static private final String COL2 = COLUMN_CERTIFICATION_CATEGORY + " TEXT NOT NULL ";
    static private final String COL3 = COLUMN_TEL + " TEXT NOT NULL ";
    static private final String COL4 = COLUMN_DISPLAY_ADDR + " TEXT NOT NULL ";
    static private final String COL5 = COLUMN_POI_ADDR + " TEXT NOT NULL ";

    //
    static private final String COL6 = COLUMN_ADDR_DIST + " TEXT NOT NULL ";

    // table structure
    static private final String CREATE_DB_TABLE = " CREATE TABLE " + TABLE_NAME + " ("
            + COL0 + ","
            + COL1 + ","
            + COL2 + ","
            + COL3 + ","
            + COL4 + ","
            + COL5 + ","
            + COL6 + " "
            + ");";

    @Override
    public boolean onCreate() {
         dbHelper = new DatabaseHelper(getContext());
        sqlDB = dbHelper.getWritableDatabase();
        if (sqlDB != null) {
            return true;
        }
        return false;
    }


    public Cursor rawQuery(String sql){
        return dbHelper.getReadableDatabase().rawQuery(sql, null);

    }

    // Returns a cursor that provides read and write access to the results of
    // the query
    // Uri : Links to the table in the provider (The From part of a query)
    // projection : an array of columns to retrieve with each row
    // selection : The where part of the query selection
    // selectionArgs : The argument part of the where (where id = 1)
    // sortOrder : The order by part of the query
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Used to create a SQL query
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Set table to query
        queryBuilder.setTables(TABLE_NAME);

        // Used to match uris with Content Providers
        switch (uriMatcher.match(uri)) {
            case uriCode:

                // A projection map maps from passed column names to database column
                // names
                queryBuilder.setProjectionMap(values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Cursor provides read and write access to the database
        Cursor cursor = queryBuilder.query(sqlDB, projection, selection,
                selectionArgs, null, null, sortOrder);

        // Register to watch for URI changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    // Handles requests for the MIME type (Type of Data) of the data at the URI
    @Override
    public String getType(Uri uri) {

        // Used to match uris with Content Providers
        switch (uriMatcher.match(uri)) {

            // vnd.android.cursor.dir/cpcontacts states that we expect multiple
            // pieces of data
            case uriCode:
                return "vnd.android.cursor.dir/" + SUB1;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    // Used to insert a new row into the provider
    // Receives the URI (Uniform Resource Identifier) for the Content Provider
    // and a set of values
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        // Gets the row id after inserting a map with the keys representing the
        // the column
        // names and their values. The second att +
        // " (id INTEGER PRIMARY KEY AUTOINCREMENT, "ribute is used when you try
        // to insert
        // an empty row
        long rowID = sqlDB.insert(TABLE_NAME, null, values);

        // Verify a row has been added
        if (rowID > 0) {

            // Append the given id to the path and return a Builder used to
            // manipulate URI
            // references
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);

            // getContentResolver provides access to the content model
            // notifyChange notifies all observers that a row was updated
            getContext().getContentResolver().notifyChange(_uri, null);

            // Return the Builder used to manipulate the URI
            return _uri;
        }
        Toast.makeText(getContext(), "Row Insert Failed", Toast.LENGTH_LONG)
                .show();
        return null;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
//        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
//        final int match = sUriMatcher.match(uri);
        sqlDB.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues value : values) {
                //  normalizeDate(value);
                long _id = sqlDB.insert(TABLE_NAME, null, value);
                if (_id != -1) {
                    returnCount++;
                }
            }
            sqlDB.setTransactionSuccessful();
        } finally {
            sqlDB.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;

    }

    // Deletes a row or a selection of rows
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted = 0;

        // Used to match uris with Content Providers
        switch (uriMatcher.match(uri)) {
            case uriCode:
                rowsDeleted = sqlDB.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // getContentResolver provides access to the content model
        // notifyChange notifies all observers that a row was updated
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    // Used to update a row or a selection of rows
    // Returns to number of rows updated
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int rowsUpdated = 0;

        // Used to match uris with Content Providers
        switch (uriMatcher.match(uri)) {
            case uriCode:

                // Update the row or rows of data
                rowsUpdated = sqlDB.update(TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // getContentResolver provides access to the content model
        // notifyChange notifies all observers that a row was updated
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    // Creates and manages our database
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqlDB) {
            sqlDB.execSQL(CREATE_DB_TABLE);
        }

        // Recreates the table when the database needs to be upgraded
        @Override
        public void onUpgrade(SQLiteDatabase sqlDB, int oldVersion,
                              int newVersion) {
            sqlDB.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(sqlDB);
        }
    }

}
