package com.example.mcadapp.Chats.Contacts;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mcadapp.Chats.ChatActivity;
import com.example.mcadapp.R;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class ContactAdapter extends RecyclerView.Adapter <ContactAdapter.ViewHolder> {

    private String TAG = ContactAdapter.class.getName();
    private  List<Map> values;
    private Intent chatIntent;


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public View layout;

        public ViewHolder(View v){
            super(v);
            layout = v;
            username = (TextView) v.findViewById(R.id.contact_username);
        }
    }

    public ContactAdapter(List<Map> usernames){
        values = usernames;
    }

    public void addContact(Map item){
        Log.d(TAG,item.toString());
        values.add(item);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.contact_row,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final Map value = values.get(position);
        final String username = (String) value.get("name");
        final String mobile   = (String) value.get("mobile");
        viewHolder.username.setText(username);
        viewHolder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatIntent = new Intent(v.getContext(), ChatActivity.class);
                chatIntent.putExtra("person",mobile);
                chatIntent.putExtra("color",getRandomColor());
                Toast.makeText(v.getContext(),"Clicked " + username,Toast.LENGTH_SHORT).show();
                v.getContext().startActivity(chatIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    private String getRandomColor() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer("#");
        while(sb.length() < 7){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7);
    }

}