package com.example.mcadapp.Chats.Sockets;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mcadapp.Chats.Message.MemberData;
import com.example.mcadapp.Chats.Message.Message;
import com.example.mcadapp.Chats.Message.MessageAdapter;
import com.example.mcadapp.Utils.FetchUserDetails;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

public class ReceiveMessageListener {

    private Context context;
    private Socket serverSocket;
    private ListView messageView;
    private MessageAdapter messageAdapter;
    private Activity activity;
    private String TAG = ReceiveMessageListener.class.getName().toString();
    private String person,color;
    private FetchUserDetails userDetails;

    public ReceiveMessageListener(Context _context, Activity _activity, ListView _messageView, MessageAdapter _messageAdapter, String _person,  String _color){
        this.context = _context;
        this.activity = _activity;
        this.messageView    = _messageView;
        this.messageAdapter = _messageAdapter;
        this.person         = _person;
        this.color          = _color;
        userDetails = new FetchUserDetails(context);
        WebSocket webSocket = new WebSocket();
        serverSocket = webSocket.getWebsocket();
        serverSocket.connect();
        serverSocket.on("onNewMessage",onNewMessage);
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {

                        String user = data.getString("user");
                        String password = data.getString("password");


                        String _person  = data.getString("person");
                        String message = data.getString("message");
                        String time    = data.getString("time");
                        if(userDetails.verifyUser(user,password) && person.equals(_person)){
                            MemberData memberData = new MemberData(person,color,time);
                            Message msg = new Message(message,memberData,false);
                            messageAdapter.add(msg);
                            messageView.setSelection(messageAdapter.getCount()-1);
                            Log.d(TAG,"satisfied");
                        }
                        Log.d(TAG,data.toString()+userDetails.verifyUser(user,password)+ person +_person);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
        }
    };


}
