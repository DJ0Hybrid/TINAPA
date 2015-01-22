package com.tinapaproject.tinapa.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinapaproject.tinapa.R;
import com.tinapaproject.tinapa.database.provider.TinapaContentProvider;
import com.tinapaproject.tinapa.utils.ImageUtils;

public class PlannedCursorAdapter extends CursorAdapter {
    private String imageColumn;
    private String nameColumn;
    private Context mContext;

    private static final int FLAGS = 0;

    public static final String TAG = "PlannedCursorAdapter";

    public PlannedCursorAdapter(Context context, Cursor c, String name, String imageColumn) {
        super(context, c, FLAGS);
        this.nameColumn = name;
        this.imageColumn = imageColumn;
        this.mContext = context;

        this.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return mContext.getContentResolver().query(TinapaContentProvider.PLANNED_POKEMON_SEARCH_GENERAL_URI, null, String.valueOf(constraint), null, null);
            }
        });
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.cell_individual, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.cellImage);
        String imageUri = cursor.getString(cursor.getColumnIndex(imageColumn));
        ImageUtils.loadImage(imageView, imageUri, true);

        TextView textView = (TextView) view.findViewById(R.id.cellName);
        String name = cursor.getString(cursor.getColumnIndex(nameColumn));
        if (textView != null) {
            textView.setText(name);
        }
    }
}
