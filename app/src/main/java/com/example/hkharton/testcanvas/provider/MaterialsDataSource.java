package com.example.hkharton.testcanvas.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

public class MaterialsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    private String[] materialColumns = {
        MySQLiteHelper.COLUMN_MATERIAL_ID,
        MySQLiteHelper.COLUMN_MATERIAL_NAME,
        MySQLiteHelper.COLUMN_MATERIAL_TYPE,
        MySQLiteHelper.COLUMN_MATERIAL_FILEPATH
    };

    private String[] characteristicColumns = {
            MySQLiteHelper.COLUMN_CHARACTERISTIC_ID,
            MySQLiteHelper.COLUMN_MATERIAL_FOREIGN_ID,
            MySQLiteHelper.COLUMN_CHARACTERISTIC
    };

    public MaterialsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean createMaterial(String materialName, int materialType, String materialFilepath, ArrayList<String> materialCharacteristic) {
        // start database transaction
        boolean insertionStatus = true;
        database.beginTransaction();

        try {
            // inserting material detail
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_MATERIAL_NAME, materialName);
            values.put(MySQLiteHelper.COLUMN_MATERIAL_TYPE, materialType);
            values.put(MySQLiteHelper.COLUMN_MATERIAL_FILEPATH, materialFilepath);
            long materialInsertId = database.insert(MySQLiteHelper.TABLE_MATERIALS, null, values);

            if (materialInsertId == -1) {
                // insertion failed
                throw new Exception("Failed Inserting Material Detail");
            }

            // inserting material characteristic detail
            Iterator<String> characteristicIterator = materialCharacteristic.iterator();
            while(characteristicIterator.hasNext()){
                // go to the element
                String eachCharacteristic = characteristicIterator.next();

                ContentValues charateristicValues = new ContentValues();
                charateristicValues.put(MySQLiteHelper.COLUMN_MATERIAL_FOREIGN_ID, materialInsertId);
                charateristicValues.put(MySQLiteHelper.COLUMN_CHARACTERISTIC, eachCharacteristic);
                long characteristicInsertId = database.insert(MySQLiteHelper.TABLE_CHARACTERISTIC, null, charateristicValues);

                if (characteristicInsertId == -1) {
                    // insertion failed
                    throw new Exception("Failed Inserting Characteristic Detail");
                }
            }

            // committing database transaction
            database.setTransactionSuccessful();
        } catch (Exception e) {
            insertionStatus = false;
        }finally {
            database.endTransaction();
        }

        return insertionStatus;
    }

    public boolean updateMaterialFilepath(int materialType, ArrayList<String> requestedCharacteristic) {
        String materialQuery =
            "SELECT DISTINCT Q1." + MySQLiteHelper.COLUMN_MATERIAL_FILEPATH + " "
            + "FROM " + MySQLiteHelper.TABLE_MATERIALS + " AS Q1 "
            + "JOIN "
            + MySQLiteHelper.TABLE_CHARACTERISTIC + " AS Q2 "
            + "ON Q1." + MySQLiteHelper.COLUMN_MATERIAL_ID + "=Q2." + MySQLiteHelper.COLUMN_MATERIAL_FOREIGN_ID + " "
            + "WHERE Q1." + MySQLiteHelper.COLUMN_MATERIAL_TYPE + "=" + materialType;

        // add characteristic filter if needed
        Cursor cursor = null;
        try {
            if (requestedCharacteristic != null && !requestedCharacteristic.isEmpty()) {
                String[] characteristicArray = requestedCharacteristic.toArray(new String[requestedCharacteristic.size()]);

                // add the WHERE clause
                String whereClause = "";
                for (int walk = 0; walk < requestedCharacteristic.size(); walk++) {
                    whereClause += "Q2." + MySQLiteHelper.COLUMN_CHARACTERISTIC + "=? ";

                    if (walk < (requestedCharacteristic.size() - 1)) {
                        whereClause += "OR ";
                    }
                }

                // attach where clause to main query
                materialQuery += " AND(" + whereClause + ")";
                cursor = database.rawQuery(materialQuery, characteristicArray);

                Log.e("TEST", materialQuery + "; size: " + cursor.getCount());
            } else {
                cursor = database.rawQuery(materialQuery, null);
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        /*
        List<Material> materials = new ArrayList<Material>();
        Cursor cursor = database.rawQuery(materialQuery, new String[]{String.valueOf(propertyId)});
        */
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TileImage.imagePath.add(cursor.getString(0));
            cursor.moveToNext();
        }

        // Make sure to close the cursor
        cursor.close();
        return true;
    }


    public void truncateMaterialDatabase(){
        database.execSQL("DELETE FROM " + MySQLiteHelper.TABLE_MATERIALS);
        database.execSQL("DELETE FROM " + MySQLiteHelper.TABLE_CHARACTERISTIC);
        database.execSQL("VACUUM");
    }

    /*
    public void deleteMaterial(Comment comment) {
        long id = comment.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_COMMENTS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Comment> getAllComments() {
        List<Comment> comments = new ArrayList<Comment>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Comment comment = cursorToComment(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return comments;
    }

    private Comment cursorToComment(Cursor cursor) {
        Comment comment = new Comment();
        comment.setId(cursor.getLong(0));
        comment.setComment(cursor.getString(1));
        return comment;
    }
    */
}