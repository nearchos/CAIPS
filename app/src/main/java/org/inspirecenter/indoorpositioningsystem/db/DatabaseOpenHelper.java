package org.inspirecenter.indoorpositioningsystem.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Nearchos
 *         Created: 26-Apr-16
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    // if you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 11;

    // this is used to name the underlying file storing the actual data
    public static final String DATABASE_NAME = "indoor_positioning.db";

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_LOCATIONS);
        db.execSQL(SQL_CREATE_FLOORS);
        db.execSQL(SQL_CREATE_IMAGES);
        db.execSQL(SQL_CREATE_TRAININGS);
        db.execSQL(SQL_CREATE_CUSTOM_CONTEXT);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // in our case, we simply delete all data and recreate the DB
        db.execSQL(SQL_DELETE_LOCATIONS);
        db.execSQL(SQL_DELETE_FLOORS);
        db.execSQL(SQL_DELETE_IMAGES);
        db.execSQL(SQL_DELETE_TRAININGS);
        db.execSQL(SQL_DELETE_CUSTOM_CONTEXT);
        onCreate(db);
    }

    private static final String SQL_CREATE_LOCATIONS =
            "CREATE TABLE menu_locations (" +
                    "uuid TEXT PRIMARY KEY NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "createdBy TEXT NOT NULL, " +
                    "timestamp INTEGER" +
                    ")";

    private static final String SQL_CREATE_FLOORS =
            "CREATE TABLE floors (" +
                    "uuid TEXT PRIMARY KEY NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "seq INTEGER, " +
                    "locationUuid TEXT NOT NULL, " +
                    "imageUrl TEXT NOT NULL, " +
                    "topLeftLat FLOAT, " +
                    "topLeftLng FLOAT, " +
                    "bottomRightLat FLOAT, " +
                    "bottomRightLng FLOAT" +
                    ")";

    private static final String SQL_CREATE_IMAGES =
            "CREATE TABLE images (" +
                    "uuid TEXT PRIMARY KEY NOT NULL, " +
                    "label TEXT NOT NULL, " +
                    "data BLOB NOT NULL" +
                    ")";

    private static final String SQL_CREATE_TRAININGS =
            "CREATE TABLE trainings (" +
                    "uuid TEXT PRIMARY KEY NOT NULL, " +
                    "createdBy TEXT NOT NULL, " +
                    "locationUuid TEXT NOT NULL, " +
                    "floorUuid TEXT NOT NULL, " +
                    "timestamp INTEGER, " +
                    "radiomap TEXT NOT NULL, " +
                    "context TEXT NOT NULL, " +
                    "lat REAL, " +
                    "lng REAL" +
                    ")";

    private static final String SQL_CREATE_CUSTOM_CONTEXT =
            "CREATE TABLE customContext (" +
                    "uuid TEXT PRIMARY KEY NOT NULL, " +
                    "locationUuid TEXT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "value TEXT NOT NULL, " +
                    "checked INTEGER" +
                    ")";

    private static final String SQL_DELETE_LOCATIONS =
            "DROP TABLE IF EXISTS menu_locations";

    private static final String SQL_DELETE_FLOORS =
            "DROP TABLE IF EXISTS floors";

    private static final String SQL_DELETE_IMAGES =
            "DROP TABLE IF EXISTS images";

    private static final String SQL_DELETE_TRAININGS =
            "DROP TABLE IF EXISTS trainings";

    private static final String SQL_DELETE_CUSTOM_CONTEXT =
            "DROP TABLE IF EXISTS customContext";
}