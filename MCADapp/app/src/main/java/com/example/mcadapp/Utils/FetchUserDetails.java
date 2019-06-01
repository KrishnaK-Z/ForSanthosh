package com.example.mcadapp.Utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class FetchUserDetails {
    protected String userame;
    protected String password;
    protected String name;
    private String TAG = FetchUserDetails.class.getName().toString();

    public FetchUserDetails(Context context){
        try {
            String filename = "login";
            FileInputStream fis = context.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            JSONObject params = new JSONObject(sb.toString());
            String _username   = params.getString("username");
            String _password   = params.getString("password");
            String _name       = params.getString("name");
            fis.close();
            this.userame  = _username;
            this.password = _password;
            this.name     = _name;
        }
        catch (Exception e){
            e.printStackTrace();
            this.userame  = null;
            this.password = null;
        }
    }

    public String getUsername(){
        return this.userame;
    }

    public String getPassword(){
        return this.password;
    }

    public Boolean verifyUser(String user,String pass){
//        Log.d(TAG,userame+user+password+pass+((user.equals(userame)) && (pass.equals("123456"))));
        return user.equals(userame) && pass.equals("123456");
    }

    public String getName(){
        return this.name;
    }
}
