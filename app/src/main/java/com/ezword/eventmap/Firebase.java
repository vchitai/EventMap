package com.ezword.eventmap;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by chita on 13/05/2018.
 */

public class Firebase {
    private static final Firebase ourInstance = new Firebase();
    private static FirebaseDatabase mDatabase = null;

    public static Firebase getInstance() {
        return ourInstance;
    }

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }
    private Firebase() {
    }
}
