package com.yuan.locationremind.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yuan.locationremind.entity.LocationEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 地址dao
 */

public class LocationDao {

    private SQLiteHelper mHelper;

    public LocationDao(Context context) {
        mHelper = new SQLiteHelper(context, "address", null, 1);
    }

    public boolean insert(LocationEntity entity) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        long insert = database.insert("address_status", null, getContentValues(entity));
        System.out.println("insert");
        database.close();
        return insert != -1;
    }

    public LocationEntity query(String id) {
        try {
            SQLiteDatabase database = mHelper.getReadableDatabase();
            Cursor cursor = null;
            try {
                cursor = database.query("address_status", null, "addressId=?", new String[]{id}, null, null, null, null);
                if (cursor.moveToNext()) {
                    LocationEntity entity = new LocationEntity();
                    String addressId = cursor.getString(cursor.getColumnIndex("addressId"));
                    double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                    double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                    String address = cursor.getString(cursor.getColumnIndex("address"));
                    String isSelect = cursor.getString(cursor.getColumnIndex("isSelect"));
                    entity.setAddress(address);
                    entity.setAddressId(addressId);
                    entity.setLatitude(latitude);
                    entity.setLongitude(longitude);
                    entity.setIsSelected(isSelect);
                    return entity;

                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public List<LocationEntity> queryAll() {
        SQLiteDatabase database = mHelper.getReadableDatabase();
        Cursor cursor = null;
        List<LocationEntity> list = new ArrayList<>();
        try {
            cursor = database.query("address_status", null, null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                LocationEntity entity = new LocationEntity();
                String addressId = cursor.getString(cursor.getColumnIndex("addressId"));
                double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                String address = cursor.getString(cursor.getColumnIndex("address"));
                String isSelect = cursor.getString(cursor.getColumnIndex("isSelect"));
                entity.setAddress(address);
                entity.setAddressId(addressId);
                entity.setLatitude(latitude);
                entity.setLongitude(longitude);
                entity.setIsSelected(isSelect);

                list.add(entity);
            }
            return list;
        } catch (Exception e) {
            return new ArrayList<>();
        } finally {
            database.close();
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public boolean update(LocationEntity entity) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        long update = database.update("address_status", getContentValues(entity), "addressId=?", new String[]{entity.getAddressId()});
        database.close();
        return update != -1;
    }

    public boolean delete(LocationEntity entity) {
        if (entity == null) return false;
        SQLiteDatabase database = mHelper.getWritableDatabase();
        long delete = database.delete("address_status", "addressId=?", new String[]{entity.getAddressId()});
        database.close();
        return delete != -1;
    }

    public boolean drop() {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        database.execSQL("DROP TABLE address_status");
        database.close();
        return true;
    }

    private ContentValues getContentValues(LocationEntity entity) {
        ContentValues cv = new ContentValues();
        cv.put("addressId", entity.getAddressId());
        cv.put("address", entity.getAddress());
        cv.put("latitude", entity.getLatitude());
        cv.put("longitude", entity.getLongitude());
        cv.put("isSelected", entity.getIsSelected());
        return cv;
    }


}
