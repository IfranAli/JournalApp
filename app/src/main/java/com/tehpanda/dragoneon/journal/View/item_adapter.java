package com.tehpanda.dragoneon.journal.View;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tehpanda.dragoneon.journal.Controller.IJournalController;
import com.tehpanda.dragoneon.journal.Controller.JournalController;
import com.tehpanda.dragoneon.journal.Model.IJournalEntry;
import com.tehpanda.dragoneon.journal.Model.Note;
import com.tehpanda.dragoneon.journal.R;

import java.util.List;

/**
 * Created by dragoneon on 13/07/14.
 */
class item_adapter extends ArrayAdapter<Note> {
    private static int[] colors;
    private int currentBook;
    private IJournalController _JournalController;
    private int layout;// = R.layout.item_layout_card;

    public item_adapter(int currentBook, Context context, IJournalController _JournalController, int layout) {
        super(context , layout, _JournalController.getListOfEntries(currentBook));
        this.layout = layout;
        this.currentBook = currentBook;
        this._JournalController = _JournalController;
        colors = new int[] {context.getResources().getColor(R.color.yellow), context.getResources().getColor(R.color.blue),
                context.getResources().getColor(R.color.pink), context.getResources().getColor(R.color.orange),
                context.getResources().getColor(R.color.purple), context.getResources().getColor(R.color.green),
                context.getResources().getColor(R.color.cyan)};
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if(itemView == null){
            LayoutInflater lf;
            lf = LayoutInflater.from(getContext());
            itemView = lf.inflate(layout, parent, false);
        }
        //Find entry
        IJournalEntry currentEntry = _JournalController.getEntry(currentBook, position);

        //Colours
        int colour = colors[currentEntry.GetDayColor()];
        ((GradientDrawable)itemView.findViewById(R.id.Graphics_DateBox).getBackground()).setColor(colour);

        TextView day = (TextView) itemView.findViewById(R.id.item_day);
        day.setText(currentEntry.GetDay());

        TextView month = (TextView) itemView.findViewById(R.id.item_month);
        month.setText(currentEntry.GetMonth());

        TextView titleView = (TextView) itemView.findViewById(R.id.title);
        titleView.setText(currentEntry.GetTitlePreview());

        TextView preview = (TextView) itemView.findViewById(R.id.item_preview);
        preview.setText(currentEntry.GetDataPreview());

        ((TextView) itemView.findViewById(R.id.time_view)).setText(currentEntry.GetTime());
        return itemView;
    }

    public void setCurrentBook(int currentBook){
        this.currentBook = currentBook;
    }
}

class Homepage_item_adapter extends ArrayAdapter<Note> {
    private static int[] colors;
    private int currentBook;
    private IJournalController _JournalController;
    private int layout;// = R.layout.item_layout_card;

    List<Note> itemsource;

    public Homepage_item_adapter(int currentBook, Context context, List<Note> _source, int layout) {
        super(context , layout, _source);
        itemsource = _source;
        this.layout = layout;
        this.currentBook = currentBook;
        this._JournalController = JournalController.GetInstance();
        colors = new int[] {context.getResources().getColor(R.color.yellow), context.getResources().getColor(R.color.blue),
                context.getResources().getColor(R.color.pink), context.getResources().getColor(R.color.orange),
                context.getResources().getColor(R.color.purple), context.getResources().getColor(R.color.green),
                context.getResources().getColor(R.color.cyan)};
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if(itemView == null){
            LayoutInflater lf;
            lf = LayoutInflater.from(getContext());
            itemView = lf.inflate(layout, parent, false);
        }

        //Find entry
        IJournalEntry currentEntry = itemsource.get(position);
        //IJournalEntry currentEntry = _JournalController.getEntry(currentBook, position);

        //Colours
        int colour = colors[currentEntry.GetDayColor()];
        ((GradientDrawable)itemView.findViewById(R.id.Graphics_DateBox).getBackground()).setColor(colour);

        TextView day = (TextView) itemView.findViewById(R.id.item_day);
        day.setText(currentEntry.GetDay());

        TextView month = (TextView) itemView.findViewById(R.id.item_month);
        month.setText(currentEntry.GetMonth());

        TextView titleView = (TextView) itemView.findViewById(R.id.title);
        titleView.setText(currentEntry.GetTitlePreview());

        TextView preview = (TextView) itemView.findViewById(R.id.item_preview);
        preview.setText(currentEntry.GetDataPreview());

        ((TextView) itemView.findViewById(R.id.time_view)).setText(currentEntry.GetTime());
        return itemView;
    }

    public void setCurrentBook(int currentBook){
        this.currentBook = currentBook;
    }
}