package com.tehpanda.dragoneon.journal.View;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tehpanda.dragoneon.journal.Controller.IJournalController;
import com.tehpanda.dragoneon.journal.Model.IJournalEntry;
import com.tehpanda.dragoneon.journal.R;

public class EditFragment extends Fragment {
    // instance of Jcontroller
    private IJournalController _JournalController;
    // instance of current entry
    private IJournalEntry entry;

    // Index of current book and position
    private boolean initialized;
    private int position;
    private int currentBook;

    // UI elements
    private EditText editData;
    private EditText editTitle;
    // View
    private TextView edit_notyetloaded;

    private static final String KEY_INDEX = "index";
    private static final String KEY_INITIALIZED = "initialized";
    private static final String KEY_CURRENT_BOOK = "book";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    //region ActionBar Items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(entry != null && entry.isEncrypted()) {
            inflater.inflate(R.menu.editfragment_actions, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_lock:
                Toast.makeText(getActivity().getApplicationContext(), entry.isEncrypted().toString(),Toast.LENGTH_SHORT).show();
                return  true;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("OnCreateView", "EditFrag");
        _JournalController = ((MainActivity)getActivity()).iJournalController;
        // Inflate the layout for this fragment
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_edit, container, false);
        // Get references to elements.
        editData = (EditText) view.findViewById(R.id.editText);
        editTitle = (EditText) view.findViewById(R.id.editTitle);
        edit_notyetloaded = (TextView) view.findViewById(R.id.Edit_NotLoaded);

        // Show "No Note Loaded" Text.
        edit_notyetloaded.setVisibility(View.VISIBLE);
        if (savedInstanceState != null && savedInstanceState.getBoolean(KEY_INITIALIZED)) {
            Log.e("OnCreate_Edit", "Loading Saved Instance State");
            position = savedInstanceState.getInt(KEY_INDEX);
            currentBook = savedInstanceState.getInt(KEY_CURRENT_BOOK);
            LoadNewEntry(currentBook, position, false);
        }
        return view;
    }

    public void ReloadData() {
        editData.setText(entry.getData());
        editTitle.setText(entry.getTitle());
    }

    public void LoadNewEntry(int currentBook, int position, boolean saveOnLoad){
        initialized = true;
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
        // Hide not loaded text.
        edit_notyetloaded.setVisibility(View.GONE);
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
            Boolean modified = false;
            String title = editTitle.getText().toString();
            String data = editData.getText().toString();

            String mdata = entry.getData();
            String mtitle = entry.getTitle();

            if(data.equals(mdata)) {
                modified = false;
            } else  {
                modified = true;
            }
            if (modified != true) {
                if(!title.equals(mtitle)) {
                    modified = true;
                } else {
                    modified = false;
                }
            }

            if(modified) {
                Log.e("Modified", "Saving");
                entry.setData(data);
                entry.setTitle(title);
                _JournalController.saveXML(currentBook);
            } else {
                Log.e("Modified", "False CPU cycle saved! :D");
            }
        } catch (Exception ex) {
            //Log.e("ERROR", ex.getMessage());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(initialized) {
            Log.e("Edit", "OnSaveInstance");
            outState.putInt(KEY_INDEX, position);
            outState.putInt(KEY_CURRENT_BOOK, currentBook);
            outState.putBoolean(KEY_INITIALIZED, true);
        } else {
            outState.putBoolean(KEY_INITIALIZED, false);
        }
        super.onSaveInstanceState(outState);
    }
}