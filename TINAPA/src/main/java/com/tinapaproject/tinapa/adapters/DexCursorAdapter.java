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
import com.tinapaproject.tinapa.fragments.DexListFragment.DexListListener;
import com.tinapaproject.tinapa.utils.ImageUtils;

/**
 * Created by Ethan on 8/20/2014.
 */
public class DexCursorAdapter extends CursorAdapter {
    private String imageColumn;
    private String nameColumn;

    private final Context mContext;

    private static final int FLAGS = 0;

    private DexListListener listener;

    public static final String TAG = "DexCursorAdapter";

    public DexCursorAdapter(Context context, DexListListener listener, Cursor c, String name, String imageColumn) {
        super(context, c, FLAGS);
        this.mContext = context;
        this.nameColumn = name;
        this.imageColumn = imageColumn;
        this.listener = listener;

        this.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return mContext.getContentResolver().query(TinapaContentProvider.POKEDEX_ALL_SHORT_URI, null, String.valueOf(constraint), null, null);
            }
        });
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.cell_individual, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final String id = cursor.getString(cursor.getColumnIndex("_id"));

        ImageView imageView = (ImageView) view.findViewById(R.id.cell_individual_image);
        String imagePath = cursor.getString(cursor.getColumnIndex(imageColumn));
        ImageUtils.loadImage(imageView, imagePath, true);

        TextView textView = (TextView) view.findViewById(R.id.cell_individual_name);
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
                    ImageView image = (ImageView) v.findViewById(R.id.cell_individual_image);
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


}
