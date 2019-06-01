package com.example.mcadapp.WifiConnector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A BroadcastReceiver that notifies of important wifi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private Channel channel;
    private WifiConnector activity;
    private String TAG = WiFiDirectBroadcastReceiver.class.getName();

    /**
     * @param manager WifiP2pManager system service
     * @param channel Wifi p2p channel
     * @param _activity activity associated with the receiver
     */
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
                                       WifiConnector _activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        activity=_activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            // UI update to indicate wifi p2p status.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
                activity.wifiSwitch.setText("ON");
                activity.wifiSwitch.setChecked(true);
                activity.dicoverWifiPeers();

            } else {

                activity.wifiSwitch.setText("OFF");
                activity.wifiSwitch.setChecked(false);
            }
            Log.d(TAG, "P2P state changed - " + state);

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if(manager != null){
                manager.requestPeers(channel,activity.peerListListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if(manager == null){
                return;
            }

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if(networkInfo.isConnected()){
                manager.requestConnectionInfo(channel,activity.connectionInfoListener);

            }
            else {
                Toast.makeText(activity.getApplicationContext(),"Disconnected",Toast.LENGTH_SHORT).show();
            }


        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

        }
    }


    public class ServerClass extends Thread{

        private ServerSocket serverSocket;
        private Socket socket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(8888);
                socket       = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ClientClass extends Thread{

        private Socket socket;
        private String hostAddr;

        public ClientClass(InetAddress hostAddress){
            hostAddr = hostAddress.getHostAddress();
            socket = new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAddr,8888),500);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
