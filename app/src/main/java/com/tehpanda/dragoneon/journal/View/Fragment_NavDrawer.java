package com.tehpanda.dragoneon.journal.View;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.tehpanda.dragoneon.journal.R;

/**
 * Created by dragoneon on 7/09/14.
 */
public class Fragment_NavDrawer extends Fragment{
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    //region ActionBar
    public Fragment_NavDrawer() {

    }

    public void SetUp(DrawerLayout drawerLayout, Toolbar actionBar){
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, actionBar, R.string.Drawer_Open, R.string.Drawer_Close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
/*                if(mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    saveToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, String.valueOf(mUserLearnedDrawer));
                }*/
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public void CloseDrawer(){
        mDrawerLayout.closeDrawers();
    }

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("OnCreate", "listViewBookFragment");
        //mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));
        if(savedInstanceState != null){
            //mFromInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        // Buttons
        view.findViewById(R.id.FL_buttton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialogNewBook");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                fragment_newbook newFragment = new fragment_newbook();
                newFragment.show(ft, "dialogNewBook");
                //Toast.makeText(getActivity(),"doalog",Toast.LENGTH_SHORT).show();
                //adapter.notifyDataSetChanged();
            }
        });
        view.findViewById(R.id.FL_button_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialogSettings");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                // Create and show the dialog.
                SettingsFragment newFragment = new SettingsFragment();
                newFragment.show(ft, "dialogSettings");
                mDrawerLayout.closeDrawers();
            }
        });
        //todo: Fix top and bottom chunk of code. Literally copy pasted...
        //todo: Deprecated since settings button now just opens a new dialog.
        // Set BookList as default if new instance.
        mFmgr = getFragmentManager();
        if( mFmgr.findFragmentById(R.id.BookList_Layout) == null) {
            mFmgr.beginTransaction().replace(R.id.BookList_Layout, new Fragment_BookListView(), BOOK_TAG).commit();
        } else {
            // Else restore previous instance.
            swwitch();
        }
        return view;
    }

    void swwitch(){
        try{
            mFmgr = getFragmentManager();
            Fragment f = mFmgr.findFragmentById(R.id.BookList_Layout);
            switch (f.getTag()){
                case SETTING_TAG :
                    mFmgr.beginTransaction().replace(R.id.BookList_Layout, new SettingsFragment(), SETTING_TAG).commit();
                    break;
                case BOOK_TAG :
                    mFmgr.beginTransaction().replace(R.id.BookList_Layout, new Fragment_BookListView(), BOOK_TAG).commit();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
        }
    }
    final String SETTING_TAG = "SettingsTab";
    final String BOOK_TAG = "BooksTab";
    android.support.v4.app.FragmentManager mFmgr;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //drawerLayout = ((MainActivity)getActivity()).Get_Drawer();
    }
}