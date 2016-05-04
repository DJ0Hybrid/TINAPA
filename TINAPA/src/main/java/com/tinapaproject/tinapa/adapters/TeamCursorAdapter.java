package com.tinapaproject.tinapa.adapters;

import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.tinapaproject.tinapa.R;
import com.tinapaproject.tinapa.database.key.TeamKeyValues;
import com.tinapaproject.tinapa.events.TeamListSelectedEvent;
import com.tinapaproject.tinapa.utils.ImageUtils;

public class TeamCursorAdapter extends CursorAdapter {
    private Context mContext;
    private Bus bus;

    private static final int FLAGS = 0;

    public static final String TAG = "MultipleCursorAdapter";

    public TeamCursorAdapter(Context context, Cursor c, Bus bus, final Uri uri) {
        super(context, c, FLAGS);
        this.bus = bus;
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
        return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.cell_team, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView teamNameView = (TextView) view.findViewById(R.id.cell_team_name);
        ImageView image1View = (ImageView) view.findViewById(R.id.cell_team_1_image);
        ImageView image2View = (ImageView) view.findViewById(R.id.cell_team_2_image);
        ImageView image3View = (ImageView) view.findViewById(R.id.cell_team_3_image);
        ImageView image4View = (ImageView) view.findViewById(R.id.cell_team_4_image);
        ImageView image5View = (ImageView) view.findViewById(R.id.cell_team_5_image);
        ImageView image6View = (ImageView) view.findViewById(R.id.cell_team_6_image);

        String image1Uri = cursor.getString(cursor.getColumnIndex(TeamKeyValues.POKEMON1_ICON));
        String image2Uri = cursor.getString(cursor.getColumnIndex(TeamKeyValues.POKEMON2_ICON));
        String image3Uri = cursor.getString(cursor.getColumnIndex(TeamKeyValues.POKEMON3_ICON));
        String image4Uri = cursor.getString(cursor.getColumnIndex(TeamKeyValues.POKEMON4_ICON));
        String image5Uri = cursor.getString(cursor.getColumnIndex(TeamKeyValues.POKEMON5_ICON));
        String image6Uri = cursor.getString(cursor.getColumnIndex(TeamKeyValues.POKEMON6_ICON));

        ImageUtils.loadImage(image1View, image1Uri, true);
        ImageUtils.loadImage(image2View, image2Uri, true);
        ImageUtils.loadImage(image3View, image3Uri, true);
        ImageUtils.loadImage(image4View, image4Uri, true);
        ImageUtils.loadImage(image5View, image5Uri, true);
        ImageUtils.loadImage(image6View, image6Uri, true);

        final String teamId = cursor.getString(cursor.getColumnIndex(TeamKeyValues.TEAM_ID));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(new TeamListSelectedEvent(teamId));
            }
        });
    }
}
