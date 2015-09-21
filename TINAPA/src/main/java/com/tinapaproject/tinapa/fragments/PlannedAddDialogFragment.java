package com.tinapaproject.tinapa.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
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
import com.tinapaproject.tinapa.database.provider.TinapaContentProvider;
import com.tinapaproject.tinapa.events.CreatePlannedPokemonEvent;
import com.tinapaproject.tinapa.utils.CursorUtils;

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
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        this.setCancelable(false);
        builder.setCancelable(false)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                        String ivHP = getIVValueSelected(mIVHP, getActivity());
                        String ivAtt = getIVValueSelected(mIVAtt, getActivity());
                        String ivDef = getIVValueSelected(mIVDef, getActivity());
                        String ivSAtt = getIVValueSelected(mIVSAtt, getActivity());
                        String ivSDef = getIVValueSelected(mIVSDef, getActivity());
                        String ivSpd = getIVValueSelected(mIVSpd, getActivity());
                        String notes = mNote.getText().toString();
                        bus.post(new CreatePlannedPokemonEvent(speciesId, abilityId, move1Id, move2Id, move3Id, move4Id, natureId, itemId, evHP, evAtt, evDef, evSAtt, evSDef, evSpd, ivHP, ivAtt, ivDef, ivSAtt, ivSDef, ivSpd, notes));
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

                    mEVHP.setText(plannedCursor.getString(plannedCursor.getColumnIndex("ev_hp")));
                    mEVAtt.setText(plannedCursor.getString(plannedCursor.getColumnIndex("ev_att")));
                    mEVDef.setText(plannedCursor.getString(plannedCursor.getColumnIndex("ev_def")));
                    mEVSAtt.setText(plannedCursor.getString(plannedCursor.getColumnIndex("ev_satk")));
                    mEVSDef.setText(plannedCursor.getString(plannedCursor.getColumnIndex("ev_sdef")));
                    mEVSpd.setText(plannedCursor.getString(plannedCursor.getColumnIndex("ev_spd")));

                    // TODO Still need to fill in the rest of the stuff.

                    mNote.setText(plannedCursor.getString(plannedCursor.getColumnIndex("note")));
                }
            }
        }

        Cursor speciesCursor = getActivity().getContentResolver().query(TinapaContentProvider.POKEDEX_ALL_SHORT_URI, null, null, null, null);
        String[] from = {DexKeyValues.name};
        int[] to = {R.id.simple_cell_name};
        CursorAdapter mSpeciesCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.cell_simple_name, speciesCursor, from, to, 0);
        mSpeciesSpinner.setAdapter(mSpeciesCursorAdapter);
        if (species_id > 0) {
            mSpeciesSpinner.setSelection(species_id -1, false);
        }

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

        Cursor natureCursor = getActivity().getContentResolver().query(TinapaContentProvider.NATURE_URI, null, null, null, null);
        String[] natureFrom = {NatureKeyValues.NATURE_NAME};
        int[] natureTo = {R.id.simple_cell_name};
        CursorAdapter mNatureCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.cell_simple_name, natureCursor, natureFrom, natureTo, 0) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.cell_simple_name, parent, false);
                }
                this.getCursor().moveToPosition(position);
                String natureName = this.getCursor().getString(this.getCursor().getColumnIndex(NatureKeyValues.NATURE_NAME));
                String increasedStatName = this.getCursor().getString(this.getCursor().getColumnIndex(NatureKeyValues.INCREASED_STAT_NAME));
                String decreasedStatName = this.getCursor().getString(this.getCursor().getColumnIndex(NatureKeyValues.DECREASED_STAT_NAME));

                StringBuilder builder = new StringBuilder(natureName);

                if (!TextUtils.isEmpty(increasedStatName) && !TextUtils.isEmpty(decreasedStatName) && !increasedStatName.equalsIgnoreCase(decreasedStatName)) {
                    builder.append(" / + " + increasedStatName);
                    builder.append(" / - " + decreasedStatName);
                } else {
                    builder.append(" / = / =");
                }

                TextView textView = (TextView) convertView.findViewById(R.id.simple_cell_name);
                textView.setText(builder.toString());

                return convertView;
            }
        };
        mNatureSpinner.setAdapter(mNatureCursorAdapter);

        if (getShowsDialog()) {
            saveButton.setVisibility(View.GONE);
        } else {
            // TODO: Set save changes button's on click listener.
        }

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

    private static String getIVValueSelected(RadioGroup radioGroup, Context context) {
        RadioButton selectedButton = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        if (selectedButton.getText().equals(context.getString(R.string.planned_iv_min))) {
            return "0";
        } else if (selectedButton.getText().equals(context.getString(R.string.planned_iv_max))) {
            return "31";
        } else {
            return "-1";
        }
    }
}
