package com.tehpanda.dragoneon.journal.View;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tehpanda.dragoneon.journal.Controller.IJournalController;
import com.tehpanda.dragoneon.journal.Model.JournalEntry;
import com.tehpanda.dragoneon.journal.R;

/**
 * Created by dragoneon on 13/07/14.
 */
public class listViewFragment extends Fragment {;
    private ArrayAdapter<JournalEntry> adapter; // Adapter.
    private MainActivity mainActivity;
    private IJournalController _JournalController;
    private int currentBook;
    private ListView listview;
    private TextView textView;

    public void UpdateAdapter(){
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("index", currentBook);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("OnCreate", "listViewFragment");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    //region ActionBar Items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.listview_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                addItem();
                return true;
            case R.id.SortColor:
                adapter.sort(JournalEntry.ByColor);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.SortDate:
                adapter.sort(JournalEntry.ByDate);
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        Log.e("ListViewF", "On Create View Called");
        mainActivity = ((MainActivity)getActivity());
        _JournalController = mainActivity.iJournalController;

        if(savedInstanceState != null)
            currentBook = savedInstanceState.getInt("index");
        else currentBook = -1;

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        textView = (TextView) view.findViewById(R.id.listViewBlankText);
        listview = (ListView) view.findViewById(R.id.listView);
        listview.setOnItemClickListener(onItemClick);

        listview.setVisibility(View.GONE);

        if(!mainActivity.isLandscape())
            view.findViewById(R.id.editTextLayout).setVisibility(View.GONE);

        // Context Menu
        registerForContextMenu(listview);
        if(currentBook != -1) {
            LoadBook(currentBook);
        }

        Log.e("ListViewF","Book id: " + String.valueOf(currentBook));
        return view;
    }

    public static int layout = R.layout.item_layout_card;
    // Set Item view Theme.
    public void SetTheme(int layout, boolean NoRefresh) {
        this.layout = layout;
        // if no books are loaded, return.
        if(NoRefresh)
            return;
        // refresh layoutview.
        LoadBook(currentBook);
    }
    public void LoadBook(int bookid){
        this.currentBook = bookid;
        adapter = new item_adapter(currentBook, getActivity().getApplicationContext(), _JournalController, layout);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Make visible after a book has been loaded.
        listview.setVisibility(View.VISIBLE);
        // Hide "Load A Book" text.
        textView.setVisibility(View.GONE);
    }

    //region Context Menu
    @Override // Creates Context Menu.
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add("Delete");
        menu.add("Add");
    }

    @Override // Onclick Context Item.
    public boolean onContextItemSelected(MenuItem item) {
        Log.e("context menuLISTVIEW", "selected item called");
        if (getUserVisibleHint()){
            super.onContextItemSelected(item);
            if (item.getTitle() == "Delete"){
                _JournalController.delete(currentBook, ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);
                adapter.notifyDataSetChanged();
            }
            else if(item.getTitle() == "Add") {
                addItem();
            }
            return true;
        }
        return false;
    }
    //endregion

    //region context menu methods.
    void addItem(){
        _JournalController.addItem(currentBook, "", "");
        adapter.notifyDataSetChanged();
    }
    //endregion

    // On list view click.
    private AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> arg0, View v, int pos, long arg3) {
            // Item on listview was clicked so we pass the index number and book number.
            PassData(currentBook, pos);
            adapter.notifyDataSetChanged();
        }
    };

    // This method is called when the user clicks on an note in the list.
    void PassData(int book, int index) {
        if(mainActivity.isLandscape()){
            mainActivity.weLandscapeNow();
        }
        mainActivity.fragment_editFrag.LoadNewEntry(book, index, true);
        mainActivity.viewPager.setCurrentItem(2, true);
    }
}