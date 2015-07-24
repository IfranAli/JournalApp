package com.tehpanda.dragoneon.journal.Fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.tehpanda.dragoneon.journal.Controller.IJournalController;
import com.tehpanda.dragoneon.journal.R;
import com.tehpanda.dragoneon.journal.View.MainActivity;

public class fragment_edit_book extends DialogFragment {
    private EditText bookname;
    private int bookIndex;
    private IJournalController journalController;

    public static fragment_edit_book newInstance(int bookIndex){
        fragment_edit_book f = new fragment_edit_book();
        Bundle args = new Bundle();
        args.putInt("index", bookIndex);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("OnCreate", "EditBookFragment");
        setStyle(STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().setTitle("Edit Book");

        journalController = ((MainActivity)getActivity()).iJournalController;
        bookIndex = getArguments().getInt("index");

        View view = inflater.inflate(R.layout.fragment_edit_book, container, false);

        bookname = (EditText)view.findViewById(R.id.BookName);
        bookname.setText(journalController.GetBookName(bookIndex));

        (view.findViewById(R.id.Save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                journalController.SetBookName(bookIndex, bookname.getText().toString());
                dismiss();
            }
        });
        return view;
    }
}

