package com.example.mcadapp.Chats.Message;

import android.content.Context;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.mcadapp.SignupActivity;
import com.example.mcadapp.Utils.Config;
import com.example.mcadapp.Utils.FetchUserDetails;
import com.example.mcadapp.Utils.IResult;
import com.example.mcadapp.Utils.VolleyService;

import org.json.JSONException;
import org.json.JSONObject;

public class SendMessage {
    private Message message;
    private MessageAdapter messageAdapter;
    private ListView messageView;
    private Context context;
    private FetchUserDetails userDetails;
    private String TAG = SendMessage.class.getName().toString();

    //define VolleyService
    protected IResult mResultCallback = null;
    protected VolleyService mVolleyService;

    public SendMessage(Message _message,ListView _messageView,MessageAdapter _messageAdapter, Context _context){
        this.initVolleyCallback();

        this.message        = _message;
        this.messageView    = _messageView;
        this.messageAdapter = _messageAdapter;
        this.context        = _context;
        this.userDetails    = new FetchUserDetails(_context);
        mVolleyService      = new VolleyService(mResultCallback,_context);

    }

    public void send(String person){
        String username = userDetails.getUsername();
        String password = userDetails.getPassword();
        String URI      = Config.HOST+"/send_message";
        JSONObject params  = new JSONObject();
        try {
            params.put("user",username);
            params.put("password",password);
            params.put("person",person);
            params.put("message",this.message.getText());

            mVolleyService.postDataVolley(Config.POSTTYPE,URI,params);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected  void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response.toString());
                try{
                    String isSuccess = response.getString("isSuccess");
                    Boolean flag     = Boolean.parseBoolean(isSuccess);
                    if(flag){
                        String _time = response.getString("time");
                        message.getMemberData().setTime(_time);
                        messageAdapter.add(message);
                        messageView.setSelection(messageAdapter.getCount()-1);
                    }
                    else {
                        Toast.makeText(context,"error in sending message",Toast.LENGTH_SHORT).show();
                    }

                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");
            }
        };
    }


}
