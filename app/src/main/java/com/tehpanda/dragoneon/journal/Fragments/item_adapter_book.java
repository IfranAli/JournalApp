package com.tehpanda.dragoneon.journal.Fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tehpanda.dragoneon.journal.Controller.IJournalController;
import com.tehpanda.dragoneon.journal.Model.Book;
import com.tehpanda.dragoneon.journal.R;

public class item_adapter_book extends ArrayAdapter<Book>{
    private IJournalController journalController;
    // Constructor
    public item_adapter_book(Context context, IJournalController jcontroller){
        super(context, R.layout.item_layout_book, jcontroller.GetBooks());
        this.journalController = jcontroller;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null){
            LayoutInflater lf;
            lf = LayoutInflater.from(getContext());
            view = lf.inflate(R.layout.item_layout_book, parent, false);
        }
        Book book = journalController.getBook(position);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(book.getBookName());

        ((TextView) view.findViewById(R.id.book_entries)).setText(book.getTotalNotes() + " Logs");
        return view;
    }
}