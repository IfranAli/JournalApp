package com.tehpanda.dragoneon.journal.View;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.tehpanda.dragoneon.journal.Model.SharedPrefMgr;
import com.tehpanda.dragoneon.journal.R;

public class SettingsFragment extends DialogFragment {
    private MainActivity mainActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Change Settings");

        mainActivity = ((MainActivity)getActivity());
        // Inflate the layout for this fragment
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
                mainActivity.fragment_listView.SetTheme(ParseThemeIndex(position));
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
            case 5: theme = R.layout.item_layout_mcard; break;
            default: theme = R.layout.item_layout_card; break;
        }
        return theme;
    }
}
