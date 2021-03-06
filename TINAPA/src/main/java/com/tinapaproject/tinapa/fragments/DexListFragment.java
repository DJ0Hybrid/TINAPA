package com.tinapaproject.tinapa.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.tinapaproject.tinapa.R;
import com.tinapaproject.tinapa.adapters.IndividualCursorAdapter;
import com.tinapaproject.tinapa.database.key.DexKeyValues;
import com.tinapaproject.tinapa.database.provider.TinapaContentProvider;

/**
 * List Fragment for handling all of the dex entries.
 */
public class DexListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private IndividualCursorAdapter adapter;

    private DexListListener mListener;

    public static final String TAG = "DexListFragment";

    public DexListFragment() {
        // Required.
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DexListListener) {
            mListener = (DexListListener) activity;
        }
        Log.d(TAG, "Attached.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "Inflating view.");
        View view = inflater.inflate(R.layout.fragment_individual_list, container, false);

        GridView gridView = (GridView) view.findViewById(R.id.individual_list_grid);

        Cursor c = getActivity().getContentResolver().query(TinapaContentProvider.POKEDEX_ALL_SHORT_URI, null, null, null, null);
        if (c == null) {
            Log.w(TAG, "Initial cursor is null!");
        } else {
            Log.v(TAG, "Cursor is set.");
        }
        adapter = new IndividualCursorAdapter(getActivity(), c, DexKeyValues.name, null, DexKeyValues.iconImage, TinapaContentProvider.POKEDEX_ALL_SHORT_URI);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onDexItemClicked(String.valueOf(id));
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onDexImageLongClicked(String.valueOf(id), (ImageView) view.findViewById(R.id.cell_individual_image), false, false, true);
                return true;
            }
        });

        getLoaderManager().initLoader(0, null, this);

        EditText searchField = (EditText) view.findViewById(R.id.individual_list_search);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing.
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.d(TAG, "Detached.");
    }

    // From LoaderManager.LoaderCallbacks<Cursor>
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    // From LoaderManager.LoaderCallbacks<Cursor>
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    // From LoaderManager.LoaderCallbacks<Cursor>
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    public interface DexListListener {
        public void onDexItemClicked(String id);

        public void onDexImageLongClicked(String id, ImageView imageView, boolean isDefault, boolean isShiny, boolean isIcon);
    }
}
