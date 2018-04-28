package com.example.bhavik.smarter.SQLiteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager {

    // SQL code to create table
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "electricityUsage.db";
    private final Context context;
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBStructure.tableEntry.TABLE_NAME + " (" +
                    DBStructure.tableEntry._ID + " INTEGER PRIMARY KEY," +
                    DBStructure.tableEntry.COLUMN_ID + TEXT_TYPE + COMMA_SEP +
                    DBStructure.tableEntry.COLUMN_DATE + TEXT_TYPE + COMMA_SEP +
                    DBStructure.tableEntry.COLUMN_HOUR + TEXT_TYPE + COMMA_SEP +
                    DBStructure.tableEntry.COLUMN_FRIDGE + TEXT_TYPE + COMMA_SEP +
                    DBStructure.tableEntry.COLUMN_AIRCON + TEXT_TYPE + COMMA_SEP +
                    DBStructure.tableEntry.COLUMN_WASHINGMACHINE + TEXT_TYPE + COMMA_SEP +
                    DBStructure.tableEntry.COLUMN_TEMPERATURE + TEXT_TYPE +
                    " );";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBStructure.tableEntry.TABLE_NAME;

    // projection object to get all records
    private String[] columns = {
            DBStructure.tableEntry.COLUMN_ID,
            DBStructure.tableEntry.COLUMN_DATE,
            DBStructure.tableEntry.COLUMN_HOUR,
            DBStructure.tableEntry.COLUMN_FRIDGE,
            DBStructure.tableEntry.COLUMN_AIRCON,
            DBStructure.tableEntry.COLUMN_WASHINGMACHINE,
            DBStructure.tableEntry.COLUMN_TEMPERATURE };

    private MySQLiteOpenHelper myDBHelper;
    private SQLiteDatabase db;

    // DBManager constructor
    public DBManager(Context ctx) {
        this.context = ctx;
        myDBHelper = new MySQLiteOpenHelper(context);
    }

    // To open database
    public DBManager open() throws SQLException {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    // To close database
    public void close() {
        myDBHelper.close();
    }

    // To insert records in SQLite DB
    public long insertRecord(String id, String date, String hour, String fridge, String airCon, String washMachine, String temp) {
        ContentValues values = new ContentValues();
        values.put(DBStructure.tableEntry.COLUMN_ID, id);
        values.put(DBStructure.tableEntry.COLUMN_DATE, date);
        values.put(DBStructure.tableEntry.COLUMN_HOUR, hour);
        values.put(DBStructure.tableEntry.COLUMN_FRIDGE, fridge);
        values.put(DBStructure.tableEntry.COLUMN_AIRCON, airCon);
        values.put(DBStructure.tableEntry.COLUMN_WASHINGMACHINE, washMachine);
        values.put(DBStructure.tableEntry.COLUMN_TEMPERATURE, temp);
        return db.insert(DBStructure.tableEntry.TABLE_NAME, null, values);
    }

    // Retrieve all records
    public Cursor getAllRecords() {
        return db.query(DBStructure.tableEntry.TABLE_NAME, columns, null, null, null, null, null);
    }

    // Delete records
    public int deleteRecord(String rowId) {
        String[] selectionArgs = { String.valueOf(rowId) };
        String selection = DBStructure.tableEntry.COLUMN_ID + " LIKE ?";
        return db.delete(DBStructure.tableEntry.TABLE_NAME, selection,selectionArgs );
    }

    // delete all records
    public void deleteAllRecords(){
        db.execSQL("delete from "+ DBStructure.tableEntry.TABLE_NAME);
    }

    // SQLiteOpenHelper class
    private static class MySQLiteOpenHelper extends SQLiteOpenHelper {
        public MySQLiteOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }
}
