package com.example.team19.personalbest.Chat;

import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
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
        String current_user_id = Cloud.getMUser().getUid();
        Messages c = mMessageList.get(i);
        String from_id = c.getfrom();

        if (from_id.equals(current_user_id)) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.messageText.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            viewHolder.messageText.setTextColor(Color.WHITE);
            viewHolder.messageText.setLayoutParams(params);
            viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);
        }
        else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.messageText.getLayoutParams();
            params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);
            viewHolder.messageText.setBackgroundColor(Color.WHITE);
            viewHolder.messageText.setTextColor(Color.BLACK);
            viewHolder.messageText.setLayoutParams(params);
        }

        Log.d("Changing message layout", from_id);
        viewHolder.messageText.setText(c.getmessage());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}
