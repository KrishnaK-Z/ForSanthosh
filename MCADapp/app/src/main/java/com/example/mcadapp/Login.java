package com.example.mcadapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.android.volley.VolleyLog.TAG;

import com.android.volley.VolleyError;
import com.example.mcadapp.Utils.Config;
import com.example.mcadapp.Utils.IResult;
import com.example.mcadapp.Utils.VolleyService;

import org.json.JSONObject;

import java.io.FileOutputStream;

public class Login extends AppCompatActivity {

    protected Button loginBtn,signupBtn;
    protected static Intent SignupIntent,mainPage;
    protected EditText username,password;

    //define VolleyService
    public static IResult mResultCallback = null;
    public static VolleyService mVolleyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        SignupIntent = new Intent(this,SignupActivity.class);
        loginBtn  = (Button) findViewById(R.id.button2);
        signupBtn = (Button) findViewById(R.id.button);
        username  = (EditText) findViewById(R.id.login_mobile);
        password  = (EditText) findViewById(R.id.login_password);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _username = username.getText().toString().trim();
                String _password = password.getText().toString().trim();
                login(_username,_password,getApplicationContext());
                //Toast.makeText(getApplicationContext(),username.getText().toString()+password.getText().toString(),Toast.LENGTH_LONG).show();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SignupIntent);
            }
        });
    }



    public static void login(String username, String password, Context context){



        Toast.makeText(context,"Logging as "+username,Toast.LENGTH_SHORT).show();
        String Url = Config.HOST+"login";
        JSONObject params = new JSONObject();
        try{
            params.put("username",username);
            params.put("password",password);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        //define callback functions
        Login.initVolleyCallback(username,password,params,context);
        mVolleyService = new VolleyService(mResultCallback,context);
        mVolleyService.postDataVolley(Config.POSTTYPE,Url,params);

    }

    //used by login
    public  static void initVolleyCallback(final String username, final String password,final JSONObject params,final Context context){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);
                try{
                    String isSuccess = response.getString("isSuccess");
                    String msg       = response.getString("message");
                    Boolean flag     = Boolean.parseBoolean(isSuccess);
                    if(flag){
                        String name      = response.getString("name");
                        Toast.makeText(context,"Logged as "+name,Toast.LENGTH_SHORT).show();
                        String filename = "login";
                        try {
                            params.put("name",name);

                            FileOutputStream outputstream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                            outputstream.write(params.toString().getBytes());
                            outputstream.close();

                            SharedPreferences sp = context.getSharedPreferences("login",Context.MODE_PRIVATE);
                            SharedPreferences.Editor Ed=sp.edit();
                            Ed.putString("isLoggedin","true" );
                            Ed.putString("name",name);
                            Ed.putString("username",username);
                            Ed.putString("password",password);
                            Ed.apply();

                            mainPage = new Intent(context.getApplicationContext(),MainActivity.class);
                            mainPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(mainPage);

                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    else{
                        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                    }
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
