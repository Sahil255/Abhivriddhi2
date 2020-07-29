package com.example.abhivriddi20;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setusename(String username) {
        prefs.edit().putString("username", username).commit();
    }

    public String getusename() {
        String username = prefs.getString("username","unauthorised");
        return username;
    }

    public void logout(){
        //prefs.edit().putString("username",null).commit();
        prefs.edit().remove("username").commit();
    }

}