package com.tinapaproject.tinapa.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.tinapaproject.tinapa.R;
import com.tinapaproject.tinapa.utils.ImageUtils;

import org.w3c.dom.Text;

/**
 * Created by Ethan on 8/20/2014.
 */
public class DexCursorAdapter extends CursorAdapter {
    private String imageColumn;
    private String nameColumn;

    private static final int FLAGS = 0;

    private DexListListener listener;

    public static final String TAG = "DexCursorAdapter";

    public DexCursorAdapter(Context context, DexListListener listener, Cursor c, String name, String imageColumn) {
        super(context, c, FLAGS);
        this.nameColumn = name;
        this.imageColumn = imageColumn;
        this.listener = listener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.cell_dex, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final String id = cursor.getString(cursor.getColumnIndex("_id"));

        ImageView imageView = (ImageView) view.findViewById(R.id.cellDexImageView);
        String imagePath = cursor.getString(cursor.getColumnIndex(imageColumn));
        ImageUtils.loadImage(imageView, imagePath, true);

        TextView textView = (TextView) view.findViewById(R.id.cellDexTextView);
        String name = cursor.getString(cursor.getColumnIndex(nameColumn));
        if (textView != null) {
            if (name != null) {
                textView.setText(name);
            } else {
                textView.setText("");
            }
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDexItemClicked("", id);
                }
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    ImageView image = (ImageView) v.findViewById(R.id.cellDexImageView);
                    if (image != null) {
                        listener.onDexImageLongClicked(id, image, imageColumn);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void setListener(DexListListener listener) {
        this.listener = listener;
    }

    public interface DexListListener {
        public void onDexItemClicked(String topic, String id);
        public void onDexImageLongClicked(String id, ImageView imageView, String column);
    }
}
