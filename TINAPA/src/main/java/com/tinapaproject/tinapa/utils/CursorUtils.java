package com.tinapaproject.tinapa.utils;

import android.widget.Spinner;

public class CursorUtils {
    public static int getPositionOfRowById(long id, Spinner spinner) {
        for (int i = 0; i < spinner.getCount(); i++) {
            long rowId = spinner.getItemIdAtPosition(i);
            if (id == rowId) {
                return i;
            }
        }
        return -1;
    }
}
