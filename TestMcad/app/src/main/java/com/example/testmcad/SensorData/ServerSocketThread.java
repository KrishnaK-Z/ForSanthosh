package com.example.testmcad.SensorData;

import android.annotation.SuppressLint;
import android.util.Log;
import com.example.testmcad.SensorData.Activity.ShareSensorData;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class ServerSocketThread extends Thread {

    private ShareSensorData sensorData;
    private String TAG = "ServerSocketThread";
    private int port;
    public ServerSocketThread(ShareSensorData shareSensorData, int prt) {
        sensorData = shareSensorData;
        port = prt;
    }
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            changeStatus("Connection Initialised \nAddress: "+getLocalIpAddress() + ":"+port);
            changeGoButtonState(false);
            Socket socket = serverSocket.accept();
            changeStatus("Connection Accepted");
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            while (true) {
                String msg = inputStream.readUTF();
                Log.d(TAG,msg);
                processMsg(msg);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeStatus(final String status) {
        sensorData.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sensorData.status.setText(status);
            }
        });
    }

    public  void changeTestMessage(final String testMessage) {
        sensorData.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sensorData.testMessage.setText(testMessage);
            }
        });
    }

    public void changeGoButtonState(final boolean state) {
        sensorData.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sensorData.goBtn.setEnabled(state);
            }
        });
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void processMsg(String message) {
        String msg[] = message.split(":");
        if(msg.length == 2) {
            if(msg[0].equals(ClientSocketThread.ACTIVITY)) {
                startActivityName(msg[1]);
            }
            else if(msg[0].equals(ClientSocketThread.TEST)) {
                this.changeTestMessage(msg[1]);
            } else if(msg[0].equals(ClientSocketThread.STOP)) {
                stopActivity();
            } else {
                Log.d(TAG,"Invalid Message");
            }
        }
        else {
            Log.d(TAG,"Invalid input format");
        }
    }

    public void startActivityName(final String name) {
        sensorData.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sensorData.activityName.setText("Started: "+name);
                sensorData.setActivityNameString(name);
                sensorData.startSensorActivity();
            }
        });
    }

    public void stopActivity() {
        sensorData.runOnUiThread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                String tmp = sensorData.getActivityNameString();
                sensorData.activityName.setText("Stopped: "+tmp);
                sensorData.stopSensorActivity();
            }
        });
    }
}
