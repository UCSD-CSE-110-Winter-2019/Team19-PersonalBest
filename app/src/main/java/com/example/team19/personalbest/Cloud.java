package com.example.team19.personalbest;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Cloud {
    private FirebaseDatabase mDb;
    private String mEmail = "";

    public Cloud(String email) {
        mDb = FirebaseDatabase.getInstance();
        mEmail = email;

        if (!isValidEmail()) {
            Log.d(Logger.CLOUD_TAG, "Email not provided.");
        }
    }

    public void get(String namespace, String key, final CloudCallback cc) {
        if (!isValidEmail()) return;

        DatabaseReference myRef = mDb.getReference(createPath(namespace, key));

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    cc.onData(value);
                    Log.d(Logger.CLOUD_TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(Logger.CLOUD_TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void set(String namespace, String key, Object value) {
        if (!isValidEmail()) return;
        mDb.getReference(createPath(namespace, key)).setValue(value);
    }

    private String createPath(String namespace, String key) {
        return mEmail + "/" + namespace + "/" + key;
    }

    private boolean isValidEmail() {
        return !mEmail.equals("");
    }
}
