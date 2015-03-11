package com.tehpanda.dragoneon.journal.Model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by panda on 2/15/15.
 */
public class SharedPrefMgr {

    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    public static final String KEY_LAYOUT_LAST_USED = "last_used_layout";
    public static final String PREF_FILE_NAME = "pref";
    public Boolean mUserLearnedDrawer;
    public Boolean mFromInstanceState;

    public static void Write(Context context, String prefName, String prefValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(prefName, prefValue);
        editor.apply();
    }
    public static String Read(Context context, String prefName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getString(prefName, defaultValue);
    }
}
