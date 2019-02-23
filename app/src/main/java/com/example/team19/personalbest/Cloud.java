package com.example.team19.personalbest;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Cloud {
    private static final String USERS = "users";
    private static final String TAG = "CLOUD";
    public static FirebaseUser mUser;

    private static boolean isUserReady() {
        return mUser != null;
    }

    public static void get(String namespace, String key, final CloudCallback cc) {
        if (!isUserReady()) return;

        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = mDb.child(USERS).child(mUser.getUid()).child(namespace).child(key);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    cc.onData(value);
                    Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public static void set(String namespace, String key, Object value) {
        if (!isUserReady()) return;

        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = mDb.child(USERS).child(mUser.getUid()).child(namespace).child(key);
        myRef.setValue(value);
    }
}
