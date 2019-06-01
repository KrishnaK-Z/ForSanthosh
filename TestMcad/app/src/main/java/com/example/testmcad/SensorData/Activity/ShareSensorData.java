package com.example.testmcad.SensorData.Activity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.testmcad.R;
import com.example.testmcad.SensorData.ServerSocketThread;
import com.example.testmcad.Utils.IResult;
import com.example.testmcad.Utils.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.Date;

import static android.util.Half.EPSILON;

public class ShareSensorData extends AppCompatActivity {


    public static String TAG = "ShareSensorData";
    EditText portNo;
    public Button   goBtn;
    public TextView status,testMessage,activityName;
    ServerSocketThread serverSocketThread;
    private SensorManager sensorManager;
    private Sensor gyroSensor, accSensor;
    private boolean isGyro,isAccle;
    FileWriter accFileWritter, gyroFileWritter;

    float[][] data = new float[120][3];
    int data_length = 0;

    //define VolleyService
    public static IResult mResultCallback = null;
    public static VolleyService mVolleyService;


    public String activityNameString,accFileName,gyroFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_sensor_data);
        portNo = (EditText) findViewById(R.id.port);
        goBtn  = (Button)   findViewById(R.id.go_btn);
        status = (TextView) findViewById(R.id.status);
        testMessage = (TextView) findViewById(R.id.test_message);
        activityName = (TextView) findViewById(R.id.activity_name);
        goBtn.setOnClickListener(goBtnListener);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor     = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor    = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(accSensor != null) {
            isAccle = true;
        } else {
            Toast.makeText(getApplicationContext(),"AcceleratorMeter is Not Available",Toast.LENGTH_SHORT).show();
        }

        if(gyroSensor !=null){
            isGyro = true;
        } else {
            Toast.makeText(getApplicationContext(),"GyroScopeMeter is Not Available",Toast.LENGTH_SHORT).show();
        }
//        startSensorActivity();
    }

    View.OnClickListener goBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                int port = Integer.parseInt(portNo.getText().toString());
                if(checkPort(port)) {
                    serverSocketThread = new ServerSocketThread(ShareSensorData.this,port);
                    serverSocketThread.start();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Invalid Port No", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Invalid Port No", Toast.LENGTH_SHORT).show();
            }

        }
    };

    public boolean checkPort(int port) {
        Log.d(TAG,""+port);
        if(port >= 5000 && port <=9000) {
            return true;
        }
        else {
            return false;
        }
    }

    public void startSensorActivity() {
        Toast.makeText(getApplicationContext(),"Started "+getActivityNameString(),Toast.LENGTH_SHORT).show();
        if (isAccle) {
            accFileName=createFile("ACCELEROMETER",getActivityNameString());
            createACCFileWritter(accFileName);
            if(this.accFileWritter!=null) {
                sensorManager.registerListener(accelerometerSensorListener, accSensor, SensorManager.SENSOR_DELAY_GAME);
            } else {
                Log.d(TAG,"ACC Writer Null");
            }
        }
        if(isGyro) {
            gyroFileName=createFile("GYROSCOPE",getActivityNameString());
            createGyroFileWritter(gyroFileName);
            if(this.gyroFileWritter!=null){
                sensorManager.registerListener(gyroMeterListener,gyroSensor,SensorManager.SENSOR_DELAY_GAME);
            } else {
                Log.d(TAG,"gyro Writer Null");
            }
        }
    }

    public void stopSensorActivity() {
        Toast.makeText(getApplicationContext(),"Stopped "+getActivityNameString(),Toast.LENGTH_SHORT).show();
        if (isAccle) {
            closeAccFileWritter();
            sensorManager.unregisterListener(accelerometerSensorListener);
        }
        if(isGyro) {
            closeGyroFileWritter();
            sensorManager.unregisterListener(gyroMeterListener);
        }
    }

    SensorEventListener accelerometerSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // In this example, alpha is calculated as t / (t + dT),
            // where t is the low-pass filter's time-constant and
            // dT is the event delivery rate.

            final float alpha = (float) 0.8;
            float gravity[] = new float[4];
            float linear_acceleration[] = new float[4];

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];
            data[data_length][0] = linear_acceleration[0];
            data[data_length][1] = linear_acceleration[1];
            data[data_length][2] = linear_acceleration[2];
            data_length++;
            if(data_length == 120) {
                try {
                    sendData();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                data_length = 0;
            }
            writeAccData(accFileName,linear_acceleration[0],linear_acceleration[1],linear_acceleration[2],event.timestamp);
            //            Log.d(TAG, event.timestamp +linear_acceleration[0]+","+linear_acceleration[1]+","+linear_acceleration[2]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    SensorEventListener gyroMeterListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // Axis of the rotation sample, not normalized yet.
            float axisX = event.values[0];
            float axisY = event.values[1];
            float axisZ = event.values[2];

//            // Calculate the angular speed of the sample
//            double omegaMagnitude = Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);
//
//            // Normalize the rotation vector if it's big enough to get the axis
//            // (that is, EPSILON should represent your maximum allowable margin of error)
//            if (omegaMagnitude > EPSILON) {
//                axisX /= omegaMagnitude;
//                axisY /= omegaMagnitude;
//                axisZ /= omegaMagnitude;
//            }
//            //            Log.d(TAG,"GYRO: "+axisX+","+axisY+","+axisZ+event.timestamp);
            writeGyroData(gyroFileName,axisX,axisY,axisZ,event.timestamp);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public String createFile(String type,String activity) {
        File folder = new File(Environment.getExternalStorageDirectory()
                + "/MCAD");
        boolean var = false;
        if (!folder.exists()) {
            var = folder.mkdir();
        }
        Date date = new Date();
        long time = date.getTime();
        Timestamp timestamp = new Timestamp(time);
        final String filename = folder.toString() + "/" + type+"_"+activity+"_"+timestamp+".csv";
        return filename;
    }
    public void createACCFileWritter(String filename){
        try {
            this.accFileWritter = new FileWriter(filename,true);
            this.accFileWritter.append("event,axisX,axisY,axisZ,timestamp\n");

        } catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error in creating file",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    public void createGyroFileWritter(String filename){
        try {
            this.gyroFileWritter = new FileWriter(filename,true);
            this.gyroFileWritter.append("event,axisX,axisY,axisZ,timestamp\n");

        } catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error in creating file",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void writeAccData(String filename,float axisX , float axisY , float axisZ, long timestamp) {
        try {
            String activity = getActivityNameString();
            this.accFileWritter.append(activity+","+axisX+","+axisY+","+axisZ+","+timestamp+"\n");
        } catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error in writing file",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    public void writeGyroData(String filename,float axisX , float axisY , float axisZ, long timestamp) {
        try {
            String activity = getActivityNameString();
            this.gyroFileWritter.append(activity+","+axisX+","+axisY+","+axisZ+","+timestamp+"\n");
        } catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error in writing file",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    public void closeAccFileWritter() {
        try {
            this.accFileWritter.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void closeGyroFileWritter() {
        try {
            this.gyroFileWritter.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getActivityNameString() {
        return activityNameString;
    }

    public void setActivityNameString(String activityNameString) {
        this.activityNameString = activityNameString;
    }

    public void sendData() throws JSONException {
        //define callback functions
        initVolleyCallback();
        mVolleyService = new VolleyService(mResultCallback,getApplicationContext());
        JSONObject params = new JSONObject();
        JSONArray outerJsonArray = new JSONArray();

        for (int i=0; i < data.length; i++) {
            JSONArray innerJsonArray = new JSONArray();

            for (int j=0; j < data[i].length; j++) innerJsonArray.put(data[i][j]);

            outerJsonArray.put(innerJsonArray);
        }
        params.put("data",outerJsonArray);
        mVolleyService.postDataVolley("post","http://192.168.100.200:8000/predict",params);
    }

    //used by login
    public  static void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

            }
            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");
            }
        };
    }
}
