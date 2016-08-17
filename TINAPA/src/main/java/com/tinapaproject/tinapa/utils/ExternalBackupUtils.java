package com.tinapaproject.tinapa.utils;

import android.os.Environment;

import java.io.File;
import java.util.Date;

public class ExternalBackupUtils {
    private static final String EXTERNAL_FOLDER = "/TinapaBackup/";

    private static final String OWNED_CATEGORY_TAG = "Owned Pokemon";
    private static final String PLANNED_CATEGORY_TAG = "Planned Pokemon";
    private static final String TEAM_CATEGORY_TAG = "Planned Team";

    private static final String NAME_TAG = "Name: ";
    private static final String ID_TAG = "ID: ";
    private static final String SPECIES_TAG = "Species: ";
    private static final String ABILITY_TAG = "Ability: ";
    private static final String NATURE_TAG = "Nature: ";
    private static final String ITEM_TAG = "Item: ";


    public static File generateBackupFile() {
        String currentDate = new Date().toString();

        File backupFile = new File(Environment.getExternalStorageDirectory().getPath() + EXTERNAL_FOLDER, currentDate);

        return null;
    }
}
