package com.ezword.eventmap.cores;

import com.google.firebase.database.FirebaseDatabase;

public class FireBase {
    private static final FireBase ourInstance = new FireBase();
    private static FirebaseDatabase mDatabase = null;

    private FireBase() {
    }

    public static FireBase getInstance() {
        return ourInstance;
    }

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }
}
