package com.example.team19.personalbest;

import android.support.annotation.NonNull;
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
    public static FirebaseUser mUser;

    private static boolean isUserReady() {
        return mUser != null;
    }

    public static void getList(String namespace, final CloudCallback cc){
        if (!isUserReady()) return;

        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = mDb.child(USERS).child(mUser.getUid()).child(namespace);

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
    public static void declineFriend(final String senderID, final String sendeeID){
        if (!isUserReady()) return;

        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = mDb.child(USERS).child(senderID).child("Friends").child(sendeeID);
        myRef.setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Write value successful for " + senderID + "/" + sendeeID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Failed to write value.", e);
                    }
                });
        myRef = mDb.child(USERS).child(sendeeID).child("Friends").child(senderID);
        myRef.setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Write value successful for " + sendeeID + "/" + senderID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Failed to write value.", e);
                    }
                });
    }

    // TODO: Replace the set values "0" with the id of the messages
    public static void acceptFriend(final String senderID, final String sendeeID){
        if (!isUserReady()) return;

        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = mDb.child(USERS).child(senderID).child("Friends").child(sendeeID);
        myRef.setValue(0)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Write value successful for " + senderID + "/" + sendeeID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Failed to write value.", e);
                    }
                });
        myRef = mDb.child(USERS).child(sendeeID).child("Friends").child(senderID);
        myRef.setValue(0)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Write value successful for " + sendeeID + "/" + senderID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Failed to write value.", e);
                    }
                });
    }

    // chat id means mutual friends, -1 means sent from user, -2 means pending for user.
    public static void addFriend(final String senderID, final String sendeeID){
        if (!isUserReady()) return;

        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = mDb.child(USERS).child(senderID).child("Friends").child(sendeeID);
        myRef.setValue(FriendActivity.PENDING_FROM)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Write value successful for " + senderID + "/" + sendeeID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Failed to write value.", e);
                    }
                });
        myRef = mDb.child(USERS).child(sendeeID).child("Friends").child(senderID);
        myRef.setValue(FriendActivity.PENDING_FOR)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Write value successful for " + sendeeID + "/" + senderID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Failed to write value.", e);
                    }
                });
    }

    public static void getOtherUser(String userID, final CloudCallback cc){
        if (!isUserReady()) return;

        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = mDb.child(USERS).child(mUser.getUid());

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

    public static void getUser(String email, final CloudCallback cc){
        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
        mDb.child(USERS).orderByChild("Email").equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { 
                cc.onData(dataSnapshot);
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
