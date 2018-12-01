package com.example.jules.mymovies.util;

import android.content.Context;

import com.example.jules.mymovies.model.DaoMaster;
import com.example.jules.mymovies.model.DaoSession;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseOpenHelper;
import org.greenrobot.greendao.identityscope.IdentityScopeType;

public class FilmsDatabase {

    /**
     * Session to connect to the application
     * local database with GreenDAO.
     */
    private static DaoSession mDaoSession;

    public static final String DB_NAME = "FilmsFavoris";

    public static DaoSession getDaoSession(Context context) {
        if (mDaoSession == null) {
            DatabaseOpenHelper openHelper = new com.example.jules.mymovies.database.DatabaseOpenHelper(context, DB_NAME);
            Database database = openHelper.getWritableDb();
            mDaoSession = new DaoMaster(database).newSession();
        }
        return mDaoSession;
    }
}
