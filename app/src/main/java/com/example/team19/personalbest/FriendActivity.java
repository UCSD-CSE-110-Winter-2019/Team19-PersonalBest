package com.example.team19.personalbest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class FriendActivity extends AppCompatActivity {
    
    public static final int PENDING_FROM = -1;
    public static final int PENDING_FOR = -2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        Button addFriend = (Button) findViewById(R.id.add_friend_btn);
        final EditText addFriendText = (EditText) findViewById(R.id.add_friend_editText);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_of_friend = addFriendText.getText().toString();
                Cloud.getUser(email_of_friend, new CloudCallback() {
                    public void onData(DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String friend_id = snapshot.getKey();
                            class MyBool{
                                boolean b;
                            }
                            final MyBool acceptInstead = new MyBool();
                            acceptInstead.b = false;
                            Cloud.get("Friends", friend_id, new CloudCallback() {
                                public void onData(DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        if (snapshot.getValue(Integer.class).equals(PENDING_FOR)){
                                            acceptInstead.b = true;
                                        }
                                    }
                                }
                            });
                            
                            if (acceptInstead.b){
                                Cloud.acceptFriend(Cloud.mUser.getUid(), friend_id);
                            }
                            else{
                                Cloud.addFriend(Cloud.mUser.getUid(), friend_id);
                            }
                        }
                        else{
                            Toast.makeText(FriendActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        populateLayout();
    }

   private void populateLayout() {
       final ArrayList<String> friend_ids = new ArrayList<>();
       final ArrayList<Boolean> pending = new ArrayList<>();
       Cloud.getList("Friends", new CloudCallback() {
           public void onData(DataSnapshot snapshot) {
               for (DataSnapshot child : snapshot.getChildren()) {
                   friend_ids.add(child.getKey());
                   pending.add(Boolean.valueOf(child.getValue(Integer.class).equals(1)));
               }
           }
       });
       final ArrayList<String> emails = new ArrayList<>();
       Cloud.getList("Friends", new CloudCallback() {
           public void onData(DataSnapshot snapshot) {
               emails.add(snapshot.child("Email").getValue(String.class));
           }
       });
       ScrollView scrollView = (ScrollView)findViewById(R.id.friend_scroll_view);
       for (String email : emails){
           ViewGroup view = new ViewGroup(this); // doesn't work, viewgroup is abstract
           view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
           TextView textView = new TextView(this);
           textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
           textView.setText(email);
           textView.setTextSize(30);
           Button stats = new Button(this);
           stats.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
           stats.setText("Stats");
           view.addView(textView);
           view.addView(stats);
           scrollView.addView(view);

       }
   }

}
