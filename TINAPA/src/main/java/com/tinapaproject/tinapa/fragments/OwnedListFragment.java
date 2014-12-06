package com.tinapaproject.tinapa.fragments;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.tinapaproject.tinapa.R;
import com.tinapaproject.tinapa.adapters.OwnedCursorAdapter;
import com.tinapaproject.tinapa.database.provider.TinapaContentProvider;

public class OwnedListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private OwnedCursorAdapter adapter;

    private OwnedListListener listener;

    public static final String TAG = "OwnedListFragment";

    public OwnedListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OwnedListListener) {
            listener = (OwnedListListener) activity;
            if (adapter != null) {
//                adapter.setListener(listener);
            }
        }
        Log.d(TAG, "Attached.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_owned_list, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.ownedGridView);
        Cursor c = getActivity().getContentResolver().query(TinapaContentProvider.OWNED_POKEMON_SEARCH_GENERAL_URI, null, null, null, null);
        if (c == null) {
            Log.w(TAG, "Initial cursor is null!");
        } else {
            Log.d(TAG, "Cursor is set.");
        }
        // TODO: The column names are probably not correct.
        adapter = new OwnedCursorAdapter(getActivity(), c, "name", "nickname", "image");
        gridView.setAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        if (adapter != null) {
//            adapter.setListener(null);
        }
        Log.d(TAG, "Detached.");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    public interface OwnedListListener {
        public void onOwnedItemClicked(String topic, String id);
    }
}
