package com.example.team19.personalbest.Chat;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.team19.personalbest.Cloud;
import com.example.team19.personalbest.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;

    public MessageAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout, parent, false);

        return new MessageViewHolder(v);
    }
    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;

        public MessageViewHolder(View view) {
            super(view);

            messageText = view.findViewById(R.id.message_view);
        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder viewHolder, int i) {
        String current_user_id = Cloud.mUser.getUid();
        Messages c = mMessageList.get(i);
        String from_id = c.getFrom();

        if (from_id.equals(current_user_id)) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.messageText.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            viewHolder.messageText.setLayoutParams(params);
        }
        else {
            viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);
            viewHolder.messageText.setBackgroundColor(Color.WHITE);
            viewHolder.messageText.setTextColor(Color.BLACK);
        }

        viewHolder.messageText.setText(c.getMessage());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}
