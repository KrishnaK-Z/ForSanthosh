package com.example.mcadapp;

import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.mcadapp.Utils.Config;
import com.example.mcadapp.Utils.IResult;
import com.example.mcadapp.Utils.VolleyService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    public static String TAG = "SignupActivity";
    protected EditText name,password,mobile;
    protected Button loginBtn,signupBtn;
    protected Intent loginIntent,mainIntent;

    //define VolleyService
    protected IResult mResultCallback = null;
    protected VolleyService mVolleyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //define callback functions
        this.initVolleyCallback();
        mVolleyService = new VolleyService(mResultCallback,this);

        name  = (EditText) findViewById(R.id.signup_name);
        password  = (EditText) findViewById(R.id.signup_password);
        mobile = (EditText) findViewById(R.id.signup_mobile);

        signupBtn   = (Button) findViewById(R.id.button);
        loginBtn    = (Button) findViewById(R.id.button2);

        loginIntent = new Intent(this,Login.class);
        mainIntent  = new Intent(this,MainActivity.class);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(loginIntent);
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignupActivity.this,"Request Sent",Toast.LENGTH_LONG).show();
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "Error: getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                String token      = task.getResult().getToken();
                                String _name_ = name.getText().toString().trim();
                                String _password_ = password.getText().toString().trim();
                                String _mobile    = mobile.getText().toString().trim();
                                String device_id  = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                                        Settings.Secure.ANDROID_ID);

                                String URL  = Config.HOST+"/add_user";

                                JSONObject params = new JSONObject();
                                try {
                                    params.put("name", _name_);
                                    params.put("password",_password_);
                                    params.put("mobile",_mobile);
                                    params.put("firebase_token",token);
                                    params.put("android_id",device_id);
                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                mVolleyService.postDataVolley(Config.POSTTYPE,URL,params);
                            }
                        });

            }
        });
    }

    protected  void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response.toString());
                try{
                    String msg = response.getString("message");
                    Toast.makeText(SignupActivity.this,msg,Toast.LENGTH_LONG).show();
                    String isSuccess = response.getString("isSuccess");
                    Boolean flag = Boolean.parseBoolean(isSuccess);
                    if(flag){
                        startActivity(mainIntent);
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
