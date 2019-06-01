package com.example.mcadapp.Chats;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mcadapp.Chats.Message.GetMessages;
import com.example.mcadapp.Chats.Message.MemberData;
import com.example.mcadapp.Chats.Message.Message;
import com.example.mcadapp.Chats.Message.MessageAdapter;
import com.example.mcadapp.Chats.Message.SendMessage;
import com.example.mcadapp.Chats.Sockets.ReceiveMessageListener;
import com.example.mcadapp.R;

public class ChatActivity extends AppCompatActivity {
    private EditText sendmessage;
    private ImageButton sendBtn ;
    private MessageAdapter messageAdapter;
    private ListView messageView;
    private ReceiveMessageListener receiveMessageListener;
    String person,color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        person = (String) intent.getStringExtra("person");
        color  = (String) intent.getStringExtra("color");
        Toast.makeText(getApplicationContext(),person,Toast.LENGTH_SHORT).show();
        sendmessage = (EditText) findViewById(R.id.sendmessage);
        sendBtn     = (ImageButton) findViewById(R.id.sendmsg_btn);
        messageView = (ListView)    findViewById(R.id.messages_view);
        messageAdapter = new MessageAdapter(getApplicationContext());
        messageView.setAdapter(messageAdapter);
        GetMessages getMessages = new GetMessages(messageView,messageAdapter,getApplicationContext(),person,color);
        receiveMessageListener = new ReceiveMessageListener(getApplication(),this,messageView,messageAdapter,person,color);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message  = sendmessage.getText().toString();
                MemberData data = new MemberData(person,color, "");
                Message   msg   = new Message(message,data,true);
                sendmessage.setText("");
//                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                SendMessage sendMessage = new SendMessage(msg,messageView,messageAdapter,getApplicationContext());
                sendMessage.send(person);
            }
        });
    }


}
