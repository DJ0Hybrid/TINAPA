package com.tinapaproject.tinapa.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.tinapaproject.tinapa.R;
import com.tinapaproject.tinapa.database.key.DexKeyValues;
import com.tinapaproject.tinapa.database.key.ItemKeyValues;
import com.tinapaproject.tinapa.database.provider.TinapaContentProvider;
import com.tinapaproject.tinapa.utils.CursorUtils;

public class PlannedAddDialogFragment extends DialogFragment {

    private Spinner mSpeciesSpinner;
    private Spinner mAbilitySpinner;
    private Spinner mMove1Spinner;
    private Spinner mMove2Spinner;
    private Spinner mMove3Spinner;
    private Spinner mMove4Spinner;
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
        mAbilitySpinner = (Spinner) view.findViewById(R.id.planned_add_ability_spinner);
        mMove1Spinner = (Spinner) view.findViewById(R.id.planned_add_move1_spinner);
        mMove2Spinner = (Spinner) view.findViewById(R.id.planned_add_move2_spinner);
        mMove3Spinner = (Spinner) view.findViewById(R.id.planned_add_move3_spinner);
        mMove4Spinner = (Spinner) view.findViewById(R.id.planned_add_move4_spinner);
        mItemSpinner = (Spinner) view.findViewById(R.id.planned_add_item_spinner);

        Cursor speciesCursor = getActivity().getContentResolver().query(TinapaContentProvider.POKEDEX_ALL_SHORT_URI, null, null, null, null);
        String[] from = {DexKeyValues.name};
        int[] to = {R.id.simple_cell_name};
        CursorAdapter mSpeciesCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.cell_simple_name, speciesCursor, from, to, 0);
        mSpeciesSpinner.setAdapter(mSpeciesCursorAdapter);

        mSpeciesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadAbilityAndMovesCursorAdapters(id, -1, -1, -1, -1, -1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing!
            }
        });


        Cursor itemCursor = getActivity().getContentResolver().query(TinapaContentProvider.ITEM_BATTLE_URI, null, null, null, null);
        String[] itemFrom = {ItemKeyValues.name};
        int[] itemTo = {R.id.simple_cell_name};
        CursorAdapter mItemCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.cell_simple_name, itemCursor, itemFrom, itemTo, 0);
        mItemSpinner.setAdapter(mItemCursorAdapter);

        return view;
    }

    private void loadAbilityAndMovesCursorAdapters(long pokemonId, long abilityId, long move1Id, long move2Id, long move3Id, long move4Id) {
        // Load up all the other spinners.
        // It is assumed that this will go off automatically.
        Cursor movesCursor = getActivity().getContentResolver().query(TinapaContentProvider.POKEDEX_POKEMON_MOVES_URI, null, "pokemon_id = " + pokemonId, null, null);
        Cursor abilitiesCursor = getActivity().getContentResolver().query(TinapaContentProvider.POKEDEX_POKEMON_ABILITIES_URI, null, "pokemon_id = " + pokemonId, null, null);

        loadMovesCursorAdapter(getActivity(), mMove1Spinner, movesCursor, move1Id);
        loadMovesCursorAdapter(getActivity(), mMove2Spinner, movesCursor, move2Id);
        loadMovesCursorAdapter(getActivity(), mMove3Spinner, movesCursor, move3Id);
        loadMovesCursorAdapter(getActivity(), mMove4Spinner, movesCursor, move4Id);

        String[] from = {"name"};
        int[] to = {R.id.simple_cell_name};
        CursorAdapter abilitiesCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.cell_simple_name, abilitiesCursor, from, to, 0);
        mAbilitySpinner.setAdapter(abilitiesCursorAdapter);
        if (abilityId >= 0) {
            int abilityPosition = CursorUtils.getPositionOfRowById(abilityId, mAbilitySpinner);
            if (abilityPosition >= 0) {
                mAbilitySpinner.setSelection(abilityPosition);
            }
        }
    }

    private static void loadMovesCursorAdapter(Activity activity, Spinner moveSpinner, Cursor movesCursor, long selectionId) {
        String[] from = {"name"};
        int[] to = {R.id.simple_cell_name};
        CursorAdapter spinnerAdapter = new SimpleCursorAdapter(activity, R.layout.cell_simple_name, movesCursor, from, to, 0);
        moveSpinner.setAdapter(spinnerAdapter);
        if (selectionId >= 0) {
            int selectionPosition = CursorUtils.getPositionOfRowById(selectionId, moveSpinner);
            if (selectionPosition >= 0) {
                moveSpinner.setSelection(selectionPosition);
            }
        }
    }
}
