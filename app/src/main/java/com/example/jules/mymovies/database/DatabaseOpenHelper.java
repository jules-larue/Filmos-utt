package com.example.jules.mymovies.database;

import android.content.Context;

import com.example.jules.mymovies.model.DaoMaster;

public class DatabaseOpenHelper extends DaoMaster.OpenHelper {

    public DatabaseOpenHelper(Context context, String name) {
        super(context, name);
    }
}
