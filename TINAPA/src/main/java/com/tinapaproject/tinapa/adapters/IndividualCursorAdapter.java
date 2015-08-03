package com.tinapaproject.tinapa.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinapaproject.tinapa.R;
import com.tinapaproject.tinapa.utils.ImageUtils;

public class IndividualCursorAdapter extends CursorAdapter {
    private String imageColumn;
    private String mainNameColumn;
    private String secondNameColumn;
    private Context mContext;

    private static final int FLAGS = 0;

    public static final String TAG = "IndividualCursorAdapter";

    public IndividualCursorAdapter(Context context, Cursor c, String mainNameColumn, String secondNameColumn, String imageColumn, final Uri uri) {
        super(context, c, FLAGS);
        this.mainNameColumn = mainNameColumn;
        this.secondNameColumn = secondNameColumn;
        this.imageColumn = imageColumn;
        this.mContext = context;

        this.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return mContext.getContentResolver().query(uri, null, String.valueOf(constraint), null, null);
            }
        });
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.cell_individual, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.cell_individual_image);
        String imageUri = cursor.getString(cursor.getColumnIndex(imageColumn));
        ImageUtils.loadImage(imageView, imageUri, true);

        TextView textView = (TextView) view.findViewById(R.id.cell_individual_name);
        String name;
        if (!TextUtils.isEmpty(mainNameColumn) && !TextUtils.isEmpty(name = cursor.getString(cursor.getColumnIndex(mainNameColumn)))) {
            textView.setText(name);
        } else if (!TextUtils.isEmpty(secondNameColumn)&& !TextUtils.isEmpty(name = cursor.getString(cursor.getColumnIndex(secondNameColumn)))) {
            textView.setText(name);
        } else {
            textView.setText("");
        }

    }

}
