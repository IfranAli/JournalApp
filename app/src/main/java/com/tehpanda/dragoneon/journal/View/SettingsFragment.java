package com.tehpanda.dragoneon.journal.View;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.tehpanda.dragoneon.journal.Model.SharedPrefMgr;
import com.tehpanda.dragoneon.journal.R;

public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private MainActivity mainActivity;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = ((MainActivity)getActivity());
        // Inflate the layout for this fragment
        //View view = inflater.inflate(R.layout.fragment_settings, container, false);
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_settings, container, false);

        // Theme selector..
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.Themes, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Save Preferences for layout.
                SharedPrefMgr.Write(getActivity(), SharedPrefMgr.KEY_LAYOUT_LAST_USED, String.valueOf(position));
                mainActivity.fragment_listView.SetTheme(ParseThemeIndex(position), true); // set this to false after todo.
                //todo: CHECK IF A BOOK IS LOADED FIRST!!
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Load Preferences for layout.
        spinner.setSelection(Integer.parseInt(SharedPrefMgr.Read(getActivity(), SharedPrefMgr.KEY_LAYOUT_LAST_USED, "0")));
        return view;
    }

    public static int ParseThemeIndex(int theme) {
        switch (theme) {
            case 0: theme = R.layout.item_layout_card; break;
            case 1: theme = R.layout.item_layout_card_simple; break;
            case 2: theme = R.layout.item_layout_card_encap; break;
            case 3: theme = R.layout.item_layout_stack; break;
            case 4: theme = R.layout.item_layout_square; break;
            default: theme = R.layout.item_layout_card; break;
        }
        return theme;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
