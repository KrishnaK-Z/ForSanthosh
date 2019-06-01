package com.example.mcadapp.Mcad;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.mcadapp.R;

import java.util.ArrayList;
import java.util.List;

public class ListenActivity extends AppCompatActivity {

    SensorManager sensorManager;
    Sensor acceleroMeter,gyroscopeMeter;
    TextView[] acceleroMeterReadings = new TextView[3];
    TextView[] gyroscopeMeterReadings = new TextView[3];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);
        sensorManager  = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acceleroMeter  = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeMeter = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(acclerometerSensorEventListener,acceleroMeter,1);
        sensorManager.registerListener(gyroscopemeterSensorEventListener,gyroscopeMeter,1);

        acceleroMeterReadings[0] = (TextView) findViewById(R.id.acc_x);
        acceleroMeterReadings[1] = (TextView) findViewById(R.id.acc_y);
        acceleroMeterReadings[2] = (TextView) findViewById(R.id.acc_z);

        gyroscopeMeterReadings[0] = (TextView) findViewById(R.id.gyro_x);
        gyroscopeMeterReadings[1] = (TextView) findViewById(R.id.gyro_y);
        gyroscopeMeterReadings[2] = (TextView) findViewById(R.id.gyro_z);

    }

    SensorEventListener acclerometerSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            acceleroMeterReadings[0].setText(""+event.values[0]);
            acceleroMeterReadings[1].setText(""+event.values[1]);
            acceleroMeterReadings[2].setText(""+event.values[2]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    SensorEventListener gyroscopemeterSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            gyroscopeMeterReadings[0].setText(""+event.values[0]);
            gyroscopeMeterReadings[1].setText(""+event.values[1]);
            gyroscopeMeterReadings[2].setText(""+event.values[2]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

}
