package com.example.team19.personalbest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.team19.personalbest.fitness.MainScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    protected Users user;
    protected TextView mEmail;
    protected Button sendRequestButton;
    protected String user_id;
    protected DatabaseReference mFriendRequestDB;
    protected DatabaseReference mFriendDB;
    protected Button view_history_btn;

    protected State current_state;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        view_history_btn = findViewById(R.id.friend_history_btn);
        user_id = getIntent().getStringExtra("user_id");
        user = ( Users) getIntent().getSerializableExtra("User");

        mEmail = findViewById(R.id.profile_email);
        mEmail.setText(user.getEmail());

        mFriendDB = FirebaseDatabase.getInstance().getReference().child("friends");
        mFriendRequestDB = FirebaseDatabase.getInstance().getReference().child("friend_req");
        sendRequestButton = findViewById(R.id.friend_request_btn);

        current_state = State.STRANGER;

        mFriendRequestDB.child(Cloud.mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)) {
                    String request_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                    if (request_type.equals("received")) {
                        sendRequestButton.setEnabled(true);
                        current_state = State.REQUEST_RECEIVED;
                        sendRequestButton.setText(getString(R.string.accept_friend_request));
                    } else if (request_type.equals("sent")) {
                        sendRequestButton.setEnabled(true);
                        current_state = State.REQUEST_SENT;
                        sendRequestButton.setText(R.string.cancel_friend_request);
                    }
                }
                else {
                    mFriendDB.child(Cloud.mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(user_id)) {
                                current_state = State.FRIEND;
                                sendRequestButton.setEnabled(false);
                                view_history_btn.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendRequestButton.setEnabled(false);

                // ====================== STRANGER STATE ===========================
                if (current_state == State.STRANGER) {
                    sendRequest();
                }

                // ====================== REQUEST SENT STATE =======================
                else if (current_state == State.REQUEST_SENT) {
                    cancelRequest();
                }

                // ======================= REQUEST RECEIVED STATE ==================
                else if (current_state == State.REQUEST_RECEIVED) {
                    acceptRequest();
                }
            }
        });

        Button decline_btn = findViewById(R.id.decline_btn);
        decline_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_state == State.REQUEST_RECEIVED) {
                    sendRequestButton.setEnabled(false);
                    cancelRequest();
                }
            }
        });

        view_history_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHistory();
            }
        });
    }

    /**
     * Accepts the request sent from the user shown on the current profile page
     */
    private void acceptRequest() {
        final String current_date = new SimpleDateFormat("dd-MM-yyyy", Locale.US)
                .format(new Date());

        mFriendDB.child(Cloud.mUser.getUid()).child(user_id).setValue(current_date)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFriendDB.child(user_id).child(Cloud.mUser.getUid()).setValue(current_date)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mFriendRequestDB.child(Cloud.mUser.getUid()).child(user_id).removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        mFriendRequestDB.child(user_id).child(Cloud.mUser.getUid()).removeValue()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        current_state = State.FRIEND;
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    /**
     * Cancels the friend request sent to the user shown on the current profile page
     */
    private void cancelRequest() {
        mFriendRequestDB.child(Cloud.mUser.getUid()).child(user_id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFriendRequestDB.child(user_id).child(Cloud.mUser.getUid()).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        current_state = State.STRANGER;
                                        sendRequestButton.setText(R.string.send_friend_request);
                                    }
                                });
                    }
                });

        sendRequestButton.setEnabled(true);
    }

    /**
     * Sends friend request to the user shown on the current profile page
     */
    private void sendRequest() {
        mFriendRequestDB.child(Cloud.mUser.getUid()).child(user_id).child("request_type")
                .setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            mFriendRequestDB.child(user_id).child(Cloud.mUser.getUid()).child("request_type")
                                    .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    current_state = State.REQUEST_SENT;
                                    sendRequestButton.setText(getString(R.string.cancel_friend_request));

                                    Toast.makeText(ProfileActivity.this,"Request Sent",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            });
                        } else {
                            Log.d("Profile", "Request Failed");
                        }
                    }
                });
        sendRequestButton.setEnabled(true);
    }

    protected enum State {
        FRIEND,
        STRANGER,
        REQUEST_SENT,
        REQUEST_RECEIVED
    }

    private void viewHistory() {
        Intent intent = new Intent(this, FriendHistoryActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}
