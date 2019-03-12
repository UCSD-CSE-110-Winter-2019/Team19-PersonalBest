package com.example.team19.personalbest.Friends;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.team19.personalbest.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class FriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mUsersList;
    private DatabaseReference mDatabase;
    FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mToolbar = findViewById(R.id.users_app_bar);
        mUsersList = findViewById(R.id.users_list);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Personal Best");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        mUsersList.setLayoutManager(new LinearLayoutManager(this));
        mUsersList.setHasFixedSize(false);


    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query = mDatabase.limitToLast(50);
        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(query, Users.class)
                .build();

        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull final Users model) {
                        holder.setEmail(model.getEmail());

                        final String user_id = getRef(position).getKey();

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(FriendsActivity.this, ProfileActivity.class);
                                intent.putExtra("user_id", user_id);
                                intent.putExtra("User", model);
                                startActivity(intent);
                            }
                        });

                        // Log to test that Map was read
                        if (model.getSteps()!=null)
                            if (model.getSteps().get("09-03-2019") != null)
                                Log.d("Step????", model.getSteps().get("09-03-2019").toString());
                    }

                    @NonNull
                    @Override
                    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.users_single, viewGroup, false);
                        Log.d("USER", "CREATE");
                        return new UsersViewHolder(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();
        mUsersList.setAdapter(firebaseRecyclerAdapter);
        Log.d("USER", "Start");
    }

    @Override
    protected void onStop() {
        firebaseRecyclerAdapter.stopListening();
        super.onStop();
    }
    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setEmail(String email) {
            TextView emailView = mView.findViewById(R.id.single_email);
            emailView.setText(email);
        }
    }
}
