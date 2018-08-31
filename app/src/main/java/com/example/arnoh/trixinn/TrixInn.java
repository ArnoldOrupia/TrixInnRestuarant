package com.example.arnoh.trixinn;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ARNOH on 5/9/2018.
 */

public class TrixInn extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
