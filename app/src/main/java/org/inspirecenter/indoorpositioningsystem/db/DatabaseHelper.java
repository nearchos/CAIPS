package org.inspirecenter.indoorpositioningsystem.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.inspirecenter.indoorpositioningsystem.data.CustomContextElement;
import org.inspirecenter.indoorpositioningsystem.data.Floor;
import org.inspirecenter.indoorpositioningsystem.data.Image;
import org.inspirecenter.indoorpositioningsystem.data.Location;
import org.inspirecenter.indoorpositioningsystem.data.Training;

import java.util.UUID;

/**
 * @author Nearchos
 *         Created: 27-Apr-16
 */
public class DatabaseHelper {
    public static final String TAG = "ips";

    private DatabaseHelper() {
        // prohibits instantiation
    }

    public static boolean containsLocation(final SQLiteDatabase sqLiteDatabase, final String uuid) {
        final Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM menu_locations WHERE uuid=?", new String [] {uuid});
        int numOfRows = cursor.getCount();
        final boolean found = numOfRows > 0;
        cursor.close();
        sqLiteDatabase.close();

        return found;
    }

    public static Location getLocation(final SQLiteDatabase sqLiteDatabase, final String uuid) {
        final Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM menu_locations WHERE uuid=?", new String [] {uuid});
        int numOfRows = cursor.getCount();
        cursor.moveToFirst();
        int columnUUIDIndex = cursor.getColumnIndex("uuid");
        int columnNameIndex = cursor.getColumnIndex("name");
        int columnCreatedByIndex = cursor.getColumnIndex("createdBy");
        int columnTimestampIndex = cursor.getColumnIndex("timestamp");
        final Location location;
        if(numOfRows == 1) {
            location = new Location(
                    cursor.getString(columnUUIDIndex),
                    cursor.getString(columnNameIndex),
                    cursor.getString(columnCreatedByIndex),
                    cursor.getLong(columnTimestampIndex)
            );
        } else {
            location = null;
        }
        cursor.close();
        sqLiteDatabase.close();

        return location;
    }

    public static long add(final SQLiteDatabase sqLiteDatabase, final Location location) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put("uuid", location.getUuid());
        contentValues.put("createdBy", location.getCreatedBy());
        contentValues.put("name", location.getName());
        contentValues.put("timestamp", location.getTimestamp());
        long row = sqLiteDatabase.insert("menu_locations", null, contentValues);
        sqLiteDatabase.close();
        return row;
    }

    public static int edit(final SQLiteDatabase sqLiteDatabase, final String locationUuid, final String name) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        int rows = sqLiteDatabase.update("menu_locations", contentValues, "uuid=?", new String [] {locationUuid});
        sqLiteDatabase.close();
        return rows;
    }

    public static Floor [] getAllFloors(final SQLiteDatabase sqLiteDatabase) {
        final Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM floors", new String [] {});
        int numOfRows = cursor.getCount();
        cursor.moveToFirst();
        int columnUUIDIndex = cursor.getColumnIndex("uuid");
        int columnNameIndex = cursor.getColumnIndex("name");
        int columnSeqIndex = cursor.getColumnIndex("seq");
        int columnLocationUuidIndex = cursor.getColumnIndex("locationUuid");
        int columnImageUrlIndex = cursor.getColumnIndex("imageUrl");
        int columnTopLeftLatIndex = cursor.getColumnIndex("topLeftLat");
        int columnTopLeftLngIndex = cursor.getColumnIndex("topLeftLng");
        int columnBottomRightLatIndex = cursor.getColumnIndex("bottomRightLat");
        int columnBottomRightLngIndex = cursor.getColumnIndex("bottomRightLng");
        final Floor[] floors = new Floor[numOfRows];
        for(int i = 0; i < numOfRows; i++) {
            final Floor floor = new Floor(
                    cursor.getString(columnUUIDIndex),
                    cursor.getString(columnNameIndex),
                    cursor.getInt(columnSeqIndex),
                    cursor.getString(columnLocationUuidIndex),
                    cursor.getString(columnImageUrlIndex),
                    cursor.getFloat(columnTopLeftLatIndex),
                    cursor.getFloat(columnTopLeftLngIndex),
                    cursor.getFloat(columnBottomRightLatIndex),
                    cursor.getFloat(columnBottomRightLngIndex)
            );
            floors[i] = floor;
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();

        return floors;
    }

    public static Floor [] getFloors(final SQLiteDatabase sqLiteDatabase, final String locationUuid) {
        final Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM floors WHERE locationUuid=?", new String [] {locationUuid});
        int numOfRows = cursor.getCount();
        cursor.moveToFirst();
        int columnUUIDIndex = cursor.getColumnIndex("uuid");
        int columnNameIndex = cursor.getColumnIndex("name");
        int columnSeqIndex = cursor.getColumnIndex("seq");
        int columnLocationUuidIndex = cursor.getColumnIndex("locationUuid");
        int columnImageUrlIndex = cursor.getColumnIndex("imageUrl");
        int columnTopLeftLatIndex = cursor.getColumnIndex("topLeftLat");
        int columnTopLeftLngIndex = cursor.getColumnIndex("topLeftLng");
        int columnBottomRightLatIndex = cursor.getColumnIndex("bottomRightLat");
        int columnBottomRightLngIndex = cursor.getColumnIndex("bottomRightLng");
        final Floor[] floors = new Floor[numOfRows];
        for(int i = 0; i < numOfRows; i++) {
            final Floor floor = new Floor(
                    cursor.getString(columnUUIDIndex),
                    cursor.getString(columnNameIndex),
                    cursor.getInt(columnSeqIndex),
                    cursor.getString(columnLocationUuidIndex),
                    cursor.getString(columnImageUrlIndex),
                    cursor.getFloat(columnTopLeftLatIndex),
                    cursor.getFloat(columnTopLeftLngIndex),
                    cursor.getFloat(columnBottomRightLatIndex),
                    cursor.getFloat(columnBottomRightLngIndex)
            );
            floors[i] = floor;
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();

        return floors;
    }

    public static Floor getFloor(final SQLiteDatabase sqLiteDatabase, final String uuid) {
        final Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM floors WHERE uuid=?", new String [] {uuid});
        int numOfRows = cursor.getCount();
        cursor.moveToFirst();
        int columnUUIDIndex = cursor.getColumnIndex("uuid");
        int columnNameIndex = cursor.getColumnIndex("name");
        int columnSeqIndex = cursor.getColumnIndex("seq");
        int columnLocationUuidIndex = cursor.getColumnIndex("locationUuid");
        int columnImageUrlIndex = cursor.getColumnIndex("imageUrl");
        int columnTopLeftLatIndex = cursor.getColumnIndex("topLeftLat");
        int columnTopLeftLngIndex = cursor.getColumnIndex("topLeftLng");
        int columnBottomRightLatIndex = cursor.getColumnIndex("bottomRightLat");
        int columnBottomRightLngIndex = cursor.getColumnIndex("bottomRightLng");
        final Floor floor;
        if(numOfRows == 1) {
            floor = new Floor(
                    cursor.getString(columnUUIDIndex),
                    cursor.getString(columnNameIndex),
                    cursor.getInt(columnSeqIndex),
                    cursor.getString(columnLocationUuidIndex),
                    cursor.getString(columnImageUrlIndex),
                    cursor.getFloat(columnTopLeftLatIndex),
                    cursor.getFloat(columnTopLeftLngIndex),
                    cursor.getFloat(columnBottomRightLatIndex),
                    cursor.getFloat(columnBottomRightLngIndex)
            );
        } else {
            floor = null;
        }
        cursor.close();
        sqLiteDatabase.close();

        return floor;
    }

    public static long addFloor(final SQLiteDatabase sqLiteDatabase, final Floor floor) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put("uuid", floor.getUuid());
        contentValues.put("name", floor.getName());
        contentValues.put("seq", floor.getSeq());
        contentValues.put("locationUuid", floor.getLocationUUID());
        contentValues.put("imageUrl", floor.getImageUrl());
        contentValues.put("topLeftLat", floor.getTopLeftLat());
        contentValues.put("topLeftLng", floor.getTopLeftLng());
        contentValues.put("bottomRightLat", floor.getBottomRightLat());
        contentValues.put("bottomRightLng", floor.getBottomRightLng());
        final long row = sqLiteDatabase.insert("floors", null, contentValues);
        sqLiteDatabase.close();
        return row;
    }

    public static int edit(final SQLiteDatabase sqLiteDatabase, final String uuid,
                           final String name, final int seq, final String imageUrl,
                           final double topLeftLat, final double topLeftLng,
                           final double bottomRightLat, final double bottomRightLng) {

        final ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("seq", seq);
        contentValues.put("imageUrl", imageUrl);
        contentValues.put("topLeftLat", topLeftLat);
        contentValues.put("topLeftLng", topLeftLng);
        contentValues.put("bottomRightLat", bottomRightLat);
        contentValues.put("bottomRightLng", bottomRightLng);
        int numOfRowsAffected = sqLiteDatabase.update("floors", contentValues, "uuid=?", new String [] {uuid});
        sqLiteDatabase.close();
        return numOfRowsAffected;
    }

    public static long addImage(final SQLiteDatabase sqLiteDatabase, final Image image) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put("uuid", image.getUuid());
        contentValues.put("label", image.getLabel());
        contentValues.put("data", image.getData());
Log.d(TAG, "Storing image: " + image.getUuid() + ", " + image.getLabel() + ", data: " + image.getData().length);
        final long row = sqLiteDatabase.insert("images", null, contentValues);
        sqLiteDatabase.close();
        return row;
    }

    public static Image getImage(final SQLiteDatabase sqLiteDatabase, final String imageUuid) {
        final Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM images WHERE uuid=?", new String [] {imageUuid});
        int numOfRows = cursor.getCount();
        int columnUUIDIndex = cursor.getColumnIndex("uuid");
        int columnLabelIndex = cursor.getColumnIndex("label");
        int columnDataIndex = cursor.getColumnIndex("data");
        final Image image;
        cursor.moveToFirst();
        if(numOfRows == 1) {
            final String uuid = cursor.getString(columnUUIDIndex);
            final String label = cursor.getString(columnLabelIndex);
            final byte [] data = cursor.getBlob(columnDataIndex);
            image = new Image(uuid, label, data);
        } else {
            image = null;
        }
        cursor.close();
        sqLiteDatabase.close();

        return image;
    }

    public static Image [] getAllImages(final SQLiteDatabase sqLiteDatabase) {
        final Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM images", new String [] {});
        int numOfRows = cursor.getCount();
        int columnUUIDIndex = cursor.getColumnIndex("uuid");
        int columnLabelIndex = cursor.getColumnIndex("label");
        int columnDataIndex = cursor.getColumnIndex("data");
        final Image [] images = new Image[numOfRows];
        cursor.moveToFirst();
        for(int i = 0; i < numOfRows; i++) {
            final String uuid = cursor.getString(columnUUIDIndex);
            final String label = cursor.getString(columnLabelIndex);
            final byte [] data = cursor.getBlob(columnDataIndex);
            final Image image = new Image(uuid, label, data);
            images[i] = image;
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();

        return images;
    }

    public static long addTraining(final SQLiteDatabase sqLiteDatabase, final Training training) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put("uuid", training.getUUID());
        contentValues.put("createdBy", training.getCreatedBy());
        contentValues.put("locationUuid", training.getLocationUuid());
        contentValues.put("floorUuid", training.getFloorUuid());
        contentValues.put("timestamp", training.getTimestamp());
        contentValues.put("radiomap", training.getRadiomapAsJSON());
        contentValues.put("context", training.getContextAsJSON());
        contentValues.put("lat", training.getLat());
        contentValues.put("lng", training.getLng());

        final long row = sqLiteDatabase.insert("trainings", null, contentValues);
        sqLiteDatabase.close();
        return row;
    }

    public static boolean deleteTraining(final SQLiteDatabase sqLiteDatabase, final String trainingUuid) {
        int rows = sqLiteDatabase.delete("trainings", "uuid=?", new String [] { trainingUuid });
        sqLiteDatabase.close();
        return rows > 0;
    }

    public static int deleteAllTrainings(final SQLiteDatabase sqLiteDatabase, final String locationUuid) {
        int rows = sqLiteDatabase.delete("trainings", "locationUuid=?", new String [] { locationUuid});
        sqLiteDatabase.close();
        return rows;
    }

    public static int getNumOfTrainings(final SQLiteDatabase sqLiteDatabase, final String locationUuid) {
        final Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM trainings WHERE locationUuid=?", new String [] {locationUuid});
        int numOfRows = cursor.getCount();
        cursor.close();
        sqLiteDatabase.close();
        return numOfRows;
    }

    public static Training [] getTrainings(final SQLiteDatabase sqLiteDatabase, final String locationUuid) {
        final Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM trainings WHERE locationUuid=?", new String [] {locationUuid});
        int numOfRows = cursor.getCount();
        int columnUUIDIndex = cursor.getColumnIndex("uuid");
        int columnCreatedByIndex = cursor.getColumnIndex("createdBy");
        int columnFloorUuidByIndex = cursor.getColumnIndex("floorUuid");
        int columnTimestampByIndex = cursor.getColumnIndex("timestamp");
        int columnRadiomapByIndex = cursor.getColumnIndex("radiomap");
        int columnContextByIndex = cursor.getColumnIndex("context");
        int columnLatByIndex = cursor.getColumnIndex("lat");
        int columnLngByIndex = cursor.getColumnIndex("lng");

        final Training [] trainings = new Training[numOfRows];
        cursor.moveToFirst();
        for(int i = 0; i < numOfRows; i++) {
            final String uuid = cursor.getString(columnUUIDIndex);
            final String createdBy = cursor.getString(columnCreatedByIndex);
            final String floorUUid = cursor.getString(columnFloorUuidByIndex);
            final long timestamp = cursor.getLong(columnTimestampByIndex);
            final String radiomap = cursor.getString(columnRadiomapByIndex);
            final String context = cursor.getString(columnContextByIndex);
            final double lat = cursor.getDouble(columnLatByIndex);
            final double lng = cursor.getDouble(columnLngByIndex);
            trainings[i] = new Training(uuid, createdBy, locationUuid, floorUUid, timestamp, radiomap, context, lat, lng);
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();

        return trainings;
    }

    public static long addCustomContext(final SQLiteDatabase sqLiteDatabase, final String uuid, final String locationUuid, final String name, final String value, final boolean checked) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put("uuid", uuid);
        contentValues.put("locationUuid", locationUuid);
        contentValues.put("name", name);
        contentValues.put("value", value);
        contentValues.put("checked", checked ? 1 : 0);

        final long row = sqLiteDatabase.insert("customContext", null, contentValues);
        sqLiteDatabase.close();
        return row;
    }

    public static int editCustomContext(final SQLiteDatabase sqLiteDatabase, final CustomContextElement customContextElement) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put("locationUuid", customContextElement.getLocationUuid());
        contentValues.put("name", customContextElement.getName());
        contentValues.put("value", customContextElement.getValue());
        contentValues.put("checked", customContextElement.isChecked() ? 1 : 0);

        final int rows = sqLiteDatabase.update("customContext", contentValues, "uuid=?", new String [] {customContextElement.getUuid()});
        sqLiteDatabase.close();
        return rows;
    }

    public static int editCustomContext(final SQLiteDatabase sqLiteDatabase, final String uuid, final boolean checked) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put("checked", checked ? 1 : 0);

        final int rows = sqLiteDatabase.update("customContext", contentValues, "uuid=?", new String [] { uuid });
        sqLiteDatabase.close();
        return rows;
    }

    /**
     * Returns all {@link CustomContextElement}s.
     *
     * @param sqLiteDatabase
     * @param locationUuid
     * @return
     */
    public static CustomContextElement [] getCustomContextElements(final SQLiteDatabase sqLiteDatabase, final String locationUuid) {
        return getCustomContextElements(sqLiteDatabase, locationUuid, false);
    }

    /**
     * Returns all {@link CustomContextElement}s, unless hideUnchecked is set to true in which case
     * it returns only those that are checked.
     *
     * @param sqLiteDatabase
     * @param locationUuid
     * @param hideUnchecked
     * @return
     */
    public static CustomContextElement [] getCustomContextElements(final SQLiteDatabase sqLiteDatabase, final String locationUuid, final boolean hideUnchecked) {
        final Cursor cursor;
        if(hideUnchecked) {
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM customContext WHERE locationUuid=? AND checked=?", new String [] {locationUuid, "1"});
        } else {
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM customContext WHERE locationUuid=?", new String [] {locationUuid});
        }
        int numOfRows = cursor.getCount();
        int columnUUIDIndex = cursor.getColumnIndex("uuid");
        int columnNameIndex = cursor.getColumnIndex("name");
        int columnValueIndex = cursor.getColumnIndex("value");
        int columnCheckedIndex = cursor.getColumnIndex("checked");

        final CustomContextElement [] customContextElements = new CustomContextElement[numOfRows];
        cursor.moveToFirst();
        for(int i = 0; i < numOfRows; i++) {
            final String uuid = cursor.getString(columnUUIDIndex);
            final String name = cursor.getString(columnNameIndex);
            final String value = cursor.getString(columnValueIndex);
            final boolean checked = cursor.getInt(columnCheckedIndex) == 1;
            customContextElements[i] = new CustomContextElement(uuid, locationUuid, name, value, checked);
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();

        return customContextElements;
    }

    public static boolean deleteCustomContext(final SQLiteDatabase sqLiteDatabase, final CustomContextElement customContextElement) {
        int rows = sqLiteDatabase.delete("customContext", "uuid=?", new String [] { customContextElement.getUuid() });
        sqLiteDatabase.close();
        return rows > 0;
    }

}