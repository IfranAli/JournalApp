package com.tehpanda.dragoneon.journal.View;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;

import com.tehpanda.dragoneon.journal.Controller.IJournalController;
import com.tehpanda.dragoneon.journal.Controller.JournalController;
import com.tehpanda.dragoneon.journal.R;


public class MainActivity extends ActionBarActivity implements Fragment_BookListView.IBookListView {
    public Fragment_NavDrawer fragment_bookView;
    public listViewFragment fragment_listView;
    public EditFragment fragment_editFrag;
    public IJournalController iJournalController = JournalController.GetInstance();

    ViewPager viewPager = null;
    private int pages;

    private Toolbar toolbar;

    public boolean isLandscape(){
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set content View.
        setContentView(R.layout.activity_my);

        Log.e("OnCreate", "MainActivity");
        // Get current Orientation.
        boolean isLandscape = isLandscape();

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        //toolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set up navigation drawer.
        fragment_bookView = (Fragment_NavDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_Drawer);
        fragment_bookView.SetUp((DrawerLayout) findViewById(R.id.layout_main_activity), toolbar);

        // Set ViewPager page count according to current Orientation.
        pages = isLandscape ? 1 : 2;


        // Check if this is first run and if so instantiate fragments.
        // Else we leave them alone to be instaniated by the ViewPager.
        if(savedInstanceState == null) {
            fragment_listView = new listViewFragment();
            fragment_editFrag = new EditFragment();
        }

        // View Pager
        viewPager = (ViewPager) findViewById(R.id.pager);
        FragmentManager fm = getSupportFragmentManager();
        viewPager.setAdapter(new MyAdapter(fm));

        if(savedInstanceState != null){
            // Finds edit fragment.
            String tag = makeFragmentName(viewPager.getId(), 1);
            fragment_editFrag = (EditFragment) getSupportFragmentManager().findFragmentByTag(tag);
            Log.e("EditFragment", "is null: " +String.valueOf(fragment_editFrag == null));
            if(fragment_editFrag == null) {
                fragment_editFrag = new EditFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.editTextLayout, fragment_editFrag, tag).commit();
            } else {
                if(isLandscape()) {
                    getSupportFragmentManager().beginTransaction().remove(fragment_editFrag).commit();
                    getSupportFragmentManager().executePendingTransactions();
                    getSupportFragmentManager().beginTransaction().replace(R.id.editTextLayout, fragment_editFrag, tag).commit();
                } else {
                    Log.e("is Portrait", "EditFrag Exists");
                    getSupportFragmentManager().beginTransaction().remove(fragment_editFrag).commit();
                    getSupportFragmentManager().executePendingTransactions();
                }
            }
        }

        // Applying Last selected theme.
        // fragment_listView.SetTheme(SettingsFragment.ParseThemeIndex(Integer.parseInt(SharedPrefMgr.Read(this, SharedPrefMgr.KEY_LAYOUT_LAST_USED, "0"))) ,true);
    }

    public void weLandscapeNow() {
        // Instantiate Editfragment if we're in Landscape mode because
        // Viewpager is not able to do it itself since we are only calling for 2pages
        // and not three.
        String tag = makeFragmentName(viewPager.getId(), 1);
        if(getSupportFragmentManager().findFragmentByTag(tag) == null) {
            fragment_editFrag = new EditFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.editTextLayout, fragment_editFrag, tag).commit();
            getSupportFragmentManager().executePendingTransactions();
        }
    }

    @Override
    public void onBackPressed() {
        int currentItem = viewPager.getCurrentItem();
        switch (currentItem) {
            case 0:
                super.onBackPressed();
                break;
            case 1:
                viewPager.setCurrentItem(0);
                break;
            case 2:
                fragment_editFrag.SaveChanges();
                viewPager.setCurrentItem(1);
                break;
        }
    }

    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    // Fragment Communication.
    @Override
    public void PostBookSelected() {
        ((Fragment_NavDrawer)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_navigation_Drawer))
                .CloseDrawer();
    }

    class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            Log.e("pager", String.valueOf(position));
            switch (position){
                case 0:
                    fragment = fragment_listView;
                    break;
                case 1:
                    Log.e("EditFrag", "Loading");
                    fragment = fragment_editFrag;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return pages;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object fragment = super.instantiateItem(container, position);
            Log.e("test", "insantiate: " + position);
            switch (position){
                case 0:
                    fragment_listView = (listViewFragment) fragment;
                    break;
                case 1:
                    if(fragment_editFrag == null){
                        Log.e("Instantiate Item", "Getting editfrag");
                        fragment_editFrag = (EditFragment) fragment;
                    }
                    break;
            }
            return fragment;
        }
    }
}

