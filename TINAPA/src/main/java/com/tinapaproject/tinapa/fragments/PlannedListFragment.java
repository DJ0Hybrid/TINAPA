package com.tinapaproject.tinapa.fragments;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.tinapaproject.tinapa.R;
import com.tinapaproject.tinapa.adapters.PlannedCursorAdapter;
import com.tinapaproject.tinapa.database.key.PlannedKeyValues;
import com.tinapaproject.tinapa.database.provider.TinapaContentProvider;

public class PlannedListFragment extends Fragment {

    private PlannedCursorAdapter mAdapter;

    private PlannedListListener mListener;

    public PlannedListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof PlannedListListener) {
            mListener = (PlannedListListener) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_individual_list, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.individual_list_grid);

        Cursor c = getActivity().getContentResolver().query(TinapaContentProvider.PLANNED_POKEMON_SEARCH_GENERAL_URI, null, null, null, null);
        mAdapter = new PlannedCursorAdapter(getActivity(), c, PlannedKeyValues.NAME, PlannedKeyValues.ICON_IMAGE);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.plannedItemClicked(String.valueOf(id));
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.plannedItemLongClicked(String.valueOf(id));
                return true;
            }
        });

        EditText searchField = (EditText) view.findViewById(R.id.individual_list_search);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s);
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
    }

    public interface PlannedListListener {
        public void plannedItemClicked(String id);
        public void plannedItemLongClicked(String id);
    }
}
