package com.example.mcadapp.WifiConnector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mcadapp.R;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class WifiConnector extends AppCompatActivity {

    Switch wifiSwitch;
    WifiManager wifiManager;

    IntentFilter mIntentFilter;


    WifiP2pManager mManager;
    Channel mChannel;
    WiFiDirectBroadcastReceiver mReceiver;

    ListView wifiDevicesView;
    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;
    public static final int simple_list_item_1=0x01090003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_connector);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        initislizeWifiBroadCastReciver();
        wifiSwitch = (Switch) findViewById(R.id.wifiSwitch);
        wifiSwitch.setOnCheckedChangeListener(wifiSwitchListener);

        wifiDevicesView = (ListView) findViewById(R.id.wifiDeviceList);
        wifiDevicesView.setOnItemClickListener(wifiDevicesListener);
    }

    public void initislizeWifiBroadCastReciver(){

        mManager    = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel    = mManager.initialize(this,getMainLooper(),null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager,mChannel,this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    public ListView.OnItemClickListener wifiDevicesListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final WifiP2pDevice device = deviceArray[position];
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;

            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getApplicationContext(),"Connected to"+device.deviceName,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(getApplicationContext(),"Failed Connect to"+device.deviceName,Toast.LENGTH_SHORT).show();
                }
            });
        }
    };



    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            if(!peerList.getDeviceList().equals(peers)){
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                deviceNameArray = new String[peerList.getDeviceList().size()];
                deviceArray     = new WifiP2pDevice[peerList.getDeviceList().size()];

                int index = 0;

                for (WifiP2pDevice device: peerList.getDeviceList()){

                    deviceNameArray[index] = device.deviceName;
                    deviceArray[index]     = device;
                    index++;
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),simple_list_item_1,deviceNameArray){
                  @Override
                  public View getView(int position, View convertView, ViewGroup parent){
                      TextView textView = (TextView) super.getView(position,convertView,parent);
                      textView.setTextColor(Color.parseColor("#000000"));
                      return  textView;
                  }
                };
                wifiDevicesView.setAdapter(arrayAdapter);
//                wifiDevicesView.setBackgroundColor(Color.parseColor("@color/"));
            }

            if(peerList.getDeviceList().size() == 0){
                Toast.makeText(getApplicationContext(),"No device Available",Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {

            final InetAddress groupOwnerAddress = info.groupOwnerAddress;

            if(info.groupFormed && info.isGroupOwner){
                Toast.makeText(getApplicationContext(),"Created Host",Toast.LENGTH_SHORT).show();
            }
            else if(info.groupFormed){
                Toast.makeText(getApplicationContext(),"Created Created",Toast.LENGTH_SHORT).show();
            }
        }
    };

    protected CompoundButton.OnCheckedChangeListener wifiSwitchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            //enable Wifi
            if(isChecked){
                wifiManager.setWifiEnabled(true);

            }
            else {
                wifiManager.setWifiEnabled(false);
            }
        }
    };

    public void dicoverWifiPeers(){
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(),"Discovery of peers started",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(),"Discovery of peers failed",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver,mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
}
