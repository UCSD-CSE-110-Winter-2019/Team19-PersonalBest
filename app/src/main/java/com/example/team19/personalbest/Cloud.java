package com.example.team19.personalbest;

import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Cloud {
    private static final String USERS = "users";
    private static final String TAG = "CLOUD";
    private static FirebaseUser mUser;

    private static boolean isUserReady() {
        return mUser != null;
    }

    public static FirebaseUser getMUser() {
        return mUser;
    }

    public static void setMUser(FirebaseUser u) {
        mUser = u;
    }

    public static void get(String namespace, String key, final CloudCallback cc) {
        if (!isUserReady()) return;

        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = mDb.child(USERS).child(mUser.getUid()).child(namespace).child(key);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        cc.onData(dataSnapshot);
                    }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public static void getAll(String namespace, final CloudCallback cc) {
        if (!isUserReady()) {
            Log.d(TAG, "User is not ready");
            return;
        }

        Log.d(TAG, "User is ready");
        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = mDb.child(USERS).child(mUser.getUid()).child(namespace);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    cc.onData(childSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
    public static void set(final String namespace, final String key, final Object value) {
        if (!isUserReady()) return;

        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = mDb.child(USERS).child(mUser.getUid()).child(namespace).child(key);
        myRef.setValue(value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Write value successful for " + namespace + "/" + key);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Failed to write value.", e);
                    }
                });
    }
}
