package com.tehpanda.dragoneon.journal.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tehpanda.dragoneon.journal.Controller.IJournalController;
import com.tehpanda.dragoneon.journal.Model.IJournalEntry;
import com.tehpanda.dragoneon.journal.Model.JournalEncodedEntry;
import com.tehpanda.dragoneon.journal.Model.JournalEntry;
import com.tehpanda.dragoneon.journal.Model.Note;
import com.tehpanda.dragoneon.journal.Model.SharedPrefMgr;
import com.tehpanda.dragoneon.journal.R;
import com.tehpanda.dragoneon.journal.View.MainActivity;

public class listViewFragment extends Fragment {;
    private ArrayAdapter<Note> adapter; // Adapter.
    private MainActivity mainActivity;
    private IJournalController _JournalController;
    private int currentBook;
    private ListView listview;
    private LinearLayout textView;

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
        if(currentBook != -1) {
            inflater.inflate(R.menu.listview_actions, menu);
        }
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
                return true;
            case R.id.RebuildBooksXml:
                _JournalController.RebuildBooksXml();
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

        if(savedInstanceState != null) {
            currentBook = savedInstanceState.getInt("index");
        }
        else {
            currentBook = -1;
        }

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        textView = (LinearLayout) view.findViewById(R.id.noBookLoadedLayout);

        listview = (ListView) view.findViewById(R.id.listView);
        listview.setOnItemClickListener(onItemClick);

        listview.setVisibility(View.GONE);

        if(!mainActivity.isLandscape())
            view.findViewById(R.id.editTextLayout).setVisibility(View.GONE);
        if(isBookLoaded()) {
            view.findViewById(R.id.noBookLoadedLayout).setVisibility(View.GONE);
        }

        // Context Menu
        registerForContextMenu(listview);


        if(currentBook != -1) {
            LoadBook(currentBook);
        }

        SetTheme(SettingsFragment.ParseThemeIndex(Integer.parseInt(SharedPrefMgr.Read(getActivity(), SharedPrefMgr.KEY_LAYOUT_LAST_USED, "0"))));

        boolean d = _JournalController.HasBooks();
        if(d) {
            // Show homepage/recent notes.
            getFragmentManager().beginTransaction().replace(R.id.noBookLoadedLayout, new HomePage()).commit();
        } else {
            // Show no books screen.
            getFragmentManager().beginTransaction().replace(R.id.noBookLoadedLayout, new NoBooksLoaded()).commit();
        }

        return view;
    }

    public int layout = R.layout.item_layout_card;
    // Set Item view Theme.
    public void SetTheme(int layout) {
        this.layout = layout;
        // If a book is loaded, then refresh it,
        // to display the new theme.
        if(isBookLoaded())
            LoadBook(currentBook);
    }
    public void LoadBook(int bookid){
        this.currentBook = bookid;
        adapter = new item_adapter(currentBook, getActivity().getApplicationContext(), _JournalController, layout);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listview.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        // Hide "Load A Book" text.
        View v = getActivity().findViewById(R.id.noBookLoadedLayout);
        if(v != null) { // Quick fix for now.
            v.setVisibility(View.GONE);
        }
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
                _JournalController.saveXML(currentBook);
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
        adapter.sort(JournalEntry.ByDate);
        adapter.notifyDataSetChanged();
    }
    //endregion

    // On list view click.
    private AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> arg0, View v, int pos, long arg3) {
            // Item on listview was clicked so we pass the index number and book number.
            IJournalEntry e = _JournalController.getEntry(currentBook, pos);
            if (e.isEncrypted() && ((JournalEncodedEntry)e).decryptionkey == null) {
                displayPasswordPrompt(currentBook, pos);
            }
            PassData(currentBook, pos);
            adapter.notifyDataSetChanged();
        }
    };

    Boolean displayPasswordPrompt(int currentBook ,int pos){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("PasswordDialogue");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Parameters
        PasswordDialogue f = new PasswordDialogue();

        Bundle bundle = new Bundle();
        bundle.putInt("currentBook", currentBook);
        bundle.putInt("entrypos", pos);

        f.setArguments(bundle);
        f.show(ft, "PasswordDialogue");
        return true;
    }

    // This method is called when the user clicks on an note in the list.
    void PassData(int book, int index) {
        if(mainActivity.isLandscape()){
            mainActivity.PostLandscape();
        }
        mainActivity.fragment_editFrag.LoadNewEntry(book, index, true);
        mainActivity.viewPager.setCurrentItem(2, true);
    }

    public boolean isBookLoaded(){
        return currentBook != -1;
    }
}