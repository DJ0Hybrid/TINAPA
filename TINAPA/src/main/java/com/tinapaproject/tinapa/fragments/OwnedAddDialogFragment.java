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
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import com.tinapaproject.tinapa.R;

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

    private CursorAdapter mSpeciesCursorAdapter;
//    private CursorAdapter mMove1CursorAdapter;
//    private CursorAdapter mMove2CursorAdapter;
//    private CursorAdapter mMove3CursorAdapter;
//    private CursorAdapter mMove4CursorAdapter;

    public static final String TAG = "OwnedAddDialogFragment";

    public static OwnedAddDialogFragment newInstance(Activity activity, Cursor speciesCursor, String speciesColumn) {
        OwnedAddDialogFragment fragment = new OwnedAddDialogFragment();
        String[] from = {speciesColumn};
        int[] to = {R.id.simple_cell_name};
        fragment.mSpeciesCursorAdapter = new SimpleCursorAdapter(activity, R.layout.cell_simple_name, speciesCursor, from, to, 0);
        return fragment;
    }

    public OwnedAddDialogFragment() {
        // Required empty public constructor
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
                String natureId = /* TODO */ "0";
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

        mShinnySwitch.setChecked(false);

        if (savedInstanceState == null) {
            mSpeciesSpinner.setAdapter(mSpeciesCursorAdapter);
            mSpeciesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Do nothing!
                }
            });
        }
        return view;
    }

    public interface OwnedAddFragmentListener {
        public void onPositiveClicked(int level, String nickname, boolean shinny, String speciesId, String abilityId, String natureId, String genderId, String move1Id, String move2Id, String move3Id, String move4Id, int ivHP, int ivAtt, int ivDef, int ivSAtt, int ivSDef, int ivSpd, int evHP, int evAtt, int evDef, int evSAtt, int evSDef, int evSpd, String notes, String planId);

    }
}
