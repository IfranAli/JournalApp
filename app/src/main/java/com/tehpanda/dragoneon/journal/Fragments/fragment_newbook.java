package com.tehpanda.dragoneon.journal.Fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.tehpanda.dragoneon.journal.Controller.IJournalController;
import com.tehpanda.dragoneon.journal.Controller.JournalController;
import com.tehpanda.dragoneon.journal.R;

/**
 * Created by panda on 11/10/14.
 */
public class fragment_newbook extends DialogFragment{
    private EditText filename;
    private EditText bookname;
    private Button bConfirm;
    private IJournalController journalController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Create New Book");

        View v = inflater.inflate(R.layout.fragmentdialog_newbook, container, false);
        journalController = JournalController.GetInstance();
        filename = (EditText) v.findViewById(R.id.textedit_filename);
        bookname = (EditText) v.findViewById(R.id.textedit_bookname);
        bConfirm = (Button) v.findViewById(R.id.button_confirm);

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                journalController.AddNewBook(bookname.getText().toString());
                sendResult();
            }
        });
        return  v;
    }

    private void sendResult() {
        //((MainActivity)getActivity()).fragment_bookView.UpdateAdapter();
        dismiss();
    }
}
