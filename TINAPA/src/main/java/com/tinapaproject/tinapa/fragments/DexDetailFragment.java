package com.tinapaproject.tinapa.fragments;



import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinapaproject.tinapa.R;
import com.tinapaproject.tinapa.database.key.DexKeyValues;
import com.tinapaproject.tinapa.utils.ImageUtils;

import org.w3c.dom.Text;


public class DexDetailFragment extends Fragment {
    private Cursor pokemonCursor;
    private Cursor movesCursor;
    private DexDetailListener listener;

    public static final String TAG = "DexDetailFragment";

    public static DexDetailFragment newInstance(Cursor pokemonCursor, Cursor movesCursor) {
        DexDetailFragment fragment = new DexDetailFragment();
        fragment.pokemonCursor = pokemonCursor;
        fragment.movesCursor = movesCursor;
        return fragment;
    }

    public DexDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DexDetailListener) {
            listener = (DexDetailListener) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dex_detail, container, false);

        if (pokemonCursor != null && pokemonCursor.moveToFirst()) {
            final String id = pokemonCursor.getString(pokemonCursor.getColumnIndex("_id"));
            // TODO: Cursor dump needs to be removed eventually.
            TextView cursorDump = (TextView) view.findViewById(R.id.dexDetailCursorDump);
            if (cursorDump != null) {
                StringBuilder sb = new StringBuilder();
                int columnCount = pokemonCursor.getColumnCount();
                for (int i = 0; i < columnCount; i++) {
                    sb.append(pokemonCursor.getColumnName(i));
                    sb.append(" = ");
                    sb.append(pokemonCursor.getString(i));
                    sb.append("\n");
                }
                cursorDump.setText(sb.toString());
            }
            // TODO: End of Cursor dump.

            // Images
            ImageView normalImageView = (ImageView) view.findViewById(R.id.dexDetailNormalImage);
            String normalImagePath = pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.image));
            ImageUtils.loadImage(normalImageView, normalImagePath, true);
            normalImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    if (listener != null) {
                        listener.onDexDetailImageLongClicked(id, (ImageView) v, DexKeyValues.image);
                        return true;
                    }
                    return false;
                }
            });

            ImageView shinnyImageView = (ImageView) view.findViewById(R.id.dexDetailShinnyImage);
            String shinnyImagePath = pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.shinnyImage));
            ImageUtils.loadImage(shinnyImageView, shinnyImagePath, true);
            shinnyImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    if (listener != null) {
                        listener.onDexDetailImageLongClicked(id, (ImageView) v, DexKeyValues.shinnyImage);
                        return true;
                    }
                    return false;
                }
            });

            // Basic Info
            TextView numberView = (TextView) view.findViewById(R.id.dexDetailNumber);
            numberView.setText(pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.number)));

            TextView nameView = (TextView) view.findViewById(R.id.dexDetailName);
            nameView.setText(pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.name)));

            TextView type1View = (TextView) view.findViewById(R.id.dexDetailType1);
            type1View.setText(pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.type1)));

            TextView type2View = (TextView) view.findViewById(R.id.dexDetailType2);
            type2View.setText(pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.type2)));

            // Abilities
            TextView ability1NameView = (TextView) view.findViewById(R.id.dexDetailAbilityName1);
            ability1NameView.setText(pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.ability1Name)));
            // TODO
            TextView ability1DescView = (TextView) view.findViewById(R.id.dexDetailAbilityDescription1);

            String ability1ID = pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.ability1ID));
            String ability2ID = pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.ability2ID));
            if (!TextUtils.isEmpty(ability2ID) && !ability1ID.equalsIgnoreCase(ability2ID)) {
                TextView ability2NameView = (TextView) view.findViewById(R.id.dexDetailAbilityName2);
                ability2NameView.setText(pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.ability2Name)));
                // TODO
                TextView ability2DescView = (TextView) view.findViewById(R.id.dexDetailAbilityDescription2);
            } else {
                View ability2TableRow = view.findViewById(R.id.dexDetailAbility2TableRow);
                ability2TableRow.setVisibility(View.GONE);
            }

            TextView ability3NameView = (TextView) view.findViewById(R.id.dexDetailAbilityName3);
            ability3NameView.setText(pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.ability3Name)));
            // TODO
            TextView ability3DescView = (TextView) view.findViewById(R.id.dexDetailAbilityDescription3);
        }

        return view;
    }

    public interface DexDetailListener {
        public void onDexDetailImageLongClicked(String id, ImageView imageView, String column);
    }
}
