package com.example.bhavik.smarter.SQLiteDB;

import android.provider.BaseColumns;

public class DBStructure {

    // Class to store the schema of the table
    public static abstract class tableEntry implements BaseColumns {
        public static final String TABLE_NAME = "eUsage";
        public static final String COLUMN_ID = "usageId";
        public static final String COLUMN_DATE = "usageDate";
        public static final String COLUMN_HOUR = "usageHour";
        public static final String COLUMN_FRIDGE = "fridgeUse";
        public static final String COLUMN_AIRCON = "airConUse";
        public static final String COLUMN_WASHINGMACHINE = "washingMachine";
        public static final String COLUMN_TEMPERATURE = "temperature";
    }
}
