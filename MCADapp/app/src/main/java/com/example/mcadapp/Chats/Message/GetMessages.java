package com.example.mcadapp.Chats.Message;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.mcadapp.Utils.Config;
import com.example.mcadapp.Utils.FetchUserDetails;
import com.example.mcadapp.Utils.IResult;
import com.example.mcadapp.Utils.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetMessages {

    private Message message;
    private MessageAdapter messageAdapter;
    private Context context;
    private FetchUserDetails userDetails;
    private String TAG = GetMessages.class.getName().toString();
    private String person;
    private String color;
    private ListView messageView;

    //define VolleyService
    protected IResult mResultCallback = null;
    protected VolleyService mVolleyService;

    public GetMessages(ListView _messageView, MessageAdapter _messageAdapter, Context _context, String _person, String _color){
        this.initVolleyCallback();
        this.messageView    = _messageView;
        this.messageAdapter = _messageAdapter;
        this.context        = _context;
        this.userDetails    = new FetchUserDetails(_context);
        this.person         = _person;
        this.color          = _color;
        mVolleyService      = new VolleyService(mResultCallback,_context);
        getMessages();
    }


    public void getMessages(){
        FetchUserDetails userDetails = new FetchUserDetails(this.context);
        JSONObject params = new JSONObject();
        String URL        = Config.HOST + "/get_messages";
        try {
            params.put("user",userDetails.getUsername());
            params.put("password", userDetails.getPassword());
            params.put("person",this.person);

            mVolleyService.postDataVolley(Config.POSTTYPE,URL,params);

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
                        JSONArray data = (JSONArray) response.get("data");
                        for(int i=0;i<data.length();i++){
                            JSONObject obj = (JSONObject) data.get(i);
                            try{
                                Boolean belongsToCurrentUser = (Boolean) obj.getBoolean("belongsToCurrentUser");
                                String msg = (String) obj.getString("message");
                                String _time_ = (String) obj.getString("time");

                                MemberData memberData = new MemberData(person,color,_time_);
                                Message message       = new Message(msg.toString(),memberData,belongsToCurrentUser);
                                messageAdapter.add(message);
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        // scroll the ListView to the last added element
                        messageView.setSelection(messageAdapter.getCount()-1);
                    }
                    else {
                        Toast.makeText(context,"error in getting message",Toast.LENGTH_SHORT).show();
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
