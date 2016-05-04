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
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.tinapaproject.tinapa.R;
import com.tinapaproject.tinapa.TinapaApplication;
import com.tinapaproject.tinapa.database.key.DexKeyValues;
import com.tinapaproject.tinapa.database.key.ItemKeyValues;
import com.tinapaproject.tinapa.database.key.NatureKeyValues;
import com.tinapaproject.tinapa.database.key.PlannedKeyValues;
import com.tinapaproject.tinapa.database.provider.TinapaContentProvider;
import com.tinapaproject.tinapa.events.CreatePlannedPokemonEvent;
import com.tinapaproject.tinapa.events.DeletePlannedPokemonEvent;
import com.tinapaproject.tinapa.utils.CursorUtils;
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
                .setView(fillView(LayoutInflater.from(getActivity()), null));
        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getShowsDialog()) {
            return super.onCreateView(inflater, container, savedInstanceState);
        } else {
            return fillView(inflater, container);
        }
    }

    private View fillView(LayoutInflater inflater, ViewGroup container) {
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

        int species_id = -1;
        int ability_id = -1;
        int item_id = -1;
        int nature_id = -1;
        int move1_id = -1;
        int move2_id = -1;
        int move3_id = -1;
        int move4_id = -1;
        if (getArguments() != null) {
            String planned_id = getArguments().getString(ARG_ID);
            if (!TextUtils.isEmpty(planned_id)) {
                Cursor plannedCursor = getActivity().getContentResolver().query(TinapaContentProvider.PLANNED_POKEMON_URI, null, planned_id, null, null);
                if (plannedCursor != null && plannedCursor.moveToFirst()) {
                    species_id = plannedCursor.getInt(plannedCursor.getColumnIndex("pokemon_id"));
                    ability_id = plannedCursor.getInt(plannedCursor.getColumnIndex(PlannedKeyValues.ABILITY_ID));
                    item_id = plannedCursor.getInt(plannedCursor.getColumnIndex("item_id"));

                    move1_id = plannedCursor.getInt(plannedCursor.getColumnIndex(PlannedKeyValues.MOVE1_ID));
                    move2_id = plannedCursor.getInt(plannedCursor.getColumnIndex(PlannedKeyValues.MOVE2_ID));
                    move3_id = plannedCursor.getInt(plannedCursor.getColumnIndex(PlannedKeyValues.MOVE3_ID));
                    move4_id = plannedCursor.getInt(plannedCursor.getColumnIndex(PlannedKeyValues.MOVE4_ID));

                    nature_id = plannedCursor.getInt(plannedCursor.getColumnIndex(PlannedKeyValues.NATURE_ID));

                    mEVHP.setText(plannedCursor.getString(plannedCursor.getColumnIndex("ev_hp")));
                    mEVAtt.setText(plannedCursor.getString(plannedCursor.getColumnIndex("ev_att")));
                    mEVDef.setText(plannedCursor.getString(plannedCursor.getColumnIndex("ev_def")));
                    mEVSAtt.setText(plannedCursor.getString(plannedCursor.getColumnIndex("ev_satk")));
                    mEVSDef.setText(plannedCursor.getString(plannedCursor.getColumnIndex("ev_sdef")));
                    mEVSpd.setText(plannedCursor.getString(plannedCursor.getColumnIndex("ev_spd")));

                    IVRadioGroupUtils.setCorrectIVValue(plannedCursor.getString(plannedCursor.getColumnIndex("iv_hp")), mIVHP);
                    IVRadioGroupUtils.setCorrectIVValue(plannedCursor.getString(plannedCursor.getColumnIndex("iv_att")), mIVAtt);
                    IVRadioGroupUtils.setCorrectIVValue(plannedCursor.getString(plannedCursor.getColumnIndex("iv_def")), mIVDef);
                    IVRadioGroupUtils.setCorrectIVValue(plannedCursor.getString(plannedCursor.getColumnIndex("iv_satt")), mIVSAtt);
                    IVRadioGroupUtils.setCorrectIVValue(plannedCursor.getString(plannedCursor.getColumnIndex("iv_sdef")), mIVSDef);
                    IVRadioGroupUtils.setCorrectIVValue(plannedCursor.getString(plannedCursor.getColumnIndex("iv_spd")), mIVSpd);

                    mNote.setText(plannedCursor.getString(plannedCursor.getColumnIndex("note")));
                }
            }
        }

        PlannedPokemonUtils.setupSpeciesSpinners(getActivity(), mSpeciesSpinner, species_id);

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

        PlannedPokemonUtils.setupItemSpinner(getActivity(), mItemSpinner, item_id);

        PlannedPokemonUtils.setupNatureSpinner(getActivity(), mNatureSpinner, nature_id);

        loadAbilityAndMovesCursorAdapters(species_id, ability_id, move1_id, move2_id, move3_id, move4_id);

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
