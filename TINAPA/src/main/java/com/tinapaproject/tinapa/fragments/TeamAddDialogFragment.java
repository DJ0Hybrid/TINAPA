package com.tinapaproject.tinapa.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Bus;
import com.tinapaproject.tinapa.R;
import com.tinapaproject.tinapa.TinapaApplication;

public class TeamAddDialogFragment extends DialogFragment {
    private Bus bus = TinapaApplication.bus;

    View pokemonSection1;
    View pokemonSection2;
    View pokemonSection3;
    View pokemonSection4;
    View pokemonSection5;
    View pokemonSection6;

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
//        builder.setCancelable(false);
//        this.setCancelable(false);

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
}
