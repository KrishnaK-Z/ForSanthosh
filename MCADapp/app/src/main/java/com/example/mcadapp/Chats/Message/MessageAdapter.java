package com.example.mcadapp.Chats.Message;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mcadapp.R;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter  extends BaseAdapter {
    List<Message> messages = new ArrayList<Message>();
    Context context;
    String TAG = MessageAdapter.class.getName().toString();

    public MessageAdapter(Context context) {
        this.context = context;
    }

    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(i);

        if (message.isBelongsToCurrentUser()) { // this message was sent by us so let's create a basic chat bubble on the right
            convertView = messageInflater.inflate(R.layout.my_message, null);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            holder._time       = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(holder);
            holder.messageBody.setText(message.getText());
            holder._time.setText(message.getMemberData().getTime());

        } else { // this message was sent by someone else so let's create an advanced chat bubble on the left
            convertView = messageInflater.inflate(R.layout.their_message, null);
            holder.avatar = (View) convertView.findViewById(R.id.avatar);
//            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            holder._time       = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(holder);

//            holder.name.setText(message.getMemberData().getName());
            holder.messageBody.setText(message.getText());
            holder._time.setText(message.getMemberData().getTime());

            GradientDrawable drawable = (GradientDrawable) holder.avatar.getBackground();
            drawable.setColor(Color.parseColor(message.getMemberData().getColor()));
        }
        return convertView;
    }


    class MessageViewHolder {
        public View avatar;
        public TextView name;
        public TextView messageBody;
        public TextView _time;
    }
}
