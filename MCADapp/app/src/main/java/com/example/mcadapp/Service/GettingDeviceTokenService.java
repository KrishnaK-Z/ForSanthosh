package com.example.mcadapp.Service;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

import static com.android.volley.VolleyLog.TAG;

public class GettingDeviceTokenService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        Toast.makeText(this.getApplicationContext(),"Token: " + token,Toast.LENGTH_LONG).show();

        // Instance ID token to your app server.
//        sendRegistrationToServer(token);
    }
}
