package com.example.mcadapp.Chats.Contacts;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.mcadapp.Utils.Config;
import com.example.mcadapp.Utils.FetchUserDetails;
import com.example.mcadapp.Utils.IResult;
import com.example.mcadapp.Utils.VolleyService;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class AddContact {
    //define VolleyService
    public IResult mResultCallback = null;
    public VolleyService mVolleyService;

    public int addContact(Context context,String contactno){
        String Url = Config.HOST+"add_contact";
        JSONObject params = new JSONObject();
        FetchUserDetails user = new FetchUserDetails(context);
        try {
            params.put("user",user.getUsername());
            params.put("password",user.getPassword());
            params.put("person",contactno);

            this.initVolleyCallback(context,contactno);
            mVolleyService = new VolleyService(mResultCallback,context);
            mVolleyService.postDataVolley(Config.POSTTYPE,Url,params);

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return  1;
    }
    //used by add contacts
    public void initVolleyCallback(final Context context, final String mobileno){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);
                try {
                    String msg  = response.getString("message");
                    String name = response.getString("name");
                    Map contact = new HashMap<>();
                    contact.put("name",name);
                    contact.put("mobileno",mobileno);
                    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
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
