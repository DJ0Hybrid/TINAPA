package com.tinapaproject.tinapa.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.squareup.otto.Bus;
import com.tinapaproject.tinapa.R;
import com.tinapaproject.tinapa.TinapaApplication;
import com.tinapaproject.tinapa.database.key.TeamKeyValues;
import com.tinapaproject.tinapa.database.provider.TinapaContentProvider;
import com.tinapaproject.tinapa.events.SaveTeamEvent;
import com.tinapaproject.tinapa.utils.IVRadioGroupUtils;
import com.tinapaproject.tinapa.utils.PlannedPokemonUtils;

public class TeamAddDialogFragment extends DialogFragment {
    private Bus bus = TinapaApplication.bus;

    View pokemonSection1;
    View pokemonSection2;
    View pokemonSection3;
    View pokemonSection4;
    View pokemonSection5;
    View pokemonSection6;

    String pokemon1Id = "";
    String pokemon2Id = "";
    String pokemon3Id = "";
    String pokemon4Id = "";
    String pokemon5Id = "";
    String pokemon6Id = "";

    private static final String TEAM_ID_ARG = "TEAM_ID_ARG";

    private static final String SPECIES_ID_LIST_KEY = "SPECIES_ID_LIST_KEY";
    private static final String ABILITY_ID_LIST_KEY = "ABILITY_ID_LIST_KEY";
    private static final String NATURE_ID_LIST_KEY = "NATURE_ID_LIST_KEY";
    private static final String MOVE1_ID_LIST_KEY = "MOVE1_ID_LIST_KEY";
    private static final String MOVE2_ID_LIST_KEY = "MOVE2_ID_LIST_KEY";
    private static final String MOVE3_ID_LIST_KEY = "MOVE3_ID_LIST_KEY";
    private static final String MOVE4_ID_LIST_KEY = "MOVE4_ID_LIST_KEY";
    private static final String ITEM_ID_LIST_KEY = "ITEM_ID_LIST_KEY";
    private static final String IV_HP_LIST_KEY = "IV_HP_LIST_KEY";
    private static final String IV_ATT_LIST_KEY = "IV_ATT_LIST_KEY";
    private static final String IV_DEF_LIST_KEY = "IV_DEF_LIST_KEY";
    private static final String IV_SATT_LIST_KEY = "IV_SATT_LIST_KEY";
    private static final String IV_SDEF_LIST_KEY = "IV_SDEF_LIST_KEY";
    private static final String IV_SPD_LIST_KEY = "IV_SPD_LIST_KEY";
    private static final String EV_HP_LIST_KEY = "EV_HP_LIST_KEY";
    private static final String EV_ATT_LIST_KEY = "EV_ATT_LIST_KEY";
    private static final String EV_DEF_LIST_KEY = "EV_DEF_LIST_KEY";
    private static final String EV_SATT_LIST_KEY = "EV_SATT_LIST_KEY";
    private static final String EV_SDEF_LIST_KEY = "EV_SDEF_LIST_KEY";
    private static final String EV_SPD_LIST_KEY = "EV_SPD_LIST_KEY";
    private static final String NOTES_LIST_KEY = "NOTES_LIST_KEY";


    public static final String TAG = "TeamAddDialogFragment";

    public TeamAddDialogFragment() {
        // Required empty constructor.
    }

    public static TeamAddDialogFragment newInstance(String teamId) {
        TeamAddDialogFragment fragment = new TeamAddDialogFragment();
        Bundle args = new Bundle();
        args.putString(TEAM_ID_ARG, teamId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(fillView(getActivity().getLayoutInflater(), null, savedInstanceState));
        builder.setCancelable(false);
        this.setCancelable(false);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bus.post(generateSaveTeamEvent());
            }
        });

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
        View view = inflater.inflate(R.layout.fragment_team_add_dialog, container, false);

        View pokemonBanner1 = view.findViewById(R.id.team_member_click_banner1);
        View pokemonBanner2 = view.findViewById(R.id.team_member_click_banner2);
        View pokemonBanner3 = view.findViewById(R.id.team_member_click_banner3);
        View pokemonBanner4 = view.findViewById(R.id.team_member_click_banner4);
        View pokemonBanner5 = view.findViewById(R.id.team_member_click_banner5);
        View pokemonBanner6 = view.findViewById(R.id.team_member_click_banner6);

        pokemonSection1 = view.findViewById(R.id.team_member_section1);
        pokemonSection2 = view.findViewById(R.id.team_member_section2);
        pokemonSection3 = view.findViewById(R.id.team_member_section3);
        pokemonSection4 = view.findViewById(R.id.team_member_section4);
        pokemonSection5 = view.findViewById(R.id.team_member_section5);
        pokemonSection6 = view.findViewById(R.id.team_member_section6);

        setBannerToggleClickListener(pokemonBanner1, pokemonSection1);
        setBannerToggleClickListener(pokemonBanner2, pokemonSection2);
        setBannerToggleClickListener(pokemonBanner3, pokemonSection3);
        setBannerToggleClickListener(pokemonBanner4, pokemonSection4);
        setBannerToggleClickListener(pokemonBanner5, pokemonSection5);
        setBannerToggleClickListener(pokemonBanner6, pokemonSection6);

        pokemonSection1.findViewById(R.id.planned_saved_button).setVisibility(View.GONE);
        pokemonSection2.findViewById(R.id.planned_saved_button).setVisibility(View.GONE);
        pokemonSection3.findViewById(R.id.planned_saved_button).setVisibility(View.GONE);
        pokemonSection4.findViewById(R.id.planned_saved_button).setVisibility(View.GONE);
        pokemonSection5.findViewById(R.id.planned_saved_button).setVisibility(View.GONE);
        pokemonSection6.findViewById(R.id.planned_saved_button).setVisibility(View.GONE);

        int pokemon1SpeciesId = -1;
        int pokemon1NatureId = -1;
        int pokemon1AbilityId = -1;
        int pokemon1ItemId = -1;
        int pokemon1Move1Id = -1;
        int pokemon1Move2Id = -1;
        int pokemon1Move3Id = -1;
        int pokemon1Move4Id = -1;
        int pokemon1IvHp = -1;
        int pokemon1IvAtt = -1;
        int pokemon1IvDef = -1;
        int pokemon1IvSAtt = -1;
        int pokemon1IvSDef = -1;
        int pokemon1IvSpd = -1;
        int pokemon1EvHp = -1;
        int pokemon1EvAtt = -1;
        int pokemon1EvDef = -1;
        int pokemon1EvSAtt = -1;
        int pokemon1EvSDef = -1;
        int pokemon1EvSpd = -1;
        String pokemon1Note = "";

        int pokemon2SpeciesId = -1;
        int pokemon2NatureId = -1;
        int pokemon2AbilityId = -1;
        int pokemon2ItemId = -1;
        int pokemon2Move1Id = -1;
        int pokemon2Move2Id = -1;
        int pokemon2Move3Id = -1;
        int pokemon2Move4Id = -1;
        int pokemon2IvHp = -1;
        int pokemon2IvAtt = -1;
        int pokemon2IvDef = -1;
        int pokemon2IvSAtt = -1;
        int pokemon2IvSDef = -1;
        int pokemon2IvSpd = -1;
        int pokemon2EvHp = -1;
        int pokemon2EvAtt = -1;
        int pokemon2EvDef = -1;
        int pokemon2EvSAtt = -1;
        int pokemon2EvSDef = -1;
        int pokemon2EvSpd = -1;
        String pokemon2Note = "";

        int pokemon3SpeciesId = -1;
        int pokemon3NatureId = -1;
        int pokemon3AbilityId = -1;
        int pokemon3ItemId = -1;
        int pokemon3Move1Id = -1;
        int pokemon3Move2Id = -1;
        int pokemon3Move3Id = -1;
        int pokemon3Move4Id = -1;
        int pokemon3IvHp = -1;
        int pokemon3IvAtt = -1;
        int pokemon3IvDef = -1;
        int pokemon3IvSAtt = -1;
        int pokemon3IvSDef = -1;
        int pokemon3IvSpd = -1;
        int pokemon3EvHp = -1;
        int pokemon3EvAtt = -1;
        int pokemon3EvDef = -1;
        int pokemon3EvSAtt = -1;
        int pokemon3EvSDef = -1;
        int pokemon3EvSpd = -1;
        String pokemon3Note = "";

        int pokemon4SpeciesId = -1;
        int pokemon4NatureId = -1;
        int pokemon4AbilityId = -1;
        int pokemon4ItemId = -1;
        int pokemon4Move1Id = -1;
        int pokemon4Move2Id = -1;
        int pokemon4Move3Id = -1;
        int pokemon4Move4Id = -1;
        int pokemon4IvHp = -1;
        int pokemon4IvAtt = -1;
        int pokemon4IvDef = -1;
        int pokemon4IvSAtt = -1;
        int pokemon4IvSDef = -1;
        int pokemon4IvSpd = -1;
        int pokemon4EvHp = -1;
        int pokemon4EvAtt = -1;
        int pokemon4EvDef = -1;
        int pokemon4EvSAtt = -1;
        int pokemon4EvSDef = -1;
        int pokemon4EvSpd = -1;
        String pokemon4Note = "";

        int pokemon5SpeciesId = -1;
        int pokemon5NatureId = -1;
        int pokemon5AbilityId = -1;
        int pokemon5ItemId = -1;
        int pokemon5Move1Id = -1;
        int pokemon5Move2Id = -1;
        int pokemon5Move3Id = -1;
        int pokemon5Move4Id = -1;
        int pokemon5IvHp = -1;
        int pokemon5IvAtt = -1;
        int pokemon5IvDef = -1;
        int pokemon5IvSAtt = -1;
        int pokemon5IvSDef = -1;
        int pokemon5IvSpd = -1;
        int pokemon5EvHp = -1;
        int pokemon5EvAtt = -1;
        int pokemon5EvDef = -1;
        int pokemon5EvSAtt = -1;
        int pokemon5EvSDef = -1;
        int pokemon5EvSpd = -1;
        String pokemon5Note = "";

        int pokemon6SpeciesId = -1;
        int pokemon6NatureId = -1;
        int pokemon6AbilityId = -1;
        int pokemon6ItemId = -1;
        int pokemon6Move1Id = -1;
        int pokemon6Move2Id = -1;
        int pokemon6Move3Id = -1;
        int pokemon6Move4Id = -1;
        int pokemon6IvHp = -1;
        int pokemon6IvAtt = -1;
        int pokemon6IvDef = -1;
        int pokemon6IvSAtt = -1;
        int pokemon6IvSDef = -1;
        int pokemon6IvSpd = -1;
        int pokemon6EvHp = -1;
        int pokemon6EvAtt = -1;
        int pokemon6EvDef = -1;
        int pokemon6EvSAtt = -1;
        int pokemon6EvSDef = -1;
        int pokemon6EvSpd = -1;
        String pokemon6Note = "";


        if (savedInstanceState != null) {
            int[] speciesIds = savedInstanceState.getIntArray(SPECIES_ID_LIST_KEY);
            pokemon1SpeciesId = speciesIds[0];
            pokemon2SpeciesId = speciesIds[1];
            pokemon3SpeciesId = speciesIds[2];
            pokemon4SpeciesId = speciesIds[3];
            pokemon5SpeciesId = speciesIds[4];
            pokemon6SpeciesId = speciesIds[5];

            int[] natureIds = savedInstanceState.getIntArray(NATURE_ID_LIST_KEY);
            pokemon1NatureId = natureIds[0];
            pokemon2NatureId = natureIds[1];
            pokemon3NatureId = natureIds[2];
            pokemon4NatureId = natureIds[3];
            pokemon5NatureId = natureIds[4];
            pokemon6NatureId = natureIds[5];

            int[] abilityIds = savedInstanceState.getIntArray(ABILITY_ID_LIST_KEY);
            pokemon1AbilityId = abilityIds[0];
            pokemon2AbilityId = abilityIds[1];
            pokemon3AbilityId = abilityIds[2];
            pokemon4AbilityId = abilityIds[3];
            pokemon5AbilityId = abilityIds[4];
            pokemon6AbilityId = abilityIds[5];

            int[] itemIds = savedInstanceState.getIntArray(ITEM_ID_LIST_KEY);
            pokemon1ItemId = itemIds[0];
            pokemon2ItemId = itemIds[1];
            pokemon3ItemId = itemIds[2];
            pokemon4ItemId = itemIds[3];
            pokemon5ItemId = itemIds[4];
            pokemon6ItemId = itemIds[5];

            int[] move1Ids = savedInstanceState.getIntArray(MOVE1_ID_LIST_KEY);
            pokemon1Move1Id = move1Ids[0];
            pokemon2Move1Id = move1Ids[1];
            pokemon3Move1Id = move1Ids[2];
            pokemon4Move1Id = move1Ids[3];
            pokemon5Move1Id = move1Ids[4];
            pokemon6Move1Id = move1Ids[5];

            int[] move2Ids = savedInstanceState.getIntArray(MOVE2_ID_LIST_KEY);
            pokemon1Move2Id = move2Ids[0];
            pokemon2Move2Id = move2Ids[1];
            pokemon3Move2Id = move2Ids[2];
            pokemon4Move2Id = move2Ids[3];
            pokemon5Move2Id = move2Ids[4];
            pokemon6Move2Id = move2Ids[5];

            int[] move3Ids = savedInstanceState.getIntArray(MOVE3_ID_LIST_KEY);
            pokemon1Move3Id = move3Ids[0];
            pokemon2Move3Id = move3Ids[1];
            pokemon3Move3Id = move3Ids[2];
            pokemon4Move3Id = move3Ids[3];
            pokemon5Move3Id = move3Ids[4];
            pokemon6Move3Id = move3Ids[5];

            int[] move4Ids = savedInstanceState.getIntArray(MOVE4_ID_LIST_KEY);
            pokemon1Move4Id = move4Ids[0];
            pokemon2Move4Id = move4Ids[1];
            pokemon3Move4Id = move4Ids[2];
            pokemon4Move4Id = move4Ids[3];
            pokemon5Move4Id = move4Ids[4];
            pokemon6Move4Id = move4Ids[5];

            int[] ivHPs = savedInstanceState.getIntArray(IV_HP_LIST_KEY);
            pokemon1IvHp = ivHPs[0];
            pokemon2IvHp = ivHPs[1];
            pokemon3IvHp = ivHPs[2];
            pokemon4IvHp = ivHPs[3];
            pokemon5IvHp = ivHPs[4];
            pokemon6IvHp = ivHPs[5];

            int[] ivAtts = savedInstanceState.getIntArray(IV_ATT_LIST_KEY);
            pokemon1IvAtt = ivAtts[0];
            pokemon2IvAtt = ivAtts[1];
            pokemon3IvAtt = ivAtts[2];
            pokemon4IvAtt = ivAtts[3];
            pokemon5IvAtt = ivAtts[4];
            pokemon6IvAtt = ivAtts[5];

            int[] ivDefs = savedInstanceState.getIntArray(IV_DEF_LIST_KEY);
            pokemon1IvDef = ivDefs[0];
            pokemon2IvDef = ivDefs[1];
            pokemon3IvDef = ivDefs[2];
            pokemon4IvDef = ivDefs[3];
            pokemon5IvDef = ivDefs[4];
            pokemon6IvDef = ivDefs[5];

            int[] ivSAtts = savedInstanceState.getIntArray(IV_SATT_LIST_KEY);
            pokemon1IvSAtt = ivSAtts[0];
            pokemon2IvSAtt = ivSAtts[1];
            pokemon3IvSAtt = ivSAtts[2];
            pokemon4IvSAtt = ivSAtts[3];
            pokemon5IvSAtt = ivSAtts[4];
            pokemon6IvSAtt = ivSAtts[5];

            int[] ivSDefs = savedInstanceState.getIntArray(IV_SDEF_LIST_KEY);
            pokemon1IvSDef = ivSDefs[0];
            pokemon2IvSDef = ivSDefs[1];
            pokemon3IvSDef = ivSDefs[2];
            pokemon4IvSDef = ivSDefs[3];
            pokemon5IvSDef = ivSDefs[4];
            pokemon6IvSDef = ivSDefs[5];

            int[] ivSpds = savedInstanceState.getIntArray(IV_SPD_LIST_KEY);
            pokemon1IvSpd = ivSpds[0];
            pokemon2IvSpd = ivSpds[1];
            pokemon3IvSpd = ivSpds[2];
            pokemon4IvSpd = ivSpds[3];
            pokemon5IvSpd = ivSpds[4];
            pokemon6IvSpd = ivSpds[5];

            int[] evHPs = savedInstanceState.getIntArray(EV_HP_LIST_KEY);
            pokemon1EvHp = evHPs[0];
            pokemon2EvHp = evHPs[1];
            pokemon3EvHp = evHPs[2];
            pokemon4EvHp = evHPs[3];
            pokemon5EvHp = evHPs[4];
            pokemon6EvHp = evHPs[5];

            int[] evAtts = savedInstanceState.getIntArray(EV_ATT_LIST_KEY);
            pokemon1EvAtt = evAtts[0];
            pokemon2EvAtt = evAtts[1];
            pokemon3EvAtt = evAtts[2];
            pokemon4EvAtt = evAtts[3];
            pokemon5EvAtt = evAtts[4];
            pokemon6EvAtt = evAtts[5];

            int[] evDefs = savedInstanceState.getIntArray(EV_DEF_LIST_KEY);
            pokemon1EvDef = evDefs[0];
            pokemon2EvDef = evDefs[1];
            pokemon3EvDef = evDefs[2];
            pokemon4EvDef = evDefs[3];
            pokemon5EvDef = evDefs[4];
            pokemon6EvDef = evDefs[5];

            int[] evSAtts = savedInstanceState.getIntArray(EV_SATT_LIST_KEY);
            pokemon1EvSAtt = evSAtts[0];
            pokemon2EvSAtt = evSAtts[1];
            pokemon3EvSAtt = evSAtts[2];
            pokemon4EvSAtt = evSAtts[3];
            pokemon5EvSAtt = evSAtts[4];
            pokemon6EvSAtt = evSAtts[5];

            int[] evSDefs = savedInstanceState.getIntArray(EV_SDEF_LIST_KEY);
            pokemon1EvSDef = evSDefs[0];
            pokemon2EvSDef = evSDefs[1];
            pokemon3EvSDef = evSDefs[2];
            pokemon4EvSDef = evSDefs[3];
            pokemon5EvSDef = evSDefs[4];
            pokemon6EvSDef = evSDefs[5];

            int[] evSpds = savedInstanceState.getIntArray(EV_SPD_LIST_KEY);
            pokemon1EvSpd = evSpds[0];
            pokemon2EvSpd = evSpds[1];
            pokemon3EvSpd = evSpds[2];
            pokemon4EvSpd = evSpds[3];
            pokemon5EvSpd = evSpds[4];
            pokemon6EvSpd = evSpds[5];

            String[] notes = savedInstanceState.getStringArray(NOTES_LIST_KEY);
            pokemon1Note = notes[0];
            pokemon2Note = notes[1];
            pokemon3Note = notes[2];
            pokemon4Note = notes[3];
            pokemon5Note = notes[4];
            pokemon6Note = notes[5];
        } else {
            if (getArguments() != null && !TextUtils.isEmpty(getArguments().getString(TEAM_ID_ARG))) {
                String teamId = getArguments().getString(TEAM_ID_ARG);
                Cursor teamCursor = getActivity().getContentResolver().query(TinapaContentProvider.PLANNED_TEAM_URI, null, teamId, null, null);
                if (teamCursor != null && teamCursor.moveToFirst()) {
                    // TODO Surely there is a better way to do this.
                    pokemon1SpeciesId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON1_ID));
                    pokemon1NatureId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON1_NATURE_ID));
                    pokemon1AbilityId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON1_ABILITY_ID));
                    pokemon1ItemId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON1_ITEM_ID));
                    pokemon1Move1Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON1_MOVE1_ID));
                    pokemon1Move2Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON1_MOVE2_ID));
                    pokemon1Move3Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON1_MOVE3_ID));
                    pokemon1Move4Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON1_MOVE4_ID));
                    pokemon1IvHp = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON1_IV_HP));
                    pokemon1IvAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON1_IV_ATT));
                    pokemon1IvDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON1_IV_DEF));
                    pokemon1IvSAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON1_IV_SATT));
                    pokemon1IvSDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON1_IV_SPD));
                    pokemon1IvSpd = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON1_IV_SPD));
                    pokemon1EvHp = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON1_EV_HP));
                    pokemon1EvAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON1_EV_ATT));
                    pokemon1EvDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON1_EV_DEF));
                    pokemon1EvSAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON1_EV_SATT));
                    pokemon1EvSDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON1_EV_SPD));
                    pokemon1EvSpd = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON1_EV_SPD));
                    pokemon1Note = teamCursor.getString(teamCursor.getColumnIndex(TeamKeyValues.POKEMON1_NOTE));

                    pokemon2SpeciesId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON2_ID));
                    pokemon2NatureId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON2_NATURE_ID));
                    pokemon2AbilityId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON2_ABILITY_ID));
                    pokemon2ItemId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON2_ITEM_ID));
                    pokemon2Move1Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON2_MOVE1_ID));
                    pokemon2Move2Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON2_MOVE2_ID));
                    pokemon2Move3Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON2_MOVE3_ID));
                    pokemon2Move4Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON2_MOVE4_ID));
                    pokemon2IvHp = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON2_IV_HP));
                    pokemon2IvAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON2_IV_ATT));
                    pokemon2IvDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON2_IV_DEF));
                    pokemon2IvSAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON2_IV_SATT));
                    pokemon2IvSDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON2_IV_SPD));
                    pokemon2IvSpd = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON2_IV_SPD));
                    pokemon2EvHp = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON2_EV_HP));
                    pokemon2EvAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON2_EV_ATT));
                    pokemon2EvDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON2_EV_DEF));
                    pokemon2EvSAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON2_EV_SATT));
                    pokemon2EvSDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON2_EV_SPD));
                    pokemon2EvSpd = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON2_EV_SPD));
                    pokemon2Note = teamCursor.getString(teamCursor.getColumnIndex(TeamKeyValues.POKEMON2_NOTE));

                    pokemon3SpeciesId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON3_ID));
                    pokemon3NatureId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON3_NATURE_ID));
                    pokemon3AbilityId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON3_ABILITY_ID));
                    pokemon3ItemId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON3_ITEM_ID));
                    pokemon3Move1Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON3_MOVE1_ID));
                    pokemon3Move2Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON3_MOVE2_ID));
                    pokemon3Move3Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON3_MOVE3_ID));
                    pokemon3Move4Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON3_MOVE4_ID));
                    pokemon3IvHp = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON3_IV_HP));
                    pokemon3IvAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON3_IV_ATT));
                    pokemon3IvDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON3_IV_DEF));
                    pokemon3IvSAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON3_IV_SATT));
                    pokemon3IvSDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON3_IV_SPD));
                    pokemon3IvSpd = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON3_IV_SPD));
                    pokemon3EvHp = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON3_EV_HP));
                    pokemon3EvAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON3_EV_ATT));
                    pokemon3EvDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON3_EV_DEF));
                    pokemon3EvSAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON3_EV_SATT));
                    pokemon3EvSDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON3_EV_SPD));
                    pokemon3EvSpd = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON3_EV_SPD));
                    pokemon3Note = teamCursor.getString(teamCursor.getColumnIndex(TeamKeyValues.POKEMON3_NOTE));

                    pokemon4SpeciesId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON4_ID));
                    pokemon4NatureId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON4_NATURE_ID));
                    pokemon4AbilityId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON4_ABILITY_ID));
                    pokemon4ItemId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON4_ITEM_ID));
                    pokemon4Move1Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON4_MOVE1_ID));
                    pokemon4Move2Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON4_MOVE2_ID));
                    pokemon4Move3Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON4_MOVE3_ID));
                    pokemon4Move4Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON4_MOVE4_ID));
                    pokemon4IvHp = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON4_IV_HP));
                    pokemon4IvAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON4_IV_ATT));
                    pokemon4IvDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON4_IV_DEF));
                    pokemon4IvSAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON4_IV_SATT));
                    pokemon4IvSDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON4_IV_SPD));
                    pokemon4IvSpd = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON4_IV_SPD));
                    pokemon4EvHp = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON4_EV_HP));
                    pokemon4EvAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON4_EV_ATT));
                    pokemon4EvDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON4_EV_DEF));
                    pokemon4EvSAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON4_EV_SATT));
                    pokemon4EvSDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON4_EV_SPD));
                    pokemon4EvSpd = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON4_EV_SPD));
                    pokemon4Note = teamCursor.getString(teamCursor.getColumnIndex(TeamKeyValues.POKEMON4_NOTE));

                    pokemon5SpeciesId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON5_ID));
                    pokemon5NatureId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON5_NATURE_ID));
                    pokemon5AbilityId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON5_ABILITY_ID));
                    pokemon5ItemId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON5_ITEM_ID));
                    pokemon5Move1Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON5_MOVE1_ID));
                    pokemon5Move2Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON5_MOVE2_ID));
                    pokemon5Move3Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON5_MOVE3_ID));
                    pokemon5Move4Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON5_MOVE4_ID));
                    pokemon5IvHp = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON5_IV_HP));
                    pokemon5IvAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON5_IV_ATT));
                    pokemon5IvDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON5_IV_DEF));
                    pokemon5IvSAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON5_IV_SATT));
                    pokemon5IvSDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON5_IV_SPD));
                    pokemon5IvSpd = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON5_IV_SPD));
                    pokemon5EvHp = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON5_EV_HP));
                    pokemon5EvAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON5_EV_ATT));
                    pokemon5EvDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON5_EV_DEF));
                    pokemon5EvSAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON5_EV_SATT));
                    pokemon5EvSDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON5_EV_SPD));
                    pokemon5EvSpd = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON5_EV_SPD));
                    pokemon5Note = teamCursor.getString(teamCursor.getColumnIndex(TeamKeyValues.POKEMON5_NOTE));

                    pokemon6SpeciesId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON6_ID));
                    pokemon6NatureId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON6_NATURE_ID));
                    pokemon6AbilityId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON6_ABILITY_ID));
                    pokemon6ItemId = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON6_ITEM_ID));
                    pokemon6Move1Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON6_MOVE1_ID));
                    pokemon6Move2Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON6_MOVE2_ID));
                    pokemon6Move3Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON6_MOVE3_ID));
                    pokemon6Move4Id = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON6_MOVE4_ID));
                    pokemon6IvHp = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON6_IV_HP));
                    pokemon6IvAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON6_IV_ATT));
                    pokemon6IvDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON6_IV_DEF));
                    pokemon6IvSAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON6_IV_SATT));
                    pokemon6IvSDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON6_IV_SPD));
                    pokemon6IvSpd = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON6_IV_SPD));
                    pokemon6EvHp = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON6_EV_HP));
                    pokemon6EvAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON6_EV_ATT));
                    pokemon6EvDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON6_EV_DEF));
                    pokemon6EvSAtt = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON6_EV_SATT));
                    pokemon6EvSDef = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON6_EV_SPD));
                    pokemon6EvSpd = teamCursor.getInt(teamCursor.getColumnIndex(TeamKeyValues.POKEMON6_EV_SPD));
                    pokemon6Note = teamCursor.getString(teamCursor.getColumnIndex(TeamKeyValues.POKEMON6_NOTE));

                }
            } else {
                pokemonSection1.setVisibility(View.GONE);
                pokemonSection2.setVisibility(View.GONE);
                pokemonSection3.setVisibility(View.GONE);
                pokemonSection4.setVisibility(View.GONE);
                pokemonSection5.setVisibility(View.GONE);
                pokemonSection6.setVisibility(View.GONE);
            }
        }

        loadPokemonSpinnerSectionWithData(pokemonSection1, getActivity(), pokemon1SpeciesId, pokemon1AbilityId, pokemon1ItemId, pokemon1NatureId, pokemon1Move1Id, pokemon1Move2Id, pokemon1Move3Id, pokemon1Move4Id);
        loadTextData(pokemonSection1, pokemon1EvHp, pokemon1EvAtt, pokemon1EvDef, pokemon1EvSAtt, pokemon1EvSDef, pokemon1EvSpd, pokemon1IvHp, pokemon1IvAtt, pokemon1IvDef, pokemon1IvSAtt, pokemon1IvSDef, pokemon1IvSpd, pokemon1Note);
        loadPokemonSpinnerSectionWithData(pokemonSection2, getActivity(), pokemon2SpeciesId, pokemon2AbilityId, pokemon2ItemId, pokemon2NatureId, pokemon2Move1Id, pokemon2Move2Id, pokemon2Move3Id, pokemon2Move4Id);
        loadTextData(pokemonSection2, pokemon2EvHp, pokemon2EvAtt, pokemon2EvDef, pokemon2EvSAtt, pokemon2EvSDef, pokemon2EvSpd, pokemon2IvHp, pokemon2IvAtt, pokemon2IvDef, pokemon2IvSAtt, pokemon2IvSDef, pokemon2IvSpd, pokemon2Note);
        loadPokemonSpinnerSectionWithData(pokemonSection3, getActivity(), pokemon3SpeciesId, pokemon3AbilityId, pokemon3ItemId, pokemon3NatureId, pokemon3Move1Id, pokemon3Move2Id, pokemon3Move3Id, pokemon3Move4Id);
        loadTextData(pokemonSection3, pokemon3EvHp, pokemon3EvAtt, pokemon3EvDef, pokemon3EvSAtt, pokemon3EvSDef, pokemon3EvSpd, pokemon3IvHp, pokemon3IvAtt, pokemon3IvDef, pokemon3IvSAtt, pokemon3IvSDef, pokemon3IvSpd, pokemon3Note);
        loadPokemonSpinnerSectionWithData(pokemonSection4, getActivity(), pokemon4SpeciesId, pokemon4AbilityId, pokemon4ItemId, pokemon4NatureId, pokemon4Move1Id, pokemon4Move2Id, pokemon4Move3Id, pokemon4Move4Id);
        loadTextData(pokemonSection4, pokemon4EvHp, pokemon4EvAtt, pokemon4EvDef, pokemon4EvSAtt, pokemon4EvSDef, pokemon4EvSpd, pokemon4IvHp, pokemon4IvAtt, pokemon4IvDef, pokemon4IvSAtt, pokemon4IvSDef, pokemon4IvSpd, pokemon4Note);
        loadPokemonSpinnerSectionWithData(pokemonSection5, getActivity(), pokemon5SpeciesId, pokemon5AbilityId, pokemon5ItemId, pokemon5NatureId, pokemon5Move1Id, pokemon5Move2Id, pokemon5Move3Id, pokemon5Move4Id);
        loadTextData(pokemonSection5, pokemon5EvHp, pokemon5EvAtt, pokemon5EvDef, pokemon5EvSAtt, pokemon5EvSDef, pokemon5EvSpd, pokemon5IvHp, pokemon5IvAtt, pokemon5IvDef, pokemon5IvSAtt, pokemon5IvSDef, pokemon5IvSpd, pokemon5Note);
        loadPokemonSpinnerSectionWithData(pokemonSection6, getActivity(), pokemon6SpeciesId, pokemon6AbilityId, pokemon6ItemId, pokemon6NatureId, pokemon6Move1Id, pokemon6Move2Id, pokemon6Move3Id, pokemon6Move4Id);
        loadTextData(pokemonSection6, pokemon6EvHp, pokemon6EvAtt, pokemon6EvDef, pokemon6EvSAtt, pokemon6EvSDef, pokemon6EvSpd, pokemon6IvHp, pokemon6IvAtt, pokemon6IvDef, pokemon6IvSAtt, pokemon6IvSDef, pokemon6IvSpd, pokemon6Note);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SaveTeamEvent event = generateSaveTeamEvent();  // This already collects everything, so let's use that.

        outState.putIntArray(SPECIES_ID_LIST_KEY, new int[]{Integer.parseInt(event.getPokemon1SpeciesId()), Integer.parseInt(event.getPokemon2SpeciesId()), Integer.parseInt(event.getPokemon3SpeciesId()), Integer.parseInt(event.getPokemon4SpeciesId()), Integer.parseInt(event.getPokemon5SpeciesId()), Integer.parseInt(event.getPokemon6SpeciesId())});
        outState.putIntArray(ABILITY_ID_LIST_KEY, new int[]{Integer.parseInt(event.getPokemon1AbilityId()), Integer.parseInt(event.getPokemon2AbilityId()), Integer.parseInt(event.getPokemon3AbilityId()), Integer.parseInt(event.getPokemon4AbilityId()), Integer.parseInt(event.getPokemon5AbilityId()), Integer.parseInt(event.getPokemon6AbilityId())});
        outState.putIntArray(ITEM_ID_LIST_KEY, new int[]{Integer.parseInt(event.getPokemon1ItemId()), Integer.parseInt(event.getPokemon2ItemId()), Integer.parseInt(event.getPokemon3ItemId()), Integer.parseInt(event.getPokemon4ItemId()), Integer.parseInt(event.getPokemon5ItemId()), Integer.parseInt(event.getPokemon6ItemId())});
        outState.putIntArray(NATURE_ID_LIST_KEY, new int[]{Integer.parseInt(event.getPokemon1NatureId()), Integer.parseInt(event.getPokemon2NatureId()), Integer.parseInt(event.getPokemon3NatureId()), Integer.parseInt(event.getPokemon4NatureId()), Integer.parseInt(event.getPokemon5NatureId()), Integer.parseInt(event.getPokemon6NatureId())});

        outState.putIntArray(MOVE1_ID_LIST_KEY, new int[]{Integer.parseInt(event.getPokemon1Move1Id()), Integer.parseInt(event.getPokemon2Move1Id()), Integer.parseInt(event.getPokemon3Move1Id()), Integer.parseInt(event.getPokemon4Move1Id()), Integer.parseInt(event.getPokemon5Move1Id()), Integer.parseInt(event.getPokemon6Move1Id())});
        outState.putIntArray(MOVE2_ID_LIST_KEY, new int[]{Integer.parseInt(event.getPokemon1Move2Id()), Integer.parseInt(event.getPokemon2Move2Id()), Integer.parseInt(event.getPokemon3Move2Id()), Integer.parseInt(event.getPokemon4Move2Id()), Integer.parseInt(event.getPokemon5Move2Id()), Integer.parseInt(event.getPokemon6Move2Id())});
        outState.putIntArray(MOVE3_ID_LIST_KEY, new int[]{Integer.parseInt(event.getPokemon1Move3Id()), Integer.parseInt(event.getPokemon2Move3Id()), Integer.parseInt(event.getPokemon3Move3Id()), Integer.parseInt(event.getPokemon4Move3Id()), Integer.parseInt(event.getPokemon5Move3Id()), Integer.parseInt(event.getPokemon6Move3Id())});
        outState.putIntArray(MOVE4_ID_LIST_KEY, new int[]{Integer.parseInt(event.getPokemon1Move4Id()), Integer.parseInt(event.getPokemon2Move4Id()), Integer.parseInt(event.getPokemon3Move4Id()), Integer.parseInt(event.getPokemon4Move4Id()), Integer.parseInt(event.getPokemon5Move4Id()), Integer.parseInt(event.getPokemon6Move4Id())});

        outState.putIntArray(IV_HP_LIST_KEY, new int[]{Integer.parseInt(event.getPokemon1IvHp()), Integer.parseInt(event.getPokemon2IvHp()), Integer.parseInt(event.getPokemon3IvHp()), Integer.parseInt(event.getPokemon4IvHp()), Integer.parseInt(event.getPokemon5IvHp()), Integer.parseInt(event.getPokemon6IvHp())});
        outState.putIntArray(IV_ATT_LIST_KEY, new int[]{Integer.parseInt(event.getPokemon1IvAtt()), Integer.parseInt(event.getPokemon2IvAtt()), Integer.parseInt(event.getPokemon3IvAtt()), Integer.parseInt(event.getPokemon4IvAtt()), Integer.parseInt(event.getPokemon5IvAtt()), Integer.parseInt(event.getPokemon6IvAtt())});
        outState.putIntArray(IV_DEF_LIST_KEY, new int[]{Integer.parseInt(event.getPokemon1IvDef()), Integer.parseInt(event.getPokemon2IvDef()), Integer.parseInt(event.getPokemon3IvDef()), Integer.parseInt(event.getPokemon4IvDef()), Integer.parseInt(event.getPokemon5IvDef()), Integer.parseInt(event.getPokemon6IvDef())});
        outState.putIntArray(IV_SATT_LIST_KEY, new int[]{Integer.parseInt(event.getPokemon1IvSAtt()), Integer.parseInt(event.getPokemon2IvSAtt()), Integer.parseInt(event.getPokemon3IvSAtt()), Integer.parseInt(event.getPokemon4IvSAtt()), Integer.parseInt(event.getPokemon5IvSAtt()), Integer.parseInt(event.getPokemon6IvSAtt())});
        outState.putIntArray(IV_SDEF_LIST_KEY, new int[]{Integer.parseInt(event.getPokemon1IvSDef()), Integer.parseInt(event.getPokemon2IvSDef()), Integer.parseInt(event.getPokemon3IvSDef()), Integer.parseInt(event.getPokemon4IvSDef()), Integer.parseInt(event.getPokemon5IvSDef()), Integer.parseInt(event.getPokemon6IvSDef())});
        outState.putIntArray(IV_SPD_LIST_KEY, new int[]{Integer.parseInt(event.getPokemon1IvSpd()), Integer.parseInt(event.getPokemon2IvSpd()), Integer.parseInt(event.getPokemon3IvSpd()), Integer.parseInt(event.getPokemon4IvSpd()), Integer.parseInt(event.getPokemon5IvSpd()), Integer.parseInt(event.getPokemon6IvSpd())});

        outState.putIntArray(EV_HP_LIST_KEY, new int[]{Integer.parseInt(event.getPokemon1EvHp()), Integer.parseInt(event.getPokemon2EvHp()), Integer.parseInt(event.getPokemon3EvHp()), Integer.parseInt(event.getPokemon4EvHp()), Integer.parseInt(event.getPokemon5EvHp()), Integer.parseInt(event.getPokemon6EvHp())});
        outState.putIntArray(EV_ATT_LIST_KEY, new int[]{Integer.parseInt(event.getPokemon1EvAtt()), Integer.parseInt(event.getPokemon2EvAtt()), Integer.parseInt(event.getPokemon3EvAtt()), Integer.parseInt(event.getPokemon4EvAtt()), Integer.parseInt(event.getPokemon5EvAtt()), Integer.parseInt(event.getPokemon6EvAtt())});
        outState.putIntArray(EV_DEF_LIST_KEY, new int[]{Integer.parseInt(event.getPokemon1EvDef()), Integer.parseInt(event.getPokemon2EvDef()), Integer.parseInt(event.getPokemon3EvDef()), Integer.parseInt(event.getPokemon4EvDef()), Integer.parseInt(event.getPokemon5EvDef()), Integer.parseInt(event.getPokemon6EvDef())});
        outState.putIntArray(EV_SATT_LIST_KEY, new int[]{Integer.parseInt(event.getPokemon1EvSAtt()), Integer.parseInt(event.getPokemon2EvSAtt()), Integer.parseInt(event.getPokemon3EvSAtt()), Integer.parseInt(event.getPokemon4EvSAtt()), Integer.parseInt(event.getPokemon5EvSAtt()), Integer.parseInt(event.getPokemon6EvSAtt())});
        outState.putIntArray(EV_SDEF_LIST_KEY, new int[]{Integer.parseInt(event.getPokemon1EvSDef()), Integer.parseInt(event.getPokemon2EvSDef()), Integer.parseInt(event.getPokemon3EvSDef()), Integer.parseInt(event.getPokemon4EvSDef()), Integer.parseInt(event.getPokemon5EvSDef()), Integer.parseInt(event.getPokemon6EvSDef())});
        outState.putIntArray(EV_SPD_LIST_KEY, new int[]{Integer.parseInt(event.getPokemon1EvSpd()), Integer.parseInt(event.getPokemon2EvSpd()), Integer.parseInt(event.getPokemon3EvSpd()), Integer.parseInt(event.getPokemon4EvSpd()), Integer.parseInt(event.getPokemon5EvSpd()), Integer.parseInt(event.getPokemon6EvSpd())});

        outState.putStringArray(NOTES_LIST_KEY, new String[]{event.getPokemon1Notes(), event.getPokemon2Notes(), event.getPokemon3Notes(), event.getPokemon4Notes(), event.getPokemon5Notes(), event.getPokemon6Notes()});

    }

    private static void setBannerToggleClickListener(View banner, final View toggleView) {
        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleView(toggleView);
            }
        });
    }

    private static void toggleView(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    private static void loadPokemonSpinnerSectionWithData(View section, final Activity activity, int pokemonSpeciesId, int pokemonAbilityId, int pokemonItemId, int pokemonNatureId, int pokemonMove1Id, int pokemonMove2Id, int pokemonMove3Id, int pokemonMove4Id) {
        Spinner pokemonSpeciesSpinner = (Spinner) section.findViewById(R.id.planned_add_species_spinner);
        Spinner pokemonAbilitySpinner = (Spinner) section.findViewById(R.id.planned_add_ability_spinner);
        Spinner pokemonItemSpinner = (Spinner) section.findViewById(R.id.planned_add_item_spinner);
        Spinner pokemonMove1Spinner = (Spinner) section.findViewById(R.id.planned_add_move1_spinner);
        Spinner pokemonMove2Spinner = (Spinner) section.findViewById(R.id.planned_add_move2_spinner);
        Spinner pokemonMove3Spinner = (Spinner) section.findViewById(R.id.planned_add_move3_spinner);
        Spinner pokemonMove4Spinner = (Spinner) section.findViewById(R.id.planned_add_move4_spinner);
        Spinner pokemonNatureSpinner = (Spinner) section.findViewById(R.id.planned_add_nature_spinner);

        PlannedPokemonUtils.setupSpeciesSpinners(activity, pokemonSpeciesSpinner, pokemonSpeciesId);
        PlannedPokemonUtils.setupNatureSpinner(activity, pokemonNatureSpinner, pokemonNatureId);
        PlannedPokemonUtils.setupItemSpinner(activity, pokemonItemSpinner, pokemonItemId);
        PlannedPokemonUtils.setupAbilityForPokemon(activity, pokemonAbilitySpinner, pokemonSpeciesId, pokemonAbilityId);
        PlannedPokemonUtils.setupAllMovesForPokemon(activity, pokemonMove1Spinner, pokemonMove2Spinner, pokemonMove3Spinner, pokemonMove4Spinner, pokemonSpeciesId, pokemonMove1Id, pokemonMove2Id, pokemonMove3Id, pokemonMove4Id);

        final Spinner finalAbilitySpinner = pokemonAbilitySpinner;
        final Spinner finalMove1Spinner = pokemonMove1Spinner;
        final Spinner finalMove2Spinner = pokemonMove2Spinner;
        final Spinner finalMove3Spinner = pokemonMove3Spinner;
        final Spinner finalMove4Spinner = pokemonMove4Spinner;

        pokemonSpeciesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PlannedPokemonUtils.setupAbilityForPokemon(activity, finalAbilitySpinner, (int) id, -1);
                PlannedPokemonUtils.setupAllMovesForPokemon(activity, finalMove1Spinner, finalMove2Spinner, finalMove3Spinner, finalMove4Spinner, (int) id, -1, -1, -1, -1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing!
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private static void loadTextData(View section, int evHP, int evAtt, int evDef, int evSAtt, int evSDef, int evSpd, int ivHP, int ivAtt, int ivDef, int ivSAtt, int ivSDef, int ivSpd, String note) {
        EditText evHPField = (EditText) section.findViewById(R.id.planned_add_ev_hp_edit_text);
        EditText evAttField = (EditText) section.findViewById(R.id.planned_add_ev_att_edit_text);
        EditText evDefField = (EditText) section.findViewById(R.id.planned_add_ev_def_edit_text);
        EditText evSAttField = (EditText) section.findViewById(R.id.planned_add_ev_satt_edit_text);
        EditText evSDefField = (EditText) section.findViewById(R.id.planned_add_ev_sdef_edit_text);
        EditText evSpdField = (EditText) section.findViewById(R.id.planned_add_ev_spd_edit_text);
        RadioGroup ivHPField = (RadioGroup) section.findViewById(R.id.planned_add_iv_hp_group);
        RadioGroup ivAttField = (RadioGroup) section.findViewById(R.id.planned_add_iv_att_group);
        RadioGroup ivDefField = (RadioGroup) section.findViewById(R.id.planned_add_iv_def_group);
        RadioGroup ivSAttField = (RadioGroup) section.findViewById(R.id.planned_add_iv_satt_group);
        RadioGroup ivSDefField = (RadioGroup) section.findViewById(R.id.planned_add_iv_sdef_group);
        RadioGroup ivSpdField = (RadioGroup) section.findViewById(R.id.planned_add_iv_spd_group);
        EditText noteField = (EditText) section.findViewById(R.id.planned_add_notes);

        if (evHP >= 0) {
            evHPField.setText(Integer.toString(evHP));
        }
        if (evAtt >= 0) {
            evAttField.setText(Integer.toString(evAtt));
        }
        if (evDef >= 0) {
            evDefField.setText(Integer.toString(evDef));
        }
        if (evSAtt >= 0) {
            evSAttField.setText(Integer.toString(evSAtt));
        }
        if (evSDef >= 0) {
            evSDefField.setText(Integer.toString(evSDef));
        }
        if (evSpd >= 0) {
            evSpdField.setText(Integer.toString(evSpd));
        }

        IVRadioGroupUtils.setCorrectIVValue(Integer.toString(ivHP), ivHPField);
        IVRadioGroupUtils.setCorrectIVValue(Integer.toString(ivAtt), ivAttField);
        IVRadioGroupUtils.setCorrectIVValue(Integer.toString(ivDef), ivDefField);
        IVRadioGroupUtils.setCorrectIVValue(Integer.toString(ivSAtt), ivSAttField);
        IVRadioGroupUtils.setCorrectIVValue(Integer.toString(ivSDef), ivSDefField);
        IVRadioGroupUtils.setCorrectIVValue(Integer.toString(ivSpd), ivSpdField);

        noteField.setText(note);
    }

    private SaveTeamEvent generateSaveTeamEvent() {
        String teamId = getArguments().getString(TEAM_ID_ARG);
        String teamName = ""; // TODO;
        String teamNotes = ""; // TODO;

        Spinner pokemon1SpeciesSpinner = (Spinner) pokemonSection1.findViewById(R.id.planned_add_species_spinner);
        String pokemon1SpeciesId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon1SpeciesSpinner);
        Spinner pokemon1AbilitySpinner = (Spinner) pokemonSection1.findViewById(R.id.planned_add_ability_spinner);
        String pokemon1AbilityId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon1AbilitySpinner);
        Spinner pokemon1ItemSpinner = (Spinner) pokemonSection1.findViewById(R.id.planned_add_item_spinner);
        String pokemon1ItemId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon1ItemSpinner);
        Spinner pokemon1Move1Spinner = (Spinner) pokemonSection1.findViewById(R.id.planned_add_move1_spinner);
        String pokemon1Move1Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon1Move1Spinner);
        Spinner pokemon1Move2Spinner = (Spinner) pokemonSection1.findViewById(R.id.planned_add_move2_spinner);
        String pokemon1Move2Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon1Move2Spinner);
        Spinner pokemon1Move3Spinner = (Spinner) pokemonSection1.findViewById(R.id.planned_add_move3_spinner);
        String pokemon1Move3Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon1Move3Spinner);
        Spinner pokemon1Move4Spinner = (Spinner) pokemonSection1.findViewById(R.id.planned_add_move4_spinner);
        String pokemon1Move4Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon1Move4Spinner);
        Spinner pokemon1NatureSpinner = (Spinner) pokemonSection1.findViewById(R.id.planned_add_nature_spinner);
        String pokemon1NatureId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon1NatureSpinner);
        String pokemon1EvHp = getEffortValueFromField((EditText) pokemonSection1.findViewById(R.id.planned_add_ev_hp_edit_text));
        String pokemon1EvAtt = getEffortValueFromField((EditText) pokemonSection1.findViewById(R.id.planned_add_ev_att_edit_text));
        String pokemon1EvDef = getEffortValueFromField((EditText) pokemonSection1.findViewById(R.id.planned_add_ev_def_edit_text));
        String pokemon1EvSAtt = getEffortValueFromField((EditText) pokemonSection1.findViewById(R.id.planned_add_ev_satt_edit_text));
        String pokemon1EvSDef = getEffortValueFromField((EditText) pokemonSection1.findViewById(R.id.planned_add_ev_sdef_edit_text));
        String pokemon1EvSpd = getEffortValueFromField((EditText) pokemonSection1.findViewById(R.id.planned_add_ev_spd_edit_text));
        String pokemon1IvHp = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection1.findViewById(R.id.planned_add_iv_hp_group), getActivity());
        String pokemon1IvAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection1.findViewById(R.id.planned_add_iv_att_group), getActivity());
        String pokemon1IvDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection1.findViewById(R.id.planned_add_iv_def_group), getActivity());
        String pokemon1IvSAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection1.findViewById(R.id.planned_add_iv_satt_group), getActivity());
        String pokemon1IvSDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection1.findViewById(R.id.planned_add_iv_sdef_group), getActivity());
        String pokemon1IvSpd = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection1.findViewById(R.id.planned_add_iv_spd_group), getActivity());
        String pokemon1Notes = ((EditText) pokemonSection1.findViewById(R.id.planned_add_notes)).getText().toString();

        Spinner pokemon2SpeciesSpinner = (Spinner) pokemonSection2.findViewById(R.id.planned_add_species_spinner);
        String pokemon2SpeciesId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon2SpeciesSpinner);
        Spinner pokemon2AbilitySpinner = (Spinner) pokemonSection2.findViewById(R.id.planned_add_ability_spinner);
        String pokemon2AbilityId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon2AbilitySpinner);
        Spinner pokemon2ItemSpinner = (Spinner) pokemonSection2.findViewById(R.id.planned_add_item_spinner);
        String pokemon2ItemId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon2ItemSpinner);
        Spinner pokemon2Move1Spinner = (Spinner) pokemonSection2.findViewById(R.id.planned_add_move1_spinner);
        String pokemon2Move1Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon2Move1Spinner);
        Spinner pokemon2Move2Spinner = (Spinner) pokemonSection2.findViewById(R.id.planned_add_move2_spinner);
        String pokemon2Move2Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon2Move2Spinner);
        Spinner pokemon2Move3Spinner = (Spinner) pokemonSection2.findViewById(R.id.planned_add_move3_spinner);
        String pokemon2Move3Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon2Move3Spinner);
        Spinner pokemon2Move4Spinner = (Spinner) pokemonSection2.findViewById(R.id.planned_add_move4_spinner);
        String pokemon2Move4Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon2Move4Spinner);
        Spinner pokemon2NatureSpinner = (Spinner) pokemonSection2.findViewById(R.id.planned_add_nature_spinner);
        String pokemon2NatureId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon2NatureSpinner);
        String pokemon2EvHp = getEffortValueFromField((EditText) pokemonSection2.findViewById(R.id.planned_add_ev_hp_edit_text));
        String pokemon2EvAtt = getEffortValueFromField((EditText) pokemonSection2.findViewById(R.id.planned_add_ev_att_edit_text));
        String pokemon2EvDef = getEffortValueFromField((EditText) pokemonSection2.findViewById(R.id.planned_add_ev_def_edit_text));
        String pokemon2EvSAtt = getEffortValueFromField((EditText) pokemonSection2.findViewById(R.id.planned_add_ev_satt_edit_text));
        String pokemon2EvSDef = getEffortValueFromField((EditText) pokemonSection2.findViewById(R.id.planned_add_ev_sdef_edit_text));
        String pokemon2EvSpd = getEffortValueFromField((EditText) pokemonSection2.findViewById(R.id.planned_add_ev_spd_edit_text));
        String pokemon2IvHp = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection2.findViewById(R.id.planned_add_iv_hp_group), getActivity());
        String pokemon2IvAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection2.findViewById(R.id.planned_add_iv_att_group), getActivity());
        String pokemon2IvDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection2.findViewById(R.id.planned_add_iv_def_group), getActivity());
        String pokemon2IvSAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection2.findViewById(R.id.planned_add_iv_satt_group), getActivity());
        String pokemon2IvSDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection2.findViewById(R.id.planned_add_iv_sdef_group), getActivity());
        String pokemon2IvSpd = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection2.findViewById(R.id.planned_add_iv_spd_group), getActivity());
        String pokemon2Notes = ((EditText) pokemonSection2.findViewById(R.id.planned_add_notes)).getText().toString();

        Spinner pokemon3SpeciesSpinner = (Spinner) pokemonSection3.findViewById(R.id.planned_add_species_spinner);
        String pokemon3SpeciesId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon3SpeciesSpinner);
        Spinner pokemon3AbilitySpinner = (Spinner) pokemonSection3.findViewById(R.id.planned_add_ability_spinner);
        String pokemon3AbilityId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon3AbilitySpinner);
        Spinner pokemon3ItemSpinner = (Spinner) pokemonSection3.findViewById(R.id.planned_add_item_spinner);
        String pokemon3ItemId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon3ItemSpinner);
        Spinner pokemon3Move1Spinner = (Spinner) pokemonSection3.findViewById(R.id.planned_add_move1_spinner);
        String pokemon3Move1Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon3Move1Spinner);
        Spinner pokemon3Move2Spinner = (Spinner) pokemonSection3.findViewById(R.id.planned_add_move2_spinner);
        String pokemon3Move2Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon3Move2Spinner);
        Spinner pokemon3Move3Spinner = (Spinner) pokemonSection3.findViewById(R.id.planned_add_move3_spinner);
        String pokemon3Move3Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon3Move3Spinner);
        Spinner pokemon3Move4Spinner = (Spinner) pokemonSection3.findViewById(R.id.planned_add_move4_spinner);
        String pokemon3Move4Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon3Move4Spinner);
        Spinner pokemon3NatureSpinner = (Spinner) pokemonSection3.findViewById(R.id.planned_add_nature_spinner);
        String pokemon3NatureId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon3NatureSpinner);
        String pokemon3EvHp = getEffortValueFromField((EditText) pokemonSection3.findViewById(R.id.planned_add_ev_hp_edit_text));
        String pokemon3EvAtt = getEffortValueFromField((EditText) pokemonSection3.findViewById(R.id.planned_add_ev_att_edit_text));
        String pokemon3EvDef = getEffortValueFromField((EditText) pokemonSection3.findViewById(R.id.planned_add_ev_def_edit_text));
        String pokemon3EvSAtt = getEffortValueFromField((EditText) pokemonSection3.findViewById(R.id.planned_add_ev_satt_edit_text));
        String pokemon3EvSDef = getEffortValueFromField((EditText) pokemonSection3.findViewById(R.id.planned_add_ev_sdef_edit_text));
        String pokemon3EvSpd = getEffortValueFromField((EditText) pokemonSection3.findViewById(R.id.planned_add_ev_spd_edit_text));
        String pokemon3IvHp = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection3.findViewById(R.id.planned_add_iv_hp_group), getActivity());
        String pokemon3IvAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection3.findViewById(R.id.planned_add_iv_att_group), getActivity());
        String pokemon3IvDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection3.findViewById(R.id.planned_add_iv_def_group), getActivity());
        String pokemon3IvSAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection3.findViewById(R.id.planned_add_iv_satt_group), getActivity());
        String pokemon3IvSDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection3.findViewById(R.id.planned_add_iv_sdef_group), getActivity());
        String pokemon3IvSpd = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection3.findViewById(R.id.planned_add_iv_spd_group), getActivity());
        String pokemon3Notes = ((EditText) pokemonSection3.findViewById(R.id.planned_add_notes)).getText().toString();

        Spinner pokemon4SpeciesSpinner = (Spinner) pokemonSection4.findViewById(R.id.planned_add_species_spinner);
        String pokemon4SpeciesId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon4SpeciesSpinner);
        Spinner pokemon4AbilitySpinner = (Spinner) pokemonSection4.findViewById(R.id.planned_add_ability_spinner);
        String pokemon4AbilityId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon4AbilitySpinner);
        Spinner pokemon4ItemSpinner = (Spinner) pokemonSection4.findViewById(R.id.planned_add_item_spinner);
        String pokemon4ItemId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon4ItemSpinner);
        Spinner pokemon4Move1Spinner = (Spinner) pokemonSection4.findViewById(R.id.planned_add_move1_spinner);
        String pokemon4Move1Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon4Move1Spinner);
        Spinner pokemon4Move2Spinner = (Spinner) pokemonSection4.findViewById(R.id.planned_add_move2_spinner);
        String pokemon4Move2Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon4Move2Spinner);
        Spinner pokemon4Move3Spinner = (Spinner) pokemonSection4.findViewById(R.id.planned_add_move3_spinner);
        String pokemon4Move3Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon4Move3Spinner);
        Spinner pokemon4Move4Spinner = (Spinner) pokemonSection4.findViewById(R.id.planned_add_move4_spinner);
        String pokemon4Move4Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon4Move4Spinner);
        Spinner pokemon4NatureSpinner = (Spinner) pokemonSection4.findViewById(R.id.planned_add_nature_spinner);
        String pokemon4NatureId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon4NatureSpinner);
        String pokemon4EvHp = getEffortValueFromField((EditText) pokemonSection4.findViewById(R.id.planned_add_ev_hp_edit_text));
        String pokemon4EvAtt = getEffortValueFromField((EditText) pokemonSection4.findViewById(R.id.planned_add_ev_att_edit_text));
        String pokemon4EvDef = getEffortValueFromField((EditText) pokemonSection4.findViewById(R.id.planned_add_ev_def_edit_text));
        String pokemon4EvSAtt = getEffortValueFromField((EditText) pokemonSection4.findViewById(R.id.planned_add_ev_satt_edit_text));
        String pokemon4EvSDef = getEffortValueFromField((EditText) pokemonSection4.findViewById(R.id.planned_add_ev_sdef_edit_text));
        String pokemon4EvSpd = getEffortValueFromField((EditText) pokemonSection4.findViewById(R.id.planned_add_ev_spd_edit_text));
        String pokemon4IvHp = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection4.findViewById(R.id.planned_add_iv_hp_group), getActivity());
        String pokemon4IvAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection4.findViewById(R.id.planned_add_iv_att_group), getActivity());
        String pokemon4IvDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection4.findViewById(R.id.planned_add_iv_def_group), getActivity());
        String pokemon4IvSAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection4.findViewById(R.id.planned_add_iv_satt_group), getActivity());
        String pokemon4IvSDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection4.findViewById(R.id.planned_add_iv_sdef_group), getActivity());
        String pokemon4IvSpd = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection4.findViewById(R.id.planned_add_iv_spd_group), getActivity());
        String pokemon4Notes = ((EditText) pokemonSection4.findViewById(R.id.planned_add_notes)).getText().toString();

        Spinner pokemon5SpeciesSpinner = (Spinner) pokemonSection5.findViewById(R.id.planned_add_species_spinner);
        String pokemon5SpeciesId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon5SpeciesSpinner);
        Spinner pokemon5AbilitySpinner = (Spinner) pokemonSection5.findViewById(R.id.planned_add_ability_spinner);
        String pokemon5AbilityId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon5AbilitySpinner);
        Spinner pokemon5ItemSpinner = (Spinner) pokemonSection5.findViewById(R.id.planned_add_item_spinner);
        String pokemon5ItemId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon5ItemSpinner);
        Spinner pokemon5Move1Spinner = (Spinner) pokemonSection5.findViewById(R.id.planned_add_move1_spinner);
        String pokemon5Move1Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon5Move1Spinner);
        Spinner pokemon5Move2Spinner = (Spinner) pokemonSection5.findViewById(R.id.planned_add_move2_spinner);
        String pokemon5Move2Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon5Move2Spinner);
        Spinner pokemon5Move3Spinner = (Spinner) pokemonSection5.findViewById(R.id.planned_add_move3_spinner);
        String pokemon5Move3Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon5Move3Spinner);
        Spinner pokemon5Move4Spinner = (Spinner) pokemonSection5.findViewById(R.id.planned_add_move4_spinner);
        String pokemon5Move4Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon5Move4Spinner);
        Spinner pokemon5NatureSpinner = (Spinner) pokemonSection5.findViewById(R.id.planned_add_nature_spinner);
        String pokemon5NatureId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon5NatureSpinner);
        String pokemon5EvHp = getEffortValueFromField((EditText) pokemonSection5.findViewById(R.id.planned_add_ev_hp_edit_text));
        String pokemon5EvAtt = getEffortValueFromField((EditText) pokemonSection5.findViewById(R.id.planned_add_ev_att_edit_text));
        String pokemon5EvDef = getEffortValueFromField((EditText) pokemonSection5.findViewById(R.id.planned_add_ev_def_edit_text));
        String pokemon5EvSAtt = getEffortValueFromField((EditText) pokemonSection5.findViewById(R.id.planned_add_ev_satt_edit_text));
        String pokemon5EvSDef = getEffortValueFromField((EditText) pokemonSection5.findViewById(R.id.planned_add_ev_sdef_edit_text));
        String pokemon5EvSpd = getEffortValueFromField((EditText) pokemonSection5.findViewById(R.id.planned_add_ev_spd_edit_text));
        String pokemon5IvHp = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection5.findViewById(R.id.planned_add_iv_hp_group), getActivity());
        String pokemon5IvAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection5.findViewById(R.id.planned_add_iv_att_group), getActivity());
        String pokemon5IvDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection5.findViewById(R.id.planned_add_iv_def_group), getActivity());
        String pokemon5IvSAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection5.findViewById(R.id.planned_add_iv_satt_group), getActivity());
        String pokemon5IvSDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection5.findViewById(R.id.planned_add_iv_sdef_group), getActivity());
        String pokemon5IvSpd = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection5.findViewById(R.id.planned_add_iv_spd_group), getActivity());
        String pokemon5Notes = ((EditText) pokemonSection5.findViewById(R.id.planned_add_notes)).getText().toString();

        Spinner pokemon6SpeciesSpinner = (Spinner) pokemonSection6.findViewById(R.id.planned_add_species_spinner);
        String pokemon6SpeciesId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon6SpeciesSpinner);
        Spinner pokemon6AbilitySpinner = (Spinner) pokemonSection6.findViewById(R.id.planned_add_ability_spinner);
        String pokemon6AbilityId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon6AbilitySpinner);
        Spinner pokemon6ItemSpinner = (Spinner) pokemonSection6.findViewById(R.id.planned_add_item_spinner);
        String pokemon6ItemId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon6ItemSpinner);
        Spinner pokemon6Move1Spinner = (Spinner) pokemonSection6.findViewById(R.id.planned_add_move1_spinner);
        String pokemon6Move1Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon6Move1Spinner);
        Spinner pokemon6Move2Spinner = (Spinner) pokemonSection6.findViewById(R.id.planned_add_move2_spinner);
        String pokemon6Move2Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon6Move2Spinner);
        Spinner pokemon6Move3Spinner = (Spinner) pokemonSection6.findViewById(R.id.planned_add_move3_spinner);
        String pokemon6Move3Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon6Move3Spinner);
        Spinner pokemon6Move4Spinner = (Spinner) pokemonSection6.findViewById(R.id.planned_add_move4_spinner);
        String pokemon6Move4Id = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon6Move4Spinner);
        Spinner pokemon6NatureSpinner = (Spinner) pokemonSection6.findViewById(R.id.planned_add_nature_spinner);
        String pokemon6NatureId = TeamAddDialogFragment.getSelectedItemIdFromSpinner(pokemon6NatureSpinner);
        String pokemon6EvHp = getEffortValueFromField((EditText) pokemonSection6.findViewById(R.id.planned_add_ev_hp_edit_text));
        String pokemon6EvAtt = getEffortValueFromField((EditText) pokemonSection6.findViewById(R.id.planned_add_ev_att_edit_text));
        String pokemon6EvDef = getEffortValueFromField((EditText) pokemonSection6.findViewById(R.id.planned_add_ev_def_edit_text));
        String pokemon6EvSAtt = getEffortValueFromField((EditText) pokemonSection6.findViewById(R.id.planned_add_ev_satt_edit_text));
        String pokemon6EvSDef = getEffortValueFromField((EditText) pokemonSection6.findViewById(R.id.planned_add_ev_sdef_edit_text));
        String pokemon6EvSpd = getEffortValueFromField((EditText) pokemonSection6.findViewById(R.id.planned_add_ev_spd_edit_text));
        String pokemon6IvHp = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection6.findViewById(R.id.planned_add_iv_hp_group), getActivity());
        String pokemon6IvAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection6.findViewById(R.id.planned_add_iv_att_group), getActivity());
        String pokemon6IvDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection6.findViewById(R.id.planned_add_iv_def_group), getActivity());
        String pokemon6IvSAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection6.findViewById(R.id.planned_add_iv_satt_group), getActivity());
        String pokemon6IvSDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection6.findViewById(R.id.planned_add_iv_sdef_group), getActivity());
        String pokemon6IvSpd = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection6.findViewById(R.id.planned_add_iv_spd_group), getActivity());
        String pokemon6Notes = ((EditText) pokemonSection6.findViewById(R.id.planned_add_notes)).getText().toString();

        return new SaveTeamEvent(teamId, teamName, teamNotes,
                pokemon1Id, pokemon1SpeciesId, pokemon1AbilityId, pokemon1ItemId, pokemon1Move1Id, pokemon1Move2Id, pokemon1Move3Id, pokemon1Move4Id, pokemon1NatureId, pokemon1EvHp, pokemon1EvAtt, pokemon1EvDef, pokemon1EvSAtt, pokemon1EvSDef, pokemon1EvSpd, pokemon1IvHp, pokemon1IvAtt, pokemon1IvDef, pokemon1IvSAtt, pokemon1IvSDef, pokemon1IvSpd, pokemon1Notes,
                pokemon2Id, pokemon2SpeciesId, pokemon2AbilityId, pokemon2ItemId, pokemon2Move1Id, pokemon2Move2Id, pokemon2Move3Id, pokemon2Move4Id, pokemon2NatureId, pokemon2EvHp, pokemon2EvAtt, pokemon2EvDef, pokemon2EvSAtt, pokemon2EvSDef, pokemon2EvSpd, pokemon2IvHp, pokemon2IvAtt, pokemon2IvDef, pokemon2IvSAtt, pokemon2IvSDef, pokemon2IvSpd, pokemon2Notes,
                pokemon3Id, pokemon3SpeciesId, pokemon3AbilityId, pokemon3ItemId, pokemon3Move1Id, pokemon3Move2Id, pokemon3Move3Id, pokemon3Move4Id, pokemon3NatureId, pokemon3EvHp, pokemon3EvAtt, pokemon3EvDef, pokemon3EvSAtt, pokemon3EvSDef, pokemon3EvSpd, pokemon3IvHp, pokemon3IvAtt, pokemon3IvDef, pokemon3IvSAtt, pokemon3IvSDef, pokemon3IvSpd, pokemon3Notes,
                pokemon4Id, pokemon4SpeciesId, pokemon4AbilityId, pokemon4ItemId, pokemon4Move1Id, pokemon4Move2Id, pokemon4Move3Id, pokemon4Move4Id, pokemon4NatureId, pokemon4EvHp, pokemon4EvAtt, pokemon4EvDef, pokemon4EvSAtt, pokemon4EvSDef, pokemon4EvSpd, pokemon4IvHp, pokemon4IvAtt, pokemon4IvDef, pokemon4IvSAtt, pokemon4IvSDef, pokemon4IvSpd, pokemon4Notes,
                pokemon5Id, pokemon5SpeciesId, pokemon5AbilityId, pokemon5ItemId, pokemon5Move1Id, pokemon5Move2Id, pokemon5Move3Id, pokemon5Move4Id, pokemon5NatureId, pokemon5EvHp, pokemon5EvAtt, pokemon5EvDef, pokemon5EvSAtt, pokemon5EvSDef, pokemon5EvSpd, pokemon5IvHp, pokemon5IvAtt, pokemon5IvDef, pokemon5IvSAtt, pokemon5IvSDef, pokemon5IvSpd, pokemon5Notes,
                pokemon6Id, pokemon6SpeciesId, pokemon6AbilityId, pokemon6ItemId, pokemon6Move1Id, pokemon6Move2Id, pokemon6Move3Id, pokemon6Move4Id, pokemon6NatureId, pokemon6EvHp, pokemon6EvAtt, pokemon6EvDef, pokemon6EvSAtt, pokemon6EvSDef, pokemon6EvSpd, pokemon6IvHp, pokemon6IvAtt, pokemon6IvDef, pokemon6IvSAtt, pokemon6IvSDef, pokemon6IvSpd, pokemon6Notes);
    }

    private static String getSelectedItemIdFromSpinner(Spinner spinner) {
        if (spinner != null && spinner.getCount() > 0) {
            return String.valueOf(spinner.getSelectedItemId());
        }
        return "-1";
    }

    private static String getEffortValueFromField(EditText field) {
        String result = field.getText().toString();
        if (TextUtils.isEmpty(result)) {
            result = "-1";
        }
        return result;
    }
}
