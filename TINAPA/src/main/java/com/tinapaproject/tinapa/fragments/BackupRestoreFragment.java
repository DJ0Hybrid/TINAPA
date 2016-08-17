package com.tinapaproject.tinapa.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.squareup.otto.Bus;
import com.tinapaproject.tinapa.R;
import com.tinapaproject.tinapa.TinapaApplication;
import com.tinapaproject.tinapa.events.BackupFileSelectionClickedEvent;

public class BackupRestoreFragment extends DialogFragment {

    private Bus bus;

    public BackupRestoreFragment() {
        // Required empty public constructor
    }

    public static BackupRestoreFragment getInstance() {
        BackupRestoreFragment fragment = new BackupRestoreFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bus = TinapaApplication.bus;
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
        builder.setCancelable(false);
        this.setCancelable(false);
        builder.setTitle(R.string.backup);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.setView(createView(builder.getContext(), null, savedInstanceState));

        return builder.create();
    }

    private View createView(Context context, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_backup, viewGroup, false);

        Button fileSelectButton = (Button) view.findViewById(R.id.backup_file_select_button);

        fileSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(new BackupFileSelectionClickedEvent());
            }
        });

        return view;
    }
}
