package com.example.testmcad.SensorData;

import android.os.Handler;

import com.example.testmcad.SensorData.Activity.GetSensorData;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.logging.LogRecord;

public class ClientSocketThread extends Thread {

    public static final String TEST     = "TEST";
    public static final String ACTIVITY = "ACTIVITY";
    public static final String STOP     = "STOP";
    private String address;
    private int port;
    private GetSensorData getSensorData;
    private String TAG = "ClientSocketThread";
    private Socket socket;
    private DataOutputStream dataOutputStream;
    public ClientSocketThread(GetSensorData sensorData, String addr, int prt) {
        getSensorData = sensorData;
        address = addr;
        port = prt;
        socket = null;
        dataOutputStream = null;
    }

    @Override
    public void run(){
        try {
            changeStatus("Connection Initialized \nAddress: "+address+":"+port);
            socket = new Socket(address,port);
            changeStatus("Connected");
            disableConnectBtn();
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            sendTestMessage();
            enableActivityStartBtn();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changeStatus(final String status) {
        getSensorData.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSensorData.status.setText(status);
            }
        });
    }

    private void changeTestMessage(final String testMessage) {
        getSensorData.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSensorData.testMessage.setText(testMessage);
            }
        });
    }

    private String randomNo() {
        Random r = new Random();
        int low  = 1000;
        int high = 9999;
        int result = r.nextInt(high - low) + low;
        return ""+result;
    }

    public void sendMessage(String message) {
        try {
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendTestMessage() {
        String msg = TEST+":"+randomNo();
        changeTestMessage(msg);
        sendMessage(msg);
    }


    public void startActivity(String activityName) {
        activityName =ACTIVITY+":"+activityName;
        sendMessage(activityName);
        disableActivityStartBtn();
        enableActivityStopBtn();
    }

    public void stopActivity() {
        String stop = ClientSocketThread.STOP+":"+ClientSocketThread.STOP;
        sendMessage(stop);
        disableActivityStopBtn();
        enableActivityStartBtn();
    }

    public void disableConnectBtn() {
        getSensorData.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSensorData.connect.setEnabled(false);
            }
        });
    }


    public void enableActivityStartBtn() {
        getSensorData.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSensorData.activityStart.setEnabled(true);
            }
        });
    }

    public void disableActivityStartBtn(){
        getSensorData.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSensorData.activityStart.setEnabled(false);
            }
        });
    }

    public void enableActivityStopBtn() {
        getSensorData.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSensorData.activityStop.setEnabled(true);
            }
        });
    }

    public void disableActivityStopBtn(){
        getSensorData.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSensorData.activityStop.setEnabled(false);
            }
        });
    }


}
