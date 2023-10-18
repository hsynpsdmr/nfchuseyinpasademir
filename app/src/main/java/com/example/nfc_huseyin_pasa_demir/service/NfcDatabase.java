package com.example.nfc_huseyin_pasa_demir.service;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Nfc.class}, version = 1, exportSchema = false)
public abstract class NfcDatabase extends RoomDatabase {
    private static final String LOG_TAG = NfcDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "nfc_db";
    private static NfcDatabase sInstance;

    public abstract NfcDao nfcDao();

    public static NfcDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                                NfcDatabase.class, NfcDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }


}
