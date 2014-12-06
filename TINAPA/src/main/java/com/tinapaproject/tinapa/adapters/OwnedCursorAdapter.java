package com.tinapaproject.tinapa.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinapaproject.tinapa.R;

public class OwnedCursorAdapter extends CursorAdapter {
    private String imageColumn;
    private String nameColumn;
    private String nicknameColumn;

    private static final int FLAGS = 0;

    public static final String TAG = "OwnedCursorAdapter";

    public OwnedCursorAdapter(Context context, Cursor c, String name, String nickname, String imageColumn) {
        super(context, c, FLAGS);
        this.nameColumn = name;
        this.nicknameColumn = nickname;
        this.imageColumn = imageColumn;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.cell_owned, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.cellOwnedImageView);
        String imageUri = cursor.getString(cursor.getColumnIndex(imageColumn));
//        loadImage(imageUri, imageView);   // TODO: Thinking of just having a util method to do this.

        TextView textView = (TextView) view.findViewById(R.id.cellOwnedTextView);
        String nickname = cursor.getString(cursor.getColumnIndex(nicknameColumn));
        if (textView != null) {
            if (nickname != null) {
                textView.setText(nickname);
            } else {
                String name = cursor.getString(cursor.getColumnIndex(nameColumn));
                if (name != null) {
                    textView.setText(name);
                } else {
                    textView.setText("");
                }
            }
        }
    }
}
