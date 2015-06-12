package com.tehpanda.dragoneon.journal.View;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.tehpanda.dragoneon.journal.Controller.IJournalController;
import com.tehpanda.dragoneon.journal.Controller.JournalController;
import com.tehpanda.dragoneon.journal.Model.IJournalEntry;
import com.tehpanda.dragoneon.journal.R;

/**
 * Created by panda on 11/10/14.
 */
public class PasswordDialogue extends DialogFragment{
    private EditText _PasswordEntry;
    private Button _Confirm;
    private IJournalController journalController;

    IJournalEntry entry;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        journalController = JournalController.GetInstance();
        entry = journalController.getEntry(getArguments()
                .getInt("currentBook"), getArguments().getInt("entrypos"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Password");

        View v = inflater.inflate(R.layout.fragment_password_dialogue, container, false);
        _PasswordEntry = (EditText) v.findViewById(R.id.editText2);
        _Confirm = (Button) v.findViewById(R.id.button);

        _Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (entry.Decrypt(_PasswordEntry.getText().toString())) {
                    ((MainActivity)getActivity()).fragment_editFrag.ReloadData();
                    Log.e("Password!!!", "Correct!");
                    dismiss();
                } else {
                    Log.e("Password!!!", "Wrong!");
                }
            }
        });
        return  v;
    }
}
