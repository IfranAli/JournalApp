package com.tehpanda.dragoneon.journal.View;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.tehpanda.dragoneon.journal.Controller.IJournalController;
import com.tehpanda.dragoneon.journal.Model.IJournalEntry;
import com.tehpanda.dragoneon.journal.R;

public class EditFragment extends Fragment {
    // instance of Jcontroller
    private IJournalController _JournalController;
    // instance of current entry
    private IJournalEntry entry;

    // Index of current book and position
    private boolean initalized;
    private int position;
    private int currentBook;

    // UI elements
    private EditText editData;
    private EditText editTitle;
    // View
    private TextView edit_notyetloaded;

    private static final String KEY_INDEX = "index";
    private static final String KEY_CURRENT_BOOK = "book";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("OnCreateView", "EditFrag");
        _JournalController = ((MainActivity)getActivity()).iJournalController;
        // Inflate the layout for this fragment
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_edit, container, false);
        // Get references to elements.
        editData = (EditText) view.findViewById(R.id.editText);
        editTitle = (EditText) view.findViewById(R.id.editTitle);
        // Display Message.
        edit_notyetloaded = (TextView) view.findViewById(R.id.Edit_NotLoaded);
        visibility(false);
        if (savedInstanceState != null) {
            Log.e("OnCreate_Edit", "Loading Saved Instance State");
            // Restore saved instance.
            position = savedInstanceState.getInt(KEY_INDEX);
            currentBook = savedInstanceState.getInt(KEY_CURRENT_BOOK);
            LoadNewEntry(currentBook, position, false);
        }
        if(initalized){
            visibility(true);
        }
        return view;
    }

    public void LoadNewEntry(int currentBook, int position, boolean saveOnLoad){
        initalized = true;
        // Save previous changes.
        if(saveOnLoad){
            SaveChanges();
        }
        // Set globals.
        this.currentBook = currentBook;
        this.position = position;
        // Get Entry object..
        this.entry = _JournalController.getEntry(currentBook, position);
        // Set Data.
        editTitle.setText(entry.getTitle());
        editData.setText(entry.getData());
        visibility(true);
    }

    private void visibility(boolean b){
        if(b){
            // Make visible.
            edit_notyetloaded.setVisibility(View.GONE);
        } else {
            // Hide editor.
            edit_notyetloaded.setVisibility(View.VISIBLE);
        }
    }

    public void SaveChanges(){
        //Toast.makeText(getActivity().getApplicationContext(), "Saving Changes", Toast.LENGTH_SHORT).show();
        if(edit_notyetloaded.isShown()) {
            // Prevents overwriting entry 0,0
            return;
        }
        if(entry == null){
            // Get Entry if it's null.
            Log.e("Editor", "Getting book");
            this.entry = _JournalController.getEntry(currentBook, position);
        }
        try {
            entry.setData(editData.getText().toString());
            entry.setTitle(editTitle.getText().toString());
            _JournalController.saveXML(currentBook);
        } catch (Exception ex) {
            //Log.e("ERROR", ex.getMessage());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e("Edit", "OnSaveInstance");
        outState.putInt(KEY_INDEX, position);
        outState.putInt(KEY_CURRENT_BOOK, currentBook);
        super.onSaveInstanceState(outState);
    }
}