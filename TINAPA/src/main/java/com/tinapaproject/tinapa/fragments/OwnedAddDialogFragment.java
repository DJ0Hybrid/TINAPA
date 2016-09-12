package com.tinapaproject.tinapa.fragments;


import android.app.Activity;
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
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.tinapaproject.tinapa.R;
import com.tinapaproject.tinapa.TinapaApplication;
import com.tinapaproject.tinapa.database.key.DexKeyValues;
import com.tinapaproject.tinapa.database.key.NatureKeyValues;
import com.tinapaproject.tinapa.database.key.OwnedKeyValues;
import com.tinapaproject.tinapa.database.provider.TinapaContentProvider;
import com.tinapaproject.tinapa.events.DeleteOwnedPokemonEvent;
import com.tinapaproject.tinapa.utils.CursorUtils;

public class OwnedAddDialogFragment extends DialogFragment {

    private OwnedAddFragmentListener mListener;

    private EditText mNicknameEditText;
    private Spinner mSpeciesSpinner;
    private Switch mShinnySwitch;
    private Spinner mAbilitySpinner;
    private Spinner mMove1Spinner;
    private Spinner mMove2Spinner;
    private Spinner mMove3Spinner;
    private Spinner mMove4Spinner;
    private Spinner mNatureSpinner;
    private EditText mLevelEditText;
    private EditText mIvHpEditText;
    private EditText mIvAttEditText;
    private EditText mIvDefEditText;
    private EditText mIvSAttEditText;
    private EditText mIvSDefEditText;
    private EditText mIvSpdEditText;
    private EditText mEvHpEditText;
    private EditText mEvAttEditText;
    private EditText mEvDefEditText;
    private EditText mEvSAttEditText;
    private EditText mEvSDefEditText;
    private EditText mEvSpdEditText;
    private EditText mNotesText;
    private Button mSaveButton;

    private Bus bus;

    private static final String ARG_POKEMON_ID = "ARG_POKEMON_ID";

    public static final String TAG = "OwnedAddDialogFragment";

    public static OwnedAddDialogFragment newInstance() {
        return new OwnedAddDialogFragment();
    }

    public static OwnedAddDialogFragment newInstance(String id) {
        OwnedAddDialogFragment fragment = new OwnedAddDialogFragment();

        Bundle args = new Bundle();
        args.putString(ARG_POKEMON_ID, id);

        fragment.setArguments(args);

        return fragment;
    }

    public OwnedAddDialogFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
        bus = TinapaApplication.bus;
        bus.register(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OwnedAddFragmentListener) {
            mListener = (OwnedAddFragmentListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getArguments() != null) {
            inflater.inflate(R.menu.fragment_owned_detail, menu);
        } else {
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_owned_detail_delete:
                if (getArguments() != null) {
                    bus.post(new DeleteOwnedPokemonEvent(getArguments().getString(ARG_POKEMON_ID)));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(fillView(getActivity().getLayoutInflater(), null, savedInstanceState));
        builder.setCancelable(false);
        this.setCancelable(false);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int level = TextUtils.isEmpty(mLevelEditText.getText().toString()) ? 50 : Integer.parseInt(mLevelEditText.getText().toString());
                String nickname = mNicknameEditText.getText().toString();
                String speciesId = String.valueOf(mSpeciesSpinner.getSelectedItemId());
                boolean shinny = mShinnySwitch.isChecked();
                String abilityId = String.valueOf(mAbilitySpinner.getSelectedItemId());
                String natureId = String.valueOf(mNatureSpinner.getSelectedItemId());
                String genderId = /* TODO */ "0";
                String move1Id = String.valueOf(mMove1Spinner.getSelectedItemId());
                String move2Id = String.valueOf(mMove2Spinner.getSelectedItemId());
                String move3Id = String.valueOf(mMove3Spinner.getSelectedItemId());
                String move4Id = String.valueOf(mMove4Spinner.getSelectedItemId());
                int ivHp = TextUtils.isEmpty(mIvHpEditText.getText().toString()) ? 0 : Integer.parseInt(mIvHpEditText.getText().toString());
                int ivAtt = TextUtils.isEmpty(mIvAttEditText.getText().toString()) ? 0 : Integer.parseInt(mIvAttEditText.getText().toString());
                int ivDef = TextUtils.isEmpty(mIvDefEditText.getText().toString()) ? 0 : Integer.parseInt(mIvDefEditText.getText().toString());
                int ivSAtt = TextUtils.isEmpty(mIvSAttEditText.getText().toString()) ? 0 : Integer.parseInt(mIvSAttEditText.getText().toString());
                int ivSDef = TextUtils.isEmpty(mIvSDefEditText.getText().toString()) ? 0 : Integer.parseInt(mIvSDefEditText.getText().toString());
                int ivSpd = TextUtils.isEmpty(mIvSpdEditText.getText().toString()) ? 0 : Integer.parseInt(mIvSpdEditText.getText().toString());
                int evHp = TextUtils.isEmpty(mEvHpEditText.getText().toString()) ? 0 : Integer.parseInt(mEvHpEditText.getText().toString());
                int evAtt = TextUtils.isEmpty(mEvAttEditText.getText().toString()) ? 0 : Integer.parseInt(mEvAttEditText.getText().toString());
                int evDef = TextUtils.isEmpty(mEvDefEditText.getText().toString()) ? 0 : Integer.parseInt(mEvDefEditText.getText().toString());
                int evSAtt = TextUtils.isEmpty(mEvSAttEditText.getText().toString()) ? 0 : Integer.parseInt(mEvSAttEditText.getText().toString());
                int evSDef = TextUtils.isEmpty(mEvSDefEditText.getText().toString()) ? 0 : Integer.parseInt(mEvSDefEditText.getText().toString());
                int evSpd = TextUtils.isEmpty(mEvSpdEditText.getText().toString()) ? 0 : Integer.parseInt(mEvSpdEditText.getText().toString());
                String notes = mNotesText.getText().toString();
                String planId /* TODO */ = "";
                mListener.onPositiveClicked(level, nickname, shinny, speciesId, abilityId, natureId, genderId, move1Id, move2Id, move3Id, move4Id, ivHp, ivAtt, ivDef, ivSAtt, ivSDef, ivSpd, evHp, evAtt, evDef, evSAtt, evSDef, evSpd, notes, planId);
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (!getShowsDialog()) {
            return fillView(inflater, container, savedInstanceState);
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    private View fillView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_owned_add_dialog, container, false);
        mNicknameEditText = (EditText) view.findViewById(R.id.owned_add_nickname);
        mShinnySwitch = (Switch) view.findViewById(R.id.owned_add_shinny_switch);
        mSpeciesSpinner = (Spinner) view.findViewById(R.id.owned_add_species_spinner);
        mAbilitySpinner = (Spinner) view.findViewById(R.id.owned_add_ability_spinner);
        mMove1Spinner = (Spinner) view.findViewById(R.id.owned_add_move1_spinner);
        mMove2Spinner = (Spinner) view.findViewById(R.id.owned_add_move2_spinner);
        mMove3Spinner = (Spinner) view.findViewById(R.id.owned_add_move3_spinner);
        mMove4Spinner = (Spinner) view.findViewById(R.id.owned_add_move4_spinner);
        mNatureSpinner = (Spinner) view.findViewById(R.id.owned_add_nature_spinner);
        mLevelEditText = (EditText) view.findViewById(R.id.owned_add_level_edit_text);
        mIvHpEditText = (EditText) view.findViewById(R.id.owned_add_iv_hp_edit_text);
        mIvAttEditText = (EditText) view.findViewById(R.id.owned_add_iv_att_edit_text);
        mIvDefEditText = (EditText) view.findViewById(R.id.owned_add_iv_def_edit_text);
        mIvSAttEditText = (EditText) view.findViewById(R.id.owned_add_iv_satt_edit_text);
        mIvSDefEditText = (EditText) view.findViewById(R.id.owned_add_iv_sdef_edit_text);
        mIvSpdEditText = (EditText) view.findViewById(R.id.owned_add_iv_spd_edit_text);
        mEvHpEditText = (EditText) view.findViewById(R.id.owned_add_ev_hp_edit_text);
        mEvAttEditText = (EditText) view.findViewById(R.id.owned_add_ev_att_edit_text);
        mEvDefEditText = (EditText) view.findViewById(R.id.owned_add_ev_def_edit_text);
        mEvSAttEditText = (EditText) view.findViewById(R.id.owned_add_ev_satt_edit_text);
        mEvSDefEditText = (EditText) view.findViewById(R.id.owned_add_ev_sdef_edit_text);
        mEvSpdEditText = (EditText) view.findViewById(R.id.owned_add_ev_spd_edit_text);
        mNotesText = (EditText) view.findViewById(R.id.owned_add_notes);

        mSaveButton = (Button) view.findViewById(R.id.owned_saved_button);

        mShinnySwitch.setChecked(false);

        String ownedPokemonId = "";
        if (getArguments() != null) {
            ownedPokemonId = getArguments().getString(ARG_POKEMON_ID);
        }
        int speciesId = -1;
        int abilityId = -1;
        int move1Id = -1;
        int move2Id = -1;
        int move3Id = -1;
        int move4Id = -1;
        int natureId = -1;
        if (!TextUtils.isEmpty(ownedPokemonId)) {
            Cursor ownedCursor = getActivity().getContentResolver().query(TinapaContentProvider.OWNED_POKEMON_URI, null, "owned_pokemons.id = " + ownedPokemonId, null, null);
            if (ownedCursor.moveToFirst()) {
                speciesId = ownedCursor.getInt(ownedCursor.getColumnIndex(OwnedKeyValues.POKEMON_ID));
                abilityId = ownedCursor.getInt(ownedCursor.getColumnIndex(OwnedKeyValues.ABILITY_ID));
                natureId = ownedCursor.getInt(ownedCursor.getColumnIndex(OwnedKeyValues.NATURE_ID));

                move1Id = ownedCursor.getInt(ownedCursor.getColumnIndex(OwnedKeyValues.MOVE1_ID));
                move2Id = ownedCursor.getInt(ownedCursor.getColumnIndex(OwnedKeyValues.MOVE2_ID));
                move3Id = ownedCursor.getInt(ownedCursor.getColumnIndex(OwnedKeyValues.MOVE3_ID));
                move4Id = ownedCursor.getInt(ownedCursor.getColumnIndex(OwnedKeyValues.MOVE4_ID));

                mNicknameEditText.setText(ownedCursor.getString(ownedCursor.getColumnIndex(OwnedKeyValues.NICKNAME)));
                mLevelEditText.setText(ownedCursor.getString(ownedCursor.getColumnIndex(OwnedKeyValues.LEVEL)));

                mShinnySwitch.setChecked(ownedCursor.getInt(ownedCursor.getColumnIndex(OwnedKeyValues.SHINNY)) != 0);

                mEvHpEditText.setText(ownedCursor.getString(ownedCursor.getColumnIndex(OwnedKeyValues.EV_HP)));
                mEvAttEditText.setText(ownedCursor.getString(ownedCursor.getColumnIndex(OwnedKeyValues.EV_ATT)));
                mEvDefEditText.setText(ownedCursor.getString(ownedCursor.getColumnIndex(OwnedKeyValues.EV_DEF)));
                mEvSAttEditText.setText(ownedCursor.getString(ownedCursor.getColumnIndex(OwnedKeyValues.EV_SATT)));
                mEvSDefEditText.setText(ownedCursor.getString(ownedCursor.getColumnIndex(OwnedKeyValues.EV_SDEF)));
                mEvSpdEditText.setText(ownedCursor.getString(ownedCursor.getColumnIndex(OwnedKeyValues.EV_SPD)));

                mIvHpEditText.setText(ownedCursor.getString(ownedCursor.getColumnIndex(OwnedKeyValues.IV_HP)));
                mIvAttEditText.setText(ownedCursor.getString(ownedCursor.getColumnIndex(OwnedKeyValues.IV_ATT)));
                mIvDefEditText.setText(ownedCursor.getString(ownedCursor.getColumnIndex(OwnedKeyValues.IV_DEF)));
                mIvSAttEditText.setText(ownedCursor.getString(ownedCursor.getColumnIndex(OwnedKeyValues.IV_SATT)));
                mIvSDefEditText.setText(ownedCursor.getString(ownedCursor.getColumnIndex(OwnedKeyValues.IV_SDEF)));
                mIvSpdEditText.setText(ownedCursor.getString(ownedCursor.getColumnIndex(OwnedKeyValues.IV_SPD)));

                mNotesText.setText(ownedCursor.getString(ownedCursor.getColumnIndex(OwnedKeyValues.NOTE)));
            }
        }

        Cursor speciesCursor = getActivity().getContentResolver().query(TinapaContentProvider.POKEDEX_ALL_SHORT_URI, null, null, null, null);
        String[] from = {DexKeyValues.name};
        int[] to = {R.id.simple_cell_name};
        CursorAdapter mSpeciesCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.cell_simple_name, speciesCursor, from, to, 0);
        mSpeciesCursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return getActivity().getContentResolver().query(TinapaContentProvider.POKEDEX_ALL_SHORT_URI, null, String.valueOf(constraint), null, null);
            }
        });
        mSpeciesSpinner.setAdapter(mSpeciesCursorAdapter);
        if (speciesId >= 0) {
            mSpeciesSpinner.setSelection(speciesId - 1, false);  // ID from SQLite starts with 1, the spinner starts with 0.
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
        if (natureId >= 0) {
            mNatureSpinner.setSelection(natureId - 1, false);
        }

        loadAbilityAndMovesCursorAdapters(speciesId, abilityId, move1Id, move2Id, move3Id, move4Id);

        if (getShowsDialog()) {
            mSaveButton.setVisibility(View.GONE);
        } else {
            mSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int level = TextUtils.isEmpty(mLevelEditText.getText().toString()) ? 50 : Integer.parseInt(mLevelEditText.getText().toString());
                    String nickname = mNicknameEditText.getText().toString();
                    String speciesId = String.valueOf(mSpeciesSpinner.getSelectedItemId());
                    boolean shinny = mShinnySwitch.isChecked();
                    String abilityId = String.valueOf(mAbilitySpinner.getSelectedItemId());
                    String natureId = String.valueOf(mNatureSpinner.getSelectedItemId());
                    String genderId = /* TODO */ "0";
                    String move1Id = String.valueOf(mMove1Spinner.getSelectedItemId());
                    String move2Id = String.valueOf(mMove2Spinner.getSelectedItemId());
                    String move3Id = String.valueOf(mMove3Spinner.getSelectedItemId());
                    String move4Id = String.valueOf(mMove4Spinner.getSelectedItemId());
                    int ivHp = TextUtils.isEmpty(mIvHpEditText.getText().toString()) ? 0 : Integer.parseInt(mIvHpEditText.getText().toString());
                    int ivAtt = TextUtils.isEmpty(mIvAttEditText.getText().toString()) ? 0 : Integer.parseInt(mIvAttEditText.getText().toString());
                    int ivDef = TextUtils.isEmpty(mIvDefEditText.getText().toString()) ? 0 : Integer.parseInt(mIvDefEditText.getText().toString());
                    int ivSAtt = TextUtils.isEmpty(mIvSAttEditText.getText().toString()) ? 0 : Integer.parseInt(mIvSAttEditText.getText().toString());
                    int ivSDef = TextUtils.isEmpty(mIvSDefEditText.getText().toString()) ? 0 : Integer.parseInt(mIvSDefEditText.getText().toString());
                    int ivSpd = TextUtils.isEmpty(mIvSpdEditText.getText().toString()) ? 0 : Integer.parseInt(mIvSpdEditText.getText().toString());
                    int evHp = TextUtils.isEmpty(mEvHpEditText.getText().toString()) ? 0 : Integer.parseInt(mEvHpEditText.getText().toString());
                    int evAtt = TextUtils.isEmpty(mEvAttEditText.getText().toString()) ? 0 : Integer.parseInt(mEvAttEditText.getText().toString());
                    int evDef = TextUtils.isEmpty(mEvDefEditText.getText().toString()) ? 0 : Integer.parseInt(mEvDefEditText.getText().toString());
                    int evSAtt = TextUtils.isEmpty(mEvSAttEditText.getText().toString()) ? 0 : Integer.parseInt(mEvSAttEditText.getText().toString());
                    int evSDef = TextUtils.isEmpty(mEvSDefEditText.getText().toString()) ? 0 : Integer.parseInt(mEvSDefEditText.getText().toString());
                    int evSpd = TextUtils.isEmpty(mEvSpdEditText.getText().toString()) ? 0 : Integer.parseInt(mEvSpdEditText.getText().toString());
                    String notes = mNotesText.getText().toString();
                    String planId /* TODO */ = "";

                    mListener.onUpdateClicked(getArguments().getString(ARG_POKEMON_ID, ""), level, nickname, shinny, speciesId, abilityId, natureId, genderId, move1Id, move2Id, move3Id, move4Id, ivHp, ivAtt, ivDef, ivSAtt, ivSDef, ivSpd, evHp, evAtt, evDef, evSAtt, evSDef, evSpd, notes, planId);
                }
            });
        }

        return view;
    }

    private void loadAbilityAndMovesCursorAdapters(long pokemonId, long abilityId, long move1Id, long move2Id, long move3Id, long move4Id) {
        // Load up all the other spinners.
        // It is assumed that this will go off automatically.
        Cursor movesCursor = getActivity().getContentResolver().query(TinapaContentProvider.POKEDEX_POKEMON_MOVES_URI, null, String.valueOf(pokemonId), null, null);
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

    public interface OwnedAddFragmentListener {
        public void onPositiveClicked(int level, String nickname, boolean shinny, String speciesId, String abilityId, String natureId, String genderId, String move1Id, String move2Id, String move3Id, String move4Id, int ivHP, int ivAtt, int ivDef, int ivSAtt, int ivSDef, int ivSpd, int evHP, int evAtt, int evDef, int evSAtt, int evSDef, int evSpd, String notes, String planId);

        public void onUpdateClicked(String ownedId, int level, String nickname, boolean shinny, String speciesId, String abilityId, String natureId, String genderId, String move1Id, String move2Id, String move3Id, String move4Id, int ivHP, int ivAtt, int ivDef, int ivSAtt, int ivSDef, int ivSpd, int evHP, int evAtt, int evDef, int evSAtt, int evSDef, int evSpd, String notes, String planId);
    }
}
