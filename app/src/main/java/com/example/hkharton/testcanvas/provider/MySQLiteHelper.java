package com.example.hkharton.testcanvas.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_MATERIALS = "materials";
    public static final String COLUMN_MATERIAL_ID = "_id";
    public static final String COLUMN_MATERIAL_NAME = "name";
    public static final String COLUMN_MATERIAL_TYPE = "type";
    public static final String COLUMN_MATERIAL_FILEPATH = "filepath";

    public static final String TABLE_CHARACTERISTIC = "characteristic";
    public static final String COLUMN_CHARACTERISTIC_ID = "_id";
    public static final String COLUMN_MATERIAL_FOREIGN_ID = "material_id";
    public static final String COLUMN_CHARACTERISTIC = "characteristic";

    private static final String DATABASE_NAME = "materials.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String MATERIAL_TABLE_CREATE =
            "CREATE TABLE "
            + TABLE_MATERIALS + "( "
            + COLUMN_MATERIAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_MATERIAL_NAME + " TEXT NOT NULL, "
            + COLUMN_MATERIAL_TYPE + " INTEGER NOT NULL, "
            + COLUMN_MATERIAL_FILEPATH + " TEXT NOT NULL);";

    private static final String CHARACTERISTIC_TABLE_CREATE =
            "CREATE TABLE "
                    + TABLE_CHARACTERISTIC + "( "
                    + COLUMN_CHARACTERISTIC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_MATERIAL_FOREIGN_ID + " INTEGER NOT NULL, "
                    + COLUMN_CHARACTERISTIC + " TEXT NOT NULL);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(MATERIAL_TABLE_CREATE);
        database.execSQL(CHARACTERISTIC_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATERIALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHARACTERISTIC);
        onCreate(db);
    }
}