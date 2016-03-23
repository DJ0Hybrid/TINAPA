package com.tinapaproject.tinapa.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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

        if (savedInstanceState == null) {
            pokemonSection1.setVisibility(View.GONE);
            pokemonSection2.setVisibility(View.GONE);
            pokemonSection3.setVisibility(View.GONE);
            pokemonSection4.setVisibility(View.GONE);
            pokemonSection5.setVisibility(View.GONE);
            pokemonSection6.setVisibility(View.GONE);
        } else {
            // TODO need to do preserved state.
        }

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

        loadPokemonSpinnerSectionWithData(pokemonSection1, getActivity());
        loadPokemonSpinnerSectionWithData(pokemonSection2, getActivity());
        loadPokemonSpinnerSectionWithData(pokemonSection3, getActivity());
        loadPokemonSpinnerSectionWithData(pokemonSection4, getActivity());
        loadPokemonSpinnerSectionWithData(pokemonSection5, getActivity());
        loadPokemonSpinnerSectionWithData(pokemonSection6, getActivity());

        return view;
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

    private static void loadPokemonSpinnerSectionWithData(View section, final Activity activity) {
        Spinner pokemonSpeciesSpinner = (Spinner) section.findViewById(R.id.planned_add_species_spinner);
        Spinner pokemonAbilitySpinner = (Spinner) section.findViewById(R.id.planned_add_ability_spinner);
        Spinner pokemonItemSpinner = (Spinner) section.findViewById(R.id.planned_add_item_spinner);
        Spinner pokemonMove1Spinner = (Spinner) section.findViewById(R.id.planned_add_move1_spinner);
        Spinner pokemonMove2Spinner = (Spinner) section.findViewById(R.id.planned_add_move2_spinner);
        Spinner pokemonMove3Spinner = (Spinner) section.findViewById(R.id.planned_add_move3_spinner);
        Spinner pokemonMove4Spinner = (Spinner) section.findViewById(R.id.planned_add_move4_spinner);
        Spinner pokemonNatureSpinner = (Spinner) section.findViewById(R.id.planned_add_nature_spinner);

        PlannedPokemonUtils.setupSpeciesSpinners(activity, pokemonSpeciesSpinner, -1);
        PlannedPokemonUtils.setupNatureSpinner(activity, pokemonNatureSpinner, -1);
        PlannedPokemonUtils.setupItemSpinner(activity, pokemonItemSpinner, -1);
        PlannedPokemonUtils.setupAbilityForPokemon(activity, pokemonAbilitySpinner, -1, -1);
        PlannedPokemonUtils.setupAllMovesForPokemon(activity, pokemonMove1Spinner, pokemonMove2Spinner, pokemonMove3Spinner, pokemonMove4Spinner, -1, -1, -1, -1, -1);

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

    private SaveTeamEvent generateSaveTeamEvent() {
        String teamId = getArguments().getString(TEAM_ID_ARG);
        String teamName = ""; // TODO;
        String teamNotes = ""; // TODO;

        Spinner pokemon1SpeciesSpinner = (Spinner) pokemonSection1.findViewById(R.id.planned_add_species_spinner);
        String pokemon1SpeciesId = String.valueOf(pokemon1SpeciesSpinner.getSelectedItemId());
        Spinner pokemon1AbilitySpinner = (Spinner) pokemonSection1.findViewById(R.id.planned_add_ability_spinner);
        String pokemon1AbilityId = String.valueOf(pokemon1AbilitySpinner.getSelectedItemId());
        Spinner pokemon1ItemSpinner = (Spinner) pokemonSection1.findViewById(R.id.planned_add_item_spinner);
        String pokemon1ItemId = String.valueOf(pokemon1ItemSpinner.getSelectedItemId());
        Spinner pokemon1Move1Spinner = (Spinner) pokemonSection1.findViewById(R.id.planned_add_move1_spinner);
        String pokemon1Move1Id = String.valueOf(pokemon1Move1Spinner.getSelectedItemId());
        Spinner pokemon1Move2Spinner = (Spinner) pokemonSection1.findViewById(R.id.planned_add_move2_spinner);
        String pokemon1Move2Id = String.valueOf(pokemon1Move2Spinner.getSelectedItemId());
        Spinner pokemon1Move3Spinner = (Spinner) pokemonSection1.findViewById(R.id.planned_add_move3_spinner);
        String pokemon1Move3Id = String.valueOf(pokemon1Move3Spinner.getSelectedItemId());
        Spinner pokemon1Move4Spinner = (Spinner) pokemonSection1.findViewById(R.id.planned_add_move4_spinner);
        String pokemon1Move4Id = String.valueOf(pokemon1Move4Spinner.getSelectedItemId());
        Spinner pokemon1NatureSpinner = (Spinner) pokemonSection1.findViewById(R.id.planned_add_nature_spinner);
        String pokemon1NatureId = String.valueOf(pokemon1NatureSpinner.getSelectedItemId());
        String pokemon1EvHp = ((EditText) pokemonSection1.findViewById(R.id.planned_add_ev_hp_edit_text)).getText().toString();
        String pokemon1EvAtt = ((EditText) pokemonSection1.findViewById(R.id.planned_add_ev_att_edit_text)).getText().toString();
        String pokemon1EvDef = ((EditText) pokemonSection1.findViewById(R.id.planned_add_ev_def_edit_text)).getText().toString();
        String pokemon1EvSAtt = ((EditText) pokemonSection1.findViewById(R.id.planned_add_ev_satt_edit_text)).getText().toString();
        String pokemon1EvSDef = ((EditText) pokemonSection1.findViewById(R.id.planned_add_ev_sdef_edit_text)).getText().toString();
        String pokemon1EvSpd = ((EditText) pokemonSection1.findViewById(R.id.planned_add_ev_spd_edit_text)).getText().toString();
        String pokemon1IvHp = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection1.findViewById(R.id.planned_add_iv_hp_group), getActivity());
        String pokemon1IvAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection1.findViewById(R.id.planned_add_iv_att_group), getActivity());
        String pokemon1IvDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection1.findViewById(R.id.planned_add_iv_def_group), getActivity());
        String pokemon1IvSAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection1.findViewById(R.id.planned_add_iv_satt_group), getActivity());
        String pokemon1IvSDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection1.findViewById(R.id.planned_add_iv_sdef_group), getActivity());
        String pokemon1IvSpd = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection1.findViewById(R.id.planned_add_iv_spd_group), getActivity());
        String pokemon1Notes = ((EditText) pokemonSection1.findViewById(R.id.planned_add_notes)).getText().toString();

        Spinner pokemon2SpeciesSpinner = (Spinner) pokemonSection2.findViewById(R.id.planned_add_species_spinner);
        String pokemon2SpeciesId = String.valueOf(pokemon2SpeciesSpinner.getSelectedItemId());
        Spinner pokemon2AbilitySpinner = (Spinner) pokemonSection2.findViewById(R.id.planned_add_ability_spinner);
        String pokemon2AbilityId = String.valueOf(pokemon2AbilitySpinner.getSelectedItemId());
        Spinner pokemon2ItemSpinner = (Spinner) pokemonSection2.findViewById(R.id.planned_add_item_spinner);
        String pokemon2ItemId = String.valueOf(pokemon2ItemSpinner.getSelectedItemId());
        Spinner pokemon2Move1Spinner = (Spinner) pokemonSection2.findViewById(R.id.planned_add_move1_spinner);
        String pokemon2Move1Id = String.valueOf(pokemon2Move1Spinner.getSelectedItemId());
        Spinner pokemon2Move2Spinner = (Spinner) pokemonSection2.findViewById(R.id.planned_add_move2_spinner);
        String pokemon2Move2Id = String.valueOf(pokemon2Move2Spinner.getSelectedItemId());
        Spinner pokemon2Move3Spinner = (Spinner) pokemonSection2.findViewById(R.id.planned_add_move3_spinner);
        String pokemon2Move3Id = String.valueOf(pokemon2Move3Spinner.getSelectedItemId());
        Spinner pokemon2Move4Spinner = (Spinner) pokemonSection2.findViewById(R.id.planned_add_move4_spinner);
        String pokemon2Move4Id = String.valueOf(pokemon2Move4Spinner.getSelectedItemId());
        Spinner pokemon2NatureSpinner = (Spinner) pokemonSection2.findViewById(R.id.planned_add_nature_spinner);
        String pokemon2NatureId = String.valueOf(pokemon2NatureSpinner.getSelectedItemId());
        String pokemon2EvHp = ((EditText) pokemonSection2.findViewById(R.id.planned_add_ev_hp_edit_text)).getText().toString();
        String pokemon2EvAtt = ((EditText) pokemonSection2.findViewById(R.id.planned_add_ev_att_edit_text)).getText().toString();
        String pokemon2EvDef = ((EditText) pokemonSection2.findViewById(R.id.planned_add_ev_def_edit_text)).getText().toString();
        String pokemon2EvSAtt = ((EditText) pokemonSection2.findViewById(R.id.planned_add_ev_satt_edit_text)).getText().toString();
        String pokemon2EvSDef = ((EditText) pokemonSection2.findViewById(R.id.planned_add_ev_sdef_edit_text)).getText().toString();
        String pokemon2EvSpd = ((EditText) pokemonSection2.findViewById(R.id.planned_add_ev_spd_edit_text)).getText().toString();
        String pokemon2IvHp = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection2.findViewById(R.id.planned_add_iv_hp_group), getActivity());
        String pokemon2IvAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection2.findViewById(R.id.planned_add_iv_att_group), getActivity());
        String pokemon2IvDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection2.findViewById(R.id.planned_add_iv_def_group), getActivity());
        String pokemon2IvSAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection2.findViewById(R.id.planned_add_iv_satt_group), getActivity());
        String pokemon2IvSDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection2.findViewById(R.id.planned_add_iv_sdef_group), getActivity());
        String pokemon2IvSpd = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection2.findViewById(R.id.planned_add_iv_spd_group), getActivity());
        String pokemon2Notes = ((EditText) pokemonSection2.findViewById(R.id.planned_add_notes)).getText().toString();

        Spinner pokemon3SpeciesSpinner = (Spinner) pokemonSection3.findViewById(R.id.planned_add_species_spinner);
        String pokemon3SpeciesId = String.valueOf(pokemon3SpeciesSpinner.getSelectedItemId());
        Spinner pokemon3AbilitySpinner = (Spinner) pokemonSection3.findViewById(R.id.planned_add_ability_spinner);
        String pokemon3AbilityId = String.valueOf(pokemon3AbilitySpinner.getSelectedItemId());
        Spinner pokemon3ItemSpinner = (Spinner) pokemonSection3.findViewById(R.id.planned_add_item_spinner);
        String pokemon3ItemId = String.valueOf(pokemon3ItemSpinner.getSelectedItemId());
        Spinner pokemon3Move1Spinner = (Spinner) pokemonSection3.findViewById(R.id.planned_add_move1_spinner);
        String pokemon3Move1Id = String.valueOf(pokemon3Move1Spinner.getSelectedItemId());
        Spinner pokemon3Move2Spinner = (Spinner) pokemonSection3.findViewById(R.id.planned_add_move2_spinner);
        String pokemon3Move2Id = String.valueOf(pokemon3Move2Spinner.getSelectedItemId());
        Spinner pokemon3Move3Spinner = (Spinner) pokemonSection3.findViewById(R.id.planned_add_move3_spinner);
        String pokemon3Move3Id = String.valueOf(pokemon3Move3Spinner.getSelectedItemId());
        Spinner pokemon3Move4Spinner = (Spinner) pokemonSection3.findViewById(R.id.planned_add_move4_spinner);
        String pokemon3Move4Id = String.valueOf(pokemon3Move4Spinner.getSelectedItemId());
        Spinner pokemon3NatureSpinner = (Spinner) pokemonSection3.findViewById(R.id.planned_add_nature_spinner);
        String pokemon3NatureId = String.valueOf(pokemon3NatureSpinner.getSelectedItemId());
        String pokemon3EvHp = ((EditText) pokemonSection3.findViewById(R.id.planned_add_ev_hp_edit_text)).getText().toString();
        String pokemon3EvAtt = ((EditText) pokemonSection3.findViewById(R.id.planned_add_ev_att_edit_text)).getText().toString();
        String pokemon3EvDef = ((EditText) pokemonSection3.findViewById(R.id.planned_add_ev_def_edit_text)).getText().toString();
        String pokemon3EvSAtt = ((EditText) pokemonSection3.findViewById(R.id.planned_add_ev_satt_edit_text)).getText().toString();
        String pokemon3EvSDef = ((EditText) pokemonSection3.findViewById(R.id.planned_add_ev_sdef_edit_text)).getText().toString();
        String pokemon3EvSpd = ((EditText) pokemonSection3.findViewById(R.id.planned_add_ev_spd_edit_text)).getText().toString();
        String pokemon3IvHp = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection3.findViewById(R.id.planned_add_iv_hp_group), getActivity());
        String pokemon3IvAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection3.findViewById(R.id.planned_add_iv_att_group), getActivity());
        String pokemon3IvDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection3.findViewById(R.id.planned_add_iv_def_group), getActivity());
        String pokemon3IvSAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection3.findViewById(R.id.planned_add_iv_satt_group), getActivity());
        String pokemon3IvSDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection3.findViewById(R.id.planned_add_iv_sdef_group), getActivity());
        String pokemon3IvSpd = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection3.findViewById(R.id.planned_add_iv_spd_group), getActivity());
        String pokemon3Notes = ((EditText) pokemonSection3.findViewById(R.id.planned_add_notes)).getText().toString();

        Spinner pokemon4SpeciesSpinner = (Spinner) pokemonSection4.findViewById(R.id.planned_add_species_spinner);
        String pokemon4SpeciesId = String.valueOf(pokemon4SpeciesSpinner.getSelectedItemId());
        Spinner pokemon4AbilitySpinner = (Spinner) pokemonSection4.findViewById(R.id.planned_add_ability_spinner);
        String pokemon4AbilityId = String.valueOf(pokemon4AbilitySpinner.getSelectedItemId());
        Spinner pokemon4ItemSpinner = (Spinner) pokemonSection4.findViewById(R.id.planned_add_item_spinner);
        String pokemon4ItemId = String.valueOf(pokemon4ItemSpinner.getSelectedItemId());
        Spinner pokemon4Move1Spinner = (Spinner) pokemonSection4.findViewById(R.id.planned_add_move1_spinner);
        String pokemon4Move1Id = String.valueOf(pokemon4Move1Spinner.getSelectedItemId());
        Spinner pokemon4Move2Spinner = (Spinner) pokemonSection4.findViewById(R.id.planned_add_move2_spinner);
        String pokemon4Move2Id = String.valueOf(pokemon4Move2Spinner.getSelectedItemId());
        Spinner pokemon4Move3Spinner = (Spinner) pokemonSection4.findViewById(R.id.planned_add_move3_spinner);
        String pokemon4Move3Id = String.valueOf(pokemon4Move3Spinner.getSelectedItemId());
        Spinner pokemon4Move4Spinner = (Spinner) pokemonSection4.findViewById(R.id.planned_add_move4_spinner);
        String pokemon4Move4Id = String.valueOf(pokemon4Move4Spinner.getSelectedItemId());
        Spinner pokemon4NatureSpinner = (Spinner) pokemonSection4.findViewById(R.id.planned_add_nature_spinner);
        String pokemon4NatureId = String.valueOf(pokemon4NatureSpinner.getSelectedItemId());
        String pokemon4EvHp = ((EditText) pokemonSection4.findViewById(R.id.planned_add_ev_hp_edit_text)).getText().toString();
        String pokemon4EvAtt = ((EditText) pokemonSection4.findViewById(R.id.planned_add_ev_att_edit_text)).getText().toString();
        String pokemon4EvDef = ((EditText) pokemonSection4.findViewById(R.id.planned_add_ev_def_edit_text)).getText().toString();
        String pokemon4EvSAtt = ((EditText) pokemonSection4.findViewById(R.id.planned_add_ev_satt_edit_text)).getText().toString();
        String pokemon4EvSDef = ((EditText) pokemonSection4.findViewById(R.id.planned_add_ev_sdef_edit_text)).getText().toString();
        String pokemon4EvSpd = ((EditText) pokemonSection4.findViewById(R.id.planned_add_ev_spd_edit_text)).getText().toString();
        String pokemon4IvHp = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection4.findViewById(R.id.planned_add_iv_hp_group), getActivity());
        String pokemon4IvAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection4.findViewById(R.id.planned_add_iv_att_group), getActivity());
        String pokemon4IvDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection4.findViewById(R.id.planned_add_iv_def_group), getActivity());
        String pokemon4IvSAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection4.findViewById(R.id.planned_add_iv_satt_group), getActivity());
        String pokemon4IvSDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection4.findViewById(R.id.planned_add_iv_sdef_group), getActivity());
        String pokemon4IvSpd = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection4.findViewById(R.id.planned_add_iv_spd_group), getActivity());
        String pokemon4Notes = ((EditText) pokemonSection4.findViewById(R.id.planned_add_notes)).getText().toString();

        Spinner pokemon5SpeciesSpinner = (Spinner) pokemonSection5.findViewById(R.id.planned_add_species_spinner);
        String pokemon5SpeciesId = String.valueOf(pokemon5SpeciesSpinner.getSelectedItemId());
        Spinner pokemon5AbilitySpinner = (Spinner) pokemonSection5.findViewById(R.id.planned_add_ability_spinner);
        String pokemon5AbilityId = String.valueOf(pokemon5AbilitySpinner.getSelectedItemId());
        Spinner pokemon5ItemSpinner = (Spinner) pokemonSection5.findViewById(R.id.planned_add_item_spinner);
        String pokemon5ItemId = String.valueOf(pokemon5ItemSpinner.getSelectedItemId());
        Spinner pokemon5Move1Spinner = (Spinner) pokemonSection5.findViewById(R.id.planned_add_move1_spinner);
        String pokemon5Move1Id = String.valueOf(pokemon5Move1Spinner.getSelectedItemId());
        Spinner pokemon5Move2Spinner = (Spinner) pokemonSection5.findViewById(R.id.planned_add_move2_spinner);
        String pokemon5Move2Id = String.valueOf(pokemon5Move2Spinner.getSelectedItemId());
        Spinner pokemon5Move3Spinner = (Spinner) pokemonSection5.findViewById(R.id.planned_add_move3_spinner);
        String pokemon5Move3Id = String.valueOf(pokemon5Move3Spinner.getSelectedItemId());
        Spinner pokemon5Move4Spinner = (Spinner) pokemonSection5.findViewById(R.id.planned_add_move4_spinner);
        String pokemon5Move4Id = String.valueOf(pokemon5Move4Spinner.getSelectedItemId());
        Spinner pokemon5NatureSpinner = (Spinner) pokemonSection5.findViewById(R.id.planned_add_nature_spinner);
        String pokemon5NatureId = String.valueOf(pokemon5NatureSpinner.getSelectedItemId());
        String pokemon5EvHp = ((EditText) pokemonSection5.findViewById(R.id.planned_add_ev_hp_edit_text)).getText().toString();
        String pokemon5EvAtt = ((EditText) pokemonSection5.findViewById(R.id.planned_add_ev_att_edit_text)).getText().toString();
        String pokemon5EvDef = ((EditText) pokemonSection5.findViewById(R.id.planned_add_ev_def_edit_text)).getText().toString();
        String pokemon5EvSAtt = ((EditText) pokemonSection5.findViewById(R.id.planned_add_ev_satt_edit_text)).getText().toString();
        String pokemon5EvSDef = ((EditText) pokemonSection5.findViewById(R.id.planned_add_ev_sdef_edit_text)).getText().toString();
        String pokemon5EvSpd = ((EditText) pokemonSection5.findViewById(R.id.planned_add_ev_spd_edit_text)).getText().toString();
        String pokemon5IvHp = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection5.findViewById(R.id.planned_add_iv_hp_group), getActivity());
        String pokemon5IvAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection5.findViewById(R.id.planned_add_iv_att_group), getActivity());
        String pokemon5IvDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection5.findViewById(R.id.planned_add_iv_def_group), getActivity());
        String pokemon5IvSAtt = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection5.findViewById(R.id.planned_add_iv_satt_group), getActivity());
        String pokemon5IvSDef = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection5.findViewById(R.id.planned_add_iv_sdef_group), getActivity());
        String pokemon5IvSpd = IVRadioGroupUtils.getIVValueSelected((RadioGroup) pokemonSection5.findViewById(R.id.planned_add_iv_spd_group), getActivity());
        String pokemon5Notes = ((EditText) pokemonSection5.findViewById(R.id.planned_add_notes)).getText().toString();

        Spinner pokemon6SpeciesSpinner = (Spinner) pokemonSection6.findViewById(R.id.planned_add_species_spinner);
        String pokemon6SpeciesId = String.valueOf(pokemon6SpeciesSpinner.getSelectedItemId());
        Spinner pokemon6AbilitySpinner = (Spinner) pokemonSection6.findViewById(R.id.planned_add_ability_spinner);
        String pokemon6AbilityId = String.valueOf(pokemon6AbilitySpinner.getSelectedItemId());
        Spinner pokemon6ItemSpinner = (Spinner) pokemonSection6.findViewById(R.id.planned_add_item_spinner);
        String pokemon6ItemId = String.valueOf(pokemon6ItemSpinner.getSelectedItemId());
        Spinner pokemon6Move1Spinner = (Spinner) pokemonSection6.findViewById(R.id.planned_add_move1_spinner);
        String pokemon6Move1Id = String.valueOf(pokemon6Move1Spinner.getSelectedItemId());
        Spinner pokemon6Move2Spinner = (Spinner) pokemonSection6.findViewById(R.id.planned_add_move2_spinner);
        String pokemon6Move2Id = String.valueOf(pokemon6Move2Spinner.getSelectedItemId());
        Spinner pokemon6Move3Spinner = (Spinner) pokemonSection6.findViewById(R.id.planned_add_move3_spinner);
        String pokemon6Move3Id = String.valueOf(pokemon6Move3Spinner.getSelectedItemId());
        Spinner pokemon6Move4Spinner = (Spinner) pokemonSection6.findViewById(R.id.planned_add_move4_spinner);
        String pokemon6Move4Id = String.valueOf(pokemon6Move4Spinner.getSelectedItemId());
        Spinner pokemon6NatureSpinner = (Spinner) pokemonSection6.findViewById(R.id.planned_add_nature_spinner);
        String pokemon6NatureId = String.valueOf(pokemon6NatureSpinner.getSelectedItemId());
        String pokemon6EvHp = ((EditText) pokemonSection6.findViewById(R.id.planned_add_ev_hp_edit_text)).getText().toString();
        String pokemon6EvAtt = ((EditText) pokemonSection6.findViewById(R.id.planned_add_ev_att_edit_text)).getText().toString();
        String pokemon6EvDef = ((EditText) pokemonSection6.findViewById(R.id.planned_add_ev_def_edit_text)).getText().toString();
        String pokemon6EvSAtt = ((EditText) pokemonSection6.findViewById(R.id.planned_add_ev_satt_edit_text)).getText().toString();
        String pokemon6EvSDef = ((EditText) pokemonSection6.findViewById(R.id.planned_add_ev_sdef_edit_text)).getText().toString();
        String pokemon6EvSpd = ((EditText) pokemonSection6.findViewById(R.id.planned_add_ev_spd_edit_text)).getText().toString();
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
}
