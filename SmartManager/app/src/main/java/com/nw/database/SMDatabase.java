package com.nw.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Environment;
import android.widget.Toast;

import com.nw.webservice.DataManager;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.utils.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class SMDatabase extends SQLiteAssetHelper
{

    private static final String DATABASE_NAME = "smartmanager.db";
    private static final int DATABASE_VERSION = 1;
    private String TABLE_ImageUploding = "ImageUploding";
    private String TABLE_VideoUploading = "VideoUploading";
    private Context context;

    public SMDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        // you can use an alternate constructor to specify a database location
        // (such as a folder on the sd card)
        // you must ensure that this folder is available and you have permission
        // to write to it
        //super(context, DATABASE_NAME, context.getExternalFilesDir(null).getAbsolutePath(), null, DATABASE_VERSION);

    }

    public void insertRecords(String str_webservice_data,String type)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("WebserviceData", str_webservice_data);
        values.put("IsUploded", 0);
        values.put("ServiceType", type);
        database.insert(TABLE_ImageUploding, null, values);
        database.close();
      //  exportDB();
    }

    public void insertVideoUploadingRecords(String str_VideoFullPath,String str_VariantId,String str_VideoTitle,String str_VideoDescription,String str_VideoTags,String str_Searchable)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("VideoFullPath", str_VideoFullPath);
        values.put("VariantId", str_VariantId);
        values.put("VideoTitle", str_VideoTitle);
        values.put("VideoDescription", str_VideoDescription);
        values.put("VideoTags", str_VideoTags);
        values.put("Searchable", str_Searchable);
        database.insert(TABLE_VideoUploading, null, values);
        database.close();
    }

    public void deleteRecords(int str_id)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_ImageUploding, "Id=?", new String[]{"" + str_id});
        database.close();
    }

    public void deleteVideoRecords(int str_id)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_VideoUploading, "Id=?", new String[]{"" + str_id});
        database.close();
    }

    public ArrayList<String> getVideoRequiestData(int id)
    {
        ArrayList<String> str_requiestData = new ArrayList<>();
        SQLiteDatabase database = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM "+TABLE_VideoUploading+" where Id = " + id;
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            do
            {
                str_requiestData.add(""+cursor.getString(1));
                str_requiestData.add(""+cursor.getString(2));
                str_requiestData.add(""+cursor.getString(3));
                str_requiestData.add(""+cursor.getString(4));
                str_requiestData.add(""+cursor.getString(5));
                str_requiestData.add(""+cursor.getString(6));
            //    str_requiestData = str_requiestData.replaceAll(Constants.CHANGE_THIS_USER_HASH, DataManager.getInstance().user.getUserHash());
            } while (cursor.moveToNext());
        }
        database.close();
        return str_requiestData;
    }

    public String getRequiestData(int id)
    {
        String str_requiestData = null;
        SQLiteDatabase database = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM "+TABLE_ImageUploding+" where Id = " + id;
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            do
            {
                str_requiestData = cursor.getString(1);
                str_requiestData = str_requiestData.replaceAll(Constants.CHANGE_THIS_USER_HASH, DataManager.getInstance().user.getUserHash());
            } while (cursor.moveToNext());
        }
        database.close();
        return str_requiestData;
    }

    public String getRequiestType(int id)
    {
        String str_requiestType = null;
        SQLiteDatabase database = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM "+TABLE_ImageUploding+" where Id = " + id;
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            do
            {
                str_requiestType = cursor.getString(3);
            } while (cursor.moveToNext());
        }
        database.close();
        return str_requiestType;
    }

    public List<Integer> getAllRecordIds()
    {
        List<Integer> IdList = new ArrayList<Integer>();
        SQLiteDatabase database = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM "+TABLE_ImageUploding;
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            do
            {
                IdList.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        database.close();
        return IdList;
    }

    public List<Integer> getAllVideoRecordIds()
    {
        List<Integer> IdList = new ArrayList<Integer>();
        SQLiteDatabase database = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM "+TABLE_VideoUploading;
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            do
            {
                IdList.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        database.close();
        return IdList;
    }


    public void exportDB()
    {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String currentDBPath = "/data/" + "com.smartmanager.android" + "/databases/" + DATABASE_NAME;
        String backupDBPath = DATABASE_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try
        {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            System.out.println("DB sssssssssssssssssssssssssss Exported!");
          //  Toast.makeText(context, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
