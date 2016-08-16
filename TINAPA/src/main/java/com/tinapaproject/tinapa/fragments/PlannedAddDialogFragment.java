package com.tinapaproject.tinapa.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.squareup.otto.Bus;
import com.tinapaproject.tinapa.R;
import com.tinapaproject.tinapa.TinapaApplication;
import com.tinapaproject.tinapa.database.key.PlannedKeyValues;
import com.tinapaproject.tinapa.database.provider.TinapaContentProvider;
import com.tinapaproject.tinapa.events.CreatePlannedPokemonEvent;
import com.tinapaproject.tinapa.events.DeletePlannedPokemonEvent;
import com.tinapaproject.tinapa.utils.IVRadioGroupUtils;
import com.tinapaproject.tinapa.utils.PlannedPokemonUtils;

public class PlannedAddDialogFragment extends DialogFragment {

    private Spinner mSpeciesSpinner;
    private Spinner mAbilitySpinner;
    private Spinner mMove1Spinner;
    private Spinner mMove2Spinner;
    private Spinner mMove3Spinner;
    private Spinner mMove4Spinner;
    private Spinner mNatureSpinner;
    private Spinner mItemSpinner;
    private EditText mEVHP;
    private EditText mEVAtt;
    private EditText mEVDef;
    private EditText mEVSAtt;
    private EditText mEVSDef;
    private EditText mEVSpd;
    private RadioGroup mIVHP;
    private RadioGroup mIVAtt;
    private RadioGroup mIVDef;
    private RadioGroup mIVSAtt;
    private RadioGroup mIVSDef;
    private RadioGroup mIVSpd;
    private EditText mNote;

    private Bus bus;

    public static final String ARG_ID = "ARG_ID";

    private static final String SPECIES_ID = "SPECIES_ID";
    private static final String ABILITY_ID = "ABILITY_ID";
    private static final String MOVE_IDS = "MOVE_IDS";
    private static final String NATURE_ID = "NATURE_ID";
    private static final String ITEM_ID = "ITEM_ID";
    private static final String EV_VALUES = "EV_VALUES";
    private static final String IV_VALUES = "IV_VALUES";
    private static final String NOTE_TEXT = "NOTE_TEXT";

    public static final String TAG = "PlannedAddDialogFragment";

    public static PlannedAddDialogFragment newInstance() {
        PlannedAddDialogFragment fragment = new PlannedAddDialogFragment();

        return fragment;
    }

    public static PlannedAddDialogFragment newInstance(String id) {
        PlannedAddDialogFragment fragment = new PlannedAddDialogFragment();

        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        fragment.setArguments(args);

        return fragment;
    }

    public PlannedAddDialogFragment() {
        // Required empty public constructor
        bus = TinapaApplication.bus;
        bus.register(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getArguments() != null) {
            inflater.inflate(R.menu.fragment_planned_detail, menu);
        } else {
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_planned_detail_delete:
                if (getArguments() != null) {
                    bus.post(new DeletePlannedPokemonEvent(getArguments().getString(ARG_ID)));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        this.setCancelable(false);
        builder.setCancelable(false)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        savePokemon(true);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setView(fillView(LayoutInflater.from(getActivity()), null, savedInstanceState));
        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getShowsDialog()) {
            return super.onCreateView(inflater, container, savedInstanceState);
        } else {
            return fillView(inflater, container, savedInstanceState);
        }
    }

    private View fillView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_planned_add_dialog, container, false);
        mSpeciesSpinner = (Spinner) view.findViewById(R.id.planned_add_species_spinner);
        mAbilitySpinner = (Spinner) view.findViewById(R.id.planned_add_ability_spinner);
        mMove1Spinner = (Spinner) view.findViewById(R.id.planned_add_move1_spinner);
        mMove2Spinner = (Spinner) view.findViewById(R.id.planned_add_move2_spinner);
        mMove3Spinner = (Spinner) view.findViewById(R.id.planned_add_move3_spinner);
        mMove4Spinner = (Spinner) view.findViewById(R.id.planned_add_move4_spinner);
        mNatureSpinner = (Spinner) view.findViewById(R.id.planned_add_nature_spinner);
        mItemSpinner = (Spinner) view.findViewById(R.id.planned_add_item_spinner);
        mEVHP = (EditText) view.findViewById(R.id.planned_add_ev_hp_edit_text);
        mEVAtt = (EditText) view.findViewById(R.id.planned_add_ev_att_edit_text);
        mEVDef = (EditText) view.findViewById(R.id.planned_add_ev_def_edit_text);
        mEVSAtt = (EditText) view.findViewById(R.id.planned_add_ev_satt_edit_text);
        mEVSDef = (EditText) view.findViewById(R.id.planned_add_ev_sdef_edit_text);
        mEVSpd = (EditText) view.findViewById(R.id.planned_add_ev_spd_edit_text);
        mIVHP = (RadioGroup) view.findViewById(R.id.planned_add_iv_hp_group);
        mIVAtt = (RadioGroup) view.findViewById(R.id.planned_add_iv_att_group);
        mIVDef = (RadioGroup) view.findViewById(R.id.planned_add_iv_def_group);
        mIVSAtt = (RadioGroup) view.findViewById(R.id.planned_add_iv_satt_group);
        mIVSDef = (RadioGroup) view.findViewById(R.id.planned_add_iv_sdef_group);
        mIVSpd = (RadioGroup) view.findViewById(R.id.planned_add_iv_spd_group);
        mNote = (EditText) view.findViewById(R.id.planned_add_notes);
        Button saveButton = (Button) view.findViewById(R.id.planned_saved_button);

        int speciesId = -1;
        int abilityId = -1;
        int itemId = -1;
        int natureId = -1;
        int move1Id = -1;
        int move2Id = -1;
        int move3Id = -1;
        int move4Id = -1;
        String evHP = "";
        String evAtt = "";
        String evDef = "";
        String evSAtt = "";
        String evSDef = "";
        String evSpd = "";
        String ivHP = "";
        String ivAtt = "";
        String ivDef = "";
        String ivSAtt = "";
        String ivSDef = "";
        String ivSpd = "";
        String note = "";

        if (savedInstanceState != null) {
            speciesId = savedInstanceState.getInt(SPECIES_ID);
            abilityId = savedInstanceState.getInt(ABILITY_ID);
            itemId = savedInstanceState.getInt(ITEM_ID);
            natureId = savedInstanceState.getInt(NATURE_ID);
            int[] moveIds = savedInstanceState.getIntArray(MOVE_IDS);
            move1Id = moveIds[0];
            move2Id = moveIds[1];
            move3Id = moveIds[2];
            move4Id = moveIds[3];
            String[] evValues = savedInstanceState.getStringArray(EV_VALUES);
            evHP = evValues[0];
            evAtt = evValues[1];
            evDef = evValues[2];
            evSAtt = evValues[3];
            evSDef = evValues[4];
            evSpd = evValues[5];
            String[] ivValues = savedInstanceState.getStringArray(IV_VALUES);
            ivHP = ivValues[0];
            ivAtt = ivValues[1];
            ivDef = ivValues[2];
            ivSAtt = ivValues[3];
            ivSDef = ivValues[4];
            ivSpd = ivValues[5];
            note = savedInstanceState.getString(NOTE_TEXT);
        } else if (getArguments() != null) {
            String planned_id = getArguments().getString(ARG_ID);
            if (!TextUtils.isEmpty(planned_id)) {
                Cursor plannedCursor = getActivity().getContentResolver().query(TinapaContentProvider.PLANNED_POKEMON_URI, null, planned_id, null, null);
                if (plannedCursor != null && plannedCursor.moveToFirst()) {
                    speciesId = plannedCursor.getInt(plannedCursor.getColumnIndex("pokemon_id"));
                    abilityId = plannedCursor.getInt(plannedCursor.getColumnIndex(PlannedKeyValues.ABILITY_ID));
                    itemId = plannedCursor.getInt(plannedCursor.getColumnIndex("item_id"));

                    move1Id = plannedCursor.getInt(plannedCursor.getColumnIndex(PlannedKeyValues.MOVE1_ID));
                    move2Id = plannedCursor.getInt(plannedCursor.getColumnIndex(PlannedKeyValues.MOVE2_ID));
                    move3Id = plannedCursor.getInt(plannedCursor.getColumnIndex(PlannedKeyValues.MOVE3_ID));
                    move4Id = plannedCursor.getInt(plannedCursor.getColumnIndex(PlannedKeyValues.MOVE4_ID));

                    natureId = plannedCursor.getInt(plannedCursor.getColumnIndex(PlannedKeyValues.NATURE_ID));

                    evHP = plannedCursor.getString(plannedCursor.getColumnIndex("ev_hp"));
                    evAtt = plannedCursor.getString(plannedCursor.getColumnIndex("ev_att"));
                    evDef = plannedCursor.getString(plannedCursor.getColumnIndex("ev_def"));
                    evSAtt = plannedCursor.getString(plannedCursor.getColumnIndex("ev_satk"));
                    evSDef = plannedCursor.getString(plannedCursor.getColumnIndex("ev_sdef"));
                    evSpd = plannedCursor.getString(plannedCursor.getColumnIndex("ev_spd"));

                    ivHP = plannedCursor.getString(plannedCursor.getColumnIndex("iv_hp"));
                    ivAtt = plannedCursor.getString(plannedCursor.getColumnIndex("iv_att"));
                    ivDef = plannedCursor.getString(plannedCursor.getColumnIndex("iv_def"));
                    ivSAtt = plannedCursor.getString(plannedCursor.getColumnIndex("iv_satt"));
                    ivSDef = plannedCursor.getString(plannedCursor.getColumnIndex("iv_sdef"));
                    ivSpd = plannedCursor.getString(plannedCursor.getColumnIndex("iv_spd"));

                    mNote.setText(plannedCursor.getString(plannedCursor.getColumnIndex("note")));
                }
            }
        }

        PlannedPokemonUtils.setupSpeciesSpinners(getActivity(), mSpeciesSpinner, speciesId);

        mSpeciesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadAbilityAndMovesCursorAdapters((int) id, -1, -1, -1, -1, -1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing!
            }
        });

        PlannedPokemonUtils.setupItemSpinner(getActivity(), mItemSpinner, itemId);

        PlannedPokemonUtils.setupNatureSpinner(getActivity(), mNatureSpinner, natureId);

        loadAbilityAndMovesCursorAdapters(speciesId, abilityId, move1Id, move2Id, move3Id, move4Id);

        mEVHP.setText(evHP);
        mEVAtt.setText(evAtt);
        mEVDef.setText(evDef);
        mEVSAtt.setText(evSAtt);
        mEVSDef.setText(evSDef);
        mEVSpd.setText(evSpd);

        IVRadioGroupUtils.setCorrectIVValue(ivHP, mIVHP);
        IVRadioGroupUtils.setCorrectIVValue(ivAtt, mIVAtt);
        IVRadioGroupUtils.setCorrectIVValue(ivDef, mIVDef);
        IVRadioGroupUtils.setCorrectIVValue(ivSAtt, mIVSAtt);
        IVRadioGroupUtils.setCorrectIVValue(ivSDef, mIVSDef);
        IVRadioGroupUtils.setCorrectIVValue(ivSpd, mIVSpd);

        mNote.setText(note);

        if (getShowsDialog()) {
            saveButton.setVisibility(View.GONE);
        } else {
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    savePokemon(false);
                }
            });
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SPECIES_ID, (int) mSpeciesSpinner.getSelectedItemId());
        outState.putInt(ABILITY_ID, (int) mAbilitySpinner.getSelectedItemId());
        outState.putInt(ITEM_ID, (int) mItemSpinner.getSelectedItemId());
        outState.putInt(NATURE_ID, (int) mNatureSpinner.getSelectedItemId());
        int[] movesId = {(int) mMove1Spinner.getSelectedItemId(), (int) mMove2Spinner.getSelectedItemId(), (int) mMove3Spinner.getSelectedItemId(), (int) mMove4Spinner.getSelectedItemId()};
        outState.putIntArray(MOVE_IDS, movesId);
        String[] evValues = {mEVHP.getText().toString(), mEVAtt.getText().toString(), mEVDef.getText().toString(), mEVSAtt.getText().toString(), mEVSDef.getText().toString(), mEVSpd.getText().toString()};
        outState.putStringArray(EV_VALUES, evValues);
        String[] ivValues = {IVRadioGroupUtils.getIVValueSelected(mIVHP, mIVHP.getContext()),
                IVRadioGroupUtils.getIVValueSelected(mIVAtt, mIVAtt.getContext()),
                IVRadioGroupUtils.getIVValueSelected(mIVDef, mIVDef.getContext()),
                IVRadioGroupUtils.getIVValueSelected(mIVSAtt, mIVSAtt.getContext()),
                IVRadioGroupUtils.getIVValueSelected(mIVSDef, mIVSDef.getContext()),
                IVRadioGroupUtils.getIVValueSelected(mIVSpd, mIVSpd.getContext())};
        outState.putStringArray(IV_VALUES, ivValues);
        outState.putString(NOTE_TEXT, mNote.getText().toString());
    }

    private void savePokemon(final boolean isNewSave) {
        String plannedId = null;
        if (getArguments() != null) {
            plannedId = getArguments().getString(ARG_ID);
        }
        String speciesId = String.valueOf(mSpeciesSpinner.getSelectedItemId());
        String abilityId = String.valueOf(mAbilitySpinner.getSelectedItemId());
        String itemId = String.valueOf(mItemSpinner.getSelectedItemId());
        String move1Id = String.valueOf(mMove1Spinner.getSelectedItemId());
        String move2Id = String.valueOf(mMove2Spinner.getSelectedItemId());
        String move3Id = String.valueOf(mMove3Spinner.getSelectedItemId());
        String move4Id = String.valueOf(mMove4Spinner.getSelectedItemId());
        String natureId = String.valueOf(mNatureSpinner.getSelectedItemId());
        String evHP = mEVHP.getText().toString();
        String evAtt = mEVAtt.getText().toString();
        String evDef = mEVDef.getText().toString();
        String evSAtt = mEVSAtt.getText().toString();
        String evSDef = mEVSDef.getText().toString();
        String evSpd = mEVSpd.getText().toString();
        String ivHP = IVRadioGroupUtils.getIVValueSelected(mIVHP, getActivity());
        String ivAtt = IVRadioGroupUtils.getIVValueSelected(mIVAtt, getActivity());
        String ivDef = IVRadioGroupUtils.getIVValueSelected(mIVDef, getActivity());
        String ivSAtt = IVRadioGroupUtils.getIVValueSelected(mIVSAtt, getActivity());
        String ivSDef = IVRadioGroupUtils.getIVValueSelected(mIVSDef, getActivity());
        String ivSpd = IVRadioGroupUtils.getIVValueSelected(mIVSpd, getActivity());
        String notes = mNote.getText().toString();
        bus.post(new CreatePlannedPokemonEvent(plannedId, speciesId, abilityId, move1Id, move2Id, move3Id, move4Id, natureId, itemId, evHP, evAtt, evDef, evSAtt, evSDef, evSpd, ivHP, ivAtt, ivDef, ivSAtt, ivSDef, ivSpd, notes, isNewSave));
    }

    private void loadAbilityAndMovesCursorAdapters(int pokemonId, int abilityId, int move1Id, int move2Id, int move3Id, int move4Id) {
        PlannedPokemonUtils.setupAbilityForPokemon(getActivity(), mAbilitySpinner, pokemonId, abilityId);
        PlannedPokemonUtils.setupAllMovesForPokemon(getActivity(), mMove1Spinner, mMove2Spinner, mMove3Spinner, mMove4Spinner, pokemonId, move1Id, move2Id, move3Id, move4Id);
    }
}
