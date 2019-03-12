package com.example.team19.personalbest.Chat;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.team19.personalbest.Cloud;
import com.example.team19.personalbest.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private String user_id;
    private String user_email;
    private Toolbar mChatToolbar;
    private DatabaseReference mRootRef;
    private ImageButton send_btn;
    private EditText message_text;

    private RecyclerView mMessages_list;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        user_id = getIntent().getStringExtra("user_id");
        user_email = getIntent().getStringExtra("user_email");

        mChatToolbar = findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);
        getSupportActionBar().setTitle(user_email);

        mRootRef = FirebaseDatabase.getInstance().getReference();


        send_btn = findViewById(R.id.chat_send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        message_text = findViewById(R.id.chat_message);
        createChat();

        mAdapter = new MessageAdapter(messagesList);

        mMessages_list = findViewById(R.id.messages_list);
        mLinearLayout = new LinearLayoutManager(this);
        mMessages_list.setHasFixedSize(true);
        mMessages_list.setLayoutManager(mLinearLayout);
        mMessages_list.setAdapter(mAdapter);

        loadMessages();

        String input_text = getIntent().getStringExtra("input_text");
        if (input_text != null) {
            message_text.setText(input_text);
            send_btn.performClick();
        }

    }

    /**
     * Create the chat in firebase if not exist
     */
    private void createChat() {
        mRootRef.child("Chat").child(Cloud.mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(user_id)) {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + Cloud.mUser.getUid() + "/" + user_id, chatAddMap);
                    chatUserMap.put("Chat/" + user_id + "/" + Cloud.mUser.getUid(), chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d("CHAT ACTIVITY", databaseError.getMessage());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Adding the message to firebase if the message is not empty
     */
    private void sendMessage() {
        String message = message_text.getText().toString();

        // empty input
        if (message.equals("")) {
            Log.d("CHAT ACTIVITY", "Nothing entered, not sending");
            return;
        }

        String current_user_ref = "messages/" + Cloud.mUser.getUid() + "/" + user_id;
        String chat_user_ref = "messages/" + user_id + "/" + Cloud.mUser.getUid();

        DatabaseReference user_message_push = mRootRef.child("messages").child(Cloud.mUser.getUid())
                .child(user_id);

        long time = System.currentTimeMillis();
        String timeS = String.valueOf(time);

        Map messageMap = new HashMap();
        messageMap.put("message", message);
        messageMap.put("time", time);
        messageMap.put("from", Cloud.mUser.getUid());

        Map messageUsermap = new HashMap();
        messageUsermap.put(current_user_ref + "/" + timeS, messageMap);
        messageUsermap.put(chat_user_ref + "/" + timeS, messageMap);

        message_text.setText("");

        mRootRef.updateChildren(messageUsermap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d("CHAT ACTIVITY", databaseError.getMessage());
                }
            }
        });

        notifyUser();
    }

    private void loadMessages() {
        mRootRef.child("messages").child(Cloud.mUser.getUid()).child(user_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages message = dataSnapshot.getValue(Messages.class);

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void notifyUser() {
        DatabaseReference notificationRef = mRootRef.child("notifications");
        HashMap<String, String> notificationData = new HashMap<>();
        notificationData.put("from", Cloud.mUser.getUid());

        notificationRef.child(user_id).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("CHAT ACTIVITY", "Notification sent to user");
            }
        });
    }
}
