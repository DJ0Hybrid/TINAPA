/**
 * Refer to:
 * http://www.drdobbs.com/database/using-sqlite-on-android/232900584
 */

package com.tinapaproject.tinapa.database;

import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.squareup.otto.Bus;
import com.tinapaproject.tinapa.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TinapaDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tinapa.db";
    protected Context context;
    protected ContentResolver contentResolver;
    protected Bus bus;

    public static final String TAG = "TinapaDatabaseHelper";

    public TinapaDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.contentResolver = context.getContentResolver();
        getWritableDatabase();

        createBus();

        Log.d(TAG, "Constructor is done.");
    }

    private void createBus() {
        if (bus == null) {
            bus = new Bus();
            bus.register(this);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "OnCreate called.");
        createBus();
        executeSQLScript(db, R.raw.database_creation, "database_creation.sql");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "OnUpgrade called.");
        createBus();
        if (newVersion > oldVersion) {
            switch (oldVersion) {
                case 1:
                    // This is the current version.
                    // When the time comes, you would add an update script to go from the old version (1) to the new version (2).
            }
        }
    }

    private void executeSQLScript(SQLiteDatabase db, int rawScript, String scriptName) {
        InputStream inputStream = null;
        db.beginTransaction();
        try {
//            bus.post(new DatabaseCreationUpdateEvent());

            inputStream = context.getResources().openRawResource(rawScript);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sqlLine = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                if (line.endsWith(");") || line.endsWith("\";")) {
                    sqlLine.append(line);
                    db.execSQL(sqlLine.toString());
                    sqlLine = new StringBuilder();
                } else {
                    sqlLine.append(line + '\n');
                }
//                bus.post(new DatabaseCreationUpdateEvent());
            }
            db.setTransactionSuccessful();
            bufferedReader.close();
            inputStream.close();

            Log.d(TAG, "Completed running the script " + scriptName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            // Script failed to load
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }
    }
}
