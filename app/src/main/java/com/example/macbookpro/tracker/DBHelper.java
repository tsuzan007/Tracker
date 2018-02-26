package com.example.macbookpro.tracker;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by macbookpro on 2/25/18.
 */

public class DBHelper extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_PATH = "/data/data/com.example.macbookpro.tracker/databases/";
    // Database Name
    private static final String DATABASE_NAME = "mydatabase.sqlite";
    // Contacts table name
    private static final String TABLE_CONTACT = "BusStops";
    private SQLiteDatabase db;
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "Name";
    private static final String KEY_TIME= "Time";
    private static final String KEY_LATITUDE = "Latitude";
    private static final String KEY_LONGITUDE = "Longitude";

    Context ctx;
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctx = context;
    }


    // Getting single contact
    public void Get_ContactDetails(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACT, new String[] { KEY_ID,
                        KEY_NAME, KEY_TIME, KEY_LATITUDE,KEY_LONGITUDE}, null,
                null, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do{
                    Log.e("....",cursor.getString(3)+cursor.getString(4));
                } while (cursor.moveToNext());
            }
            // return contact
            cursor.close();
            db.close();

        }
    }
    public void CopyDataBaseFromAsset() throws IOException{
        InputStream in  = ctx.getAssets().open("mydatabase.sqlite");
        Log.e("sample", "Starting copying" );
        String outputFileName = DATABASE_PATH+DATABASE_NAME;
        File databaseFile = new File( "/data/data/com.example.macbookpro.tracker/databases");
        // check if databases folder exists, if not create one and its subfolders
        if (!databaseFile.exists()){
            databaseFile.mkdir();
        }

        OutputStream out = new FileOutputStream(outputFileName);

        byte[] buffer = new byte[1024];
        int length;


        while ((length = in.read(buffer))>0){
            out.write(buffer,0,length);
        }
        Log.e("sample", "Completed" );
        out.flush();
        out.close();
        in.close();

    }


    public void openDataBase () throws SQLException {
        String path = DATABASE_PATH+DATABASE_NAME;
        db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }
}
