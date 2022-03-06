package com.example.agrify.LoginPackage;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static SharedPrefManager mInstance;
    private static Context mCtx;
    private static final String SHARED_PREF_NAME = "mysharedpref12";
    private static final String KEY_USER_ID = "userID";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_FIRST_NAME = "userFirstName";
    private static final String KEY_USER_LAST_NAME = "userLastName";
    private static final String KEY_LOCATION = "userLocation";
    private static final String KEY_EMAIL_ADDRESS = "userEmailAddress";
    private static final String KEY_USER_TYPE = "userType";
    private static final String KEY_PROFILE_PIC = "profilePic";
    private static final String KEY_STATUS  =   "status";
    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager (context);
        }
        return mInstance;
    }

    public boolean userLogin(String user_id, String username, String user_first_name, String user_last_name,
                             String location, String email_address, String user_type, String profilePic, String status) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences (SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit ();

        editor.putString (KEY_USER_ID, user_id);
        editor.putString (KEY_USERNAME, username);
        editor.putString (KEY_USER_FIRST_NAME, user_first_name);
        editor.putString (KEY_USER_LAST_NAME, user_last_name);
        editor.putString (KEY_LOCATION, location);
        editor.putString (KEY_EMAIL_ADDRESS, email_address);
        editor.putString (KEY_USER_TYPE, user_type);
        editor.putString (KEY_PROFILE_PIC, profilePic);
        editor.putString (KEY_STATUS, status);
        editor.apply ();

        return true;
    }
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences (SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getString (KEY_USERNAME, null) != null) {
            return true;
        }
        return false;
    }

    public boolean logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences (SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit ();
        editor.clear ();
        editor.apply ();
        return true;
    }
    public String getProfilePic() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences (SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString (KEY_PROFILE_PIC, null);
    }

    public String getUserID() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences (SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString (KEY_USER_ID, null);
    }

    public String getFirstName() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences (SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString (KEY_USER_FIRST_NAME, null);
    }

    public String getLastName() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences (SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString (KEY_USER_LAST_NAME, null);
    }

    public String getEmailAddress() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences (SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString (KEY_EMAIL_ADDRESS, null);
    }

    public String getLocation() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences (SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString (KEY_LOCATION, null);
    }

    public String getUserType() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences (SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString (KEY_USER_TYPE, null);
    }

    public String getStatus(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences (SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_STATUS, null);
    }
}
