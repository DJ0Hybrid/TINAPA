package com.tinapaproject.tinapa.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinapaproject.tinapa.R;

public class PlannedAddDialogFragment extends DialogFragment {

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
        return inflater.inflate(R.layout.fragment_planned_add_dialog, container, false);
    }
}
