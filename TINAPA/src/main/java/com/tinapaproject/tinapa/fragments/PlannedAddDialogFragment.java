package com.tinapaproject.tinapa.fragments;

import android.app.DialogFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.tinapaproject.tinapa.R;
import com.tinapaproject.tinapa.database.key.DexKeyValues;
import com.tinapaproject.tinapa.database.key.ItemKeyValues;
import com.tinapaproject.tinapa.database.provider.TinapaContentProvider;

public class PlannedAddDialogFragment extends DialogFragment {

    private Spinner mSpeciesSpinner;
    private Spinner mItemSpinner;

    public static final String TAG = "PlannedAddDialogFragment";

    public static PlannedAddDialogFragment newInstance() {
        PlannedAddDialogFragment fragment = new PlannedAddDialogFragment();

        return fragment;
    }

    public PlannedAddDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_planned_add_dialog, container, false);
        mSpeciesSpinner = (Spinner) view.findViewById(R.id.planned_add_species_spinner);

        Cursor speciesCursor = getActivity().getContentResolver().query(TinapaContentProvider.POKEDEX_ALL_SHORT_URI, null, null, null, null);
        String[] from = {DexKeyValues.name};
        int[] to = {R.id.simple_cell_name};
        CursorAdapter mSpeciesCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.cell_simple_name, speciesCursor, from, to, 0);
        mSpeciesSpinner.setAdapter(mSpeciesCursorAdapter);


        Cursor itemCursor = getActivity().getContentResolver().query(TinapaContentProvider.ITEM_BATTLE_URI, null, null, null, null);
        mItemSpinner = (Spinner) view.findViewById(R.id.planned_add_item_spinner);
        String[] itemFrom = {ItemKeyValues.name};
        int[] itemTo = {R.id.simple_cell_name};
        CursorAdapter mItemCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.cell_simple_name, itemCursor, itemFrom, itemTo, 0);
        mItemSpinner.setAdapter(mItemCursorAdapter);

        return view;
    }
}
