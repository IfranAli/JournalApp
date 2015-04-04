package com.tehpanda.dragoneon.journal.View;

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.Toast;

import com.tehpanda.dragoneon.journal.Controller.IJournalController;
import com.tehpanda.dragoneon.journal.Controller.JournalController;
import com.tehpanda.dragoneon.journal.Model.Book;
import com.tehpanda.dragoneon.journal.R;
/**
 * Created by dragoneon on 7/09/14.
 */
public class Fragment_BookListView extends Fragment{
    private ArrayAdapter<Book> adapter;
    private IJournalController journalController;
    private MainActivity mainActivity;

    IBookListView mCallback;
    //region ActionBar
    public Fragment_BookListView() {

    }

    // Update Adapter.
//    public void UpdateAdapter(){
//        adapter.notifyDataSetChanged();
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.drawer_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_addBook:
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                Fragment prev = getFragmentManager().findFragmentByTag("dialogNewBook");
//                if (prev != null) {
//                    ft.remove(prev);
//                }
//                ft.addToBackStack(null);
//
//                // Create and show the dialog.
//                fragment_newbook newFragment = new fragment_newbook();
//                newFragment.setTargetFragment(this, 1);
//                newFragment.show(ft, "dialogNewBook");
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //endregion

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (IBookListView) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));
        if(savedInstanceState != null){
            //mFromInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        journalController = JournalController.GetInstance();
        //mCallback = (IBookListView);

        mainActivity = ((MainActivity)getActivity());
        View view = inflater.inflate(R.layout.fragment_booklistview, container, false);

        adapter = new item_adapter_book(getActivity().getApplicationContext(), journalController);
        ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onItemClick);
        // Context Menu.
        registerForContextMenu(listView);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> arg0, View v, int pos, long arg3) {
            if(mainActivity.fragment_listView == null)
                Log.e("TEST", "ListView is non-exist");
            mainActivity.fragment_listView.LoadBook(pos);
            mainActivity.viewPager.setCurrentItem(0, true);
            mCallback.PostBookSelected();
        }
    };

    // Context Menu Overrides.
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add("Edit Book");
        menu.add("Delete Book");
        menu.add("Export .txt");
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.e("context menuBOOKVIEW", "selected item called");
        if(getUserVisibleHint()){
            int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
            if(item.getTitle().equals("Edit Book")){
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                fragment_edit_book newFragment = fragment_edit_book.newInstance(position);
                newFragment.show(ft, "dialog");
            } else if (item.getTitle().equals("Delete Book")){
                journalController.DeleteBook(journalController.getBook(position));

                adapter.notifyDataSetChanged();
            } else if (item.getTitle().equals("Export .txt")) {
                Toast.makeText(getActivity(), journalController.ExportBookAsText(position),Toast.LENGTH_SHORT).show();
            }
            //return true;
            return super.onContextItemSelected(item);
        }
        return false;
    }

    public interface IBookListView {
        public void PostBookSelected();
    }
}