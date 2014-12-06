/**
 * Refer to:
 * http://www.drdobbs.com/database/using-sqlite-on-android/232900584
 */

package com.tinapaproject.tinapa.database;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tinapaproject.tinapa.R;
import com.tinapaproject.tinapa.database.provider.TinapaContentProvider;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ethan on 8/20/2014.
 */
public class TinapaDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tinapa.db";
    protected Context context;
    protected ContentResolver contentResolver;

    public static final String TAG = "TinapaDatabaseHelper";

    public TinapaDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.contentResolver = context.getContentResolver();
        getWritableDatabase();
        Log.d(TAG, "Constructor is done.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "OnCreate called.");
        executeSQLScript(db, R.raw.database_creation, "database_creation.sql");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "OnUpgrade called.");
        if (newVersion > oldVersion) {
            switch (oldVersion) {
                case 1:
                    // This is the current version.
                    // When the time comes, you would add an update script to go from the old version (1) to the new version (2).
            }
        }
    }

    public Cursor getPokedexShortList() {
        return contentResolver.query(TinapaContentProvider.POKEDEX_ALL_SHORT_URI, null, null, null, null);
    }

    @Deprecated
    public Cursor getPokedexGridCursor() {
        return getPokedexGridCursor("");
    }

    @Deprecated
    public Cursor getPokedexGridCursor(String where) {
        Map<String, String> selectAs = new HashMap<String, String>();
        selectAs.put("id", "_id");
        selectAs.put("name", "name");
        selectAs.put("image", "image");
        return getPokedexGridCursor(selectAs, where);
    }

    @Deprecated
    public Cursor getPokedexGridCursor(Map<String, String> selectAs, String where) {
        StringBuilder query = new StringBuilder("SELECT");
        if (selectAs.size() > 0) {
            for (String selection : selectAs.keySet()) {
                query.append(" ");
                query.append(selection);
                String as = selectAs.get(selection);
                if (as != null && !as.isEmpty()) {
                    query.append(" AS ");
                    query.append(as);
                }
                query.append(",");
            }
            query.replace(query.length()-1, query.length(), "");    // Remove the last comma.
        } else {
            query.append(" *");
        }
        query.append("\n");

        query.append("FROM pokemon JOIN pokemon_species_names\n");

        query.append("WHERE pokemon.id == pokemon_species_id");
        query.append(" AND local_language_id == 9");    // TODO: Do more than just English.

        if (where != null && !where.isEmpty()) {
            query.append(" AND ");
            query.append(where);
        }

        query.append("\n");
        query.append("ORDER BY \"order\";");

        return getReadableDatabase().rawQuery(query.toString(), null);
    }

//    private void executeSQLScript(SQLiteDatabase db, int rawScript, String scriptName) {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        byte buff[] = new byte[1024];
//        int len;
//        InputStream inputStream = null;
//        db.beginTransaction();
//        try {
//            inputStream = context.getResources().openRawResource(rawScript);
//            while ((len = inputStream.read(buff)) != -1) {
//                outputStream.write(buff, 0, len);
//            }
//            outputStream.close();
//            inputStream.close();
//
//            String[] script = outputStream.toString().split(";");
//            for (int i = 0; i < script.length; i++) {
//                String scriptLine = script[i].trim();
//                if (scriptLine.length() > 0) {
//                    db.execSQL(scriptLine + ";");
//                }
//            }
//            db.setTransactionSuccessful();
//            Log.d(TAG, "Completed running the script " + scriptName);
//        } catch (IOException e) {
//            // Script failed to load
//            e.printStackTrace();
//            Log.e(TAG, e.getMessage());
//        } finally {
//            db.endTransaction();
//        }
//    }

    private void executeSQLScript(SQLiteDatabase db, int rawScript, String scriptName) {
       InputStream inputStream = null;
        db.beginTransaction();
        try {
            inputStream = context.getResources().openRawResource(rawScript);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sqlLine = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                if (line.endsWith(");") || line.endsWith("\";")) {
                    sqlLine.append(line);
                    Log.d(TAG, sqlLine.toString());
                    db.execSQL(sqlLine.toString());
                    sqlLine = new StringBuilder();
                } else {
                    sqlLine.append(line + '\n');
                }
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
