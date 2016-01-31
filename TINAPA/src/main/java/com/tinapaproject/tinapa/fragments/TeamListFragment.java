package com.tinapaproject.tinapa.fragments;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.squareup.otto.Bus;
import com.tinapaproject.tinapa.R;
import com.tinapaproject.tinapa.TinapaApplication;
import com.tinapaproject.tinapa.adapters.IndividualCursorAdapter;
import com.tinapaproject.tinapa.adapters.TeamCursorAdapter;
import com.tinapaproject.tinapa.database.provider.TinapaContentProvider;

public class TeamListFragment extends Fragment {
    public static final String TAG = "TeamListFragment";

    private Bus bus;

    public TeamListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_list, container, false);

        ListView listView = (ListView) view.findViewById(R.id.teamListView);

        Cursor teamListCursor = getActivity().getContentResolver().query(TinapaContentProvider.PLANNED_TEAM_LIST_URI, null, null, null, null);
        // TODO: Do I really need the column names?
        TeamCursorAdapter adapter = new TeamCursorAdapter(getActivity(), teamListCursor, new String[0], "", new String[0], TinapaContentProvider.PLANNED_TEAM_LIST_URI);
        listView.setAdapter(adapter);

        return view;
    }
}
