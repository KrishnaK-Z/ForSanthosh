package com.example.testmcad.SensorData.Activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testmcad.R;
import com.example.testmcad.SensorData.ClientSocketThread;

public class GetSensorData extends AppCompatActivity {

    public TextView status,testMessage;
    public Button connect, activityStart,activityStop;
    protected EditText address;
    public Spinner activityDropdown;
    ArrayAdapter<CharSequence> adapter;
    ClientSocketThread clientSocketThread;
    protected String activityName = "walking";
    private boolean flag = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_sensor_data);
        status  = (TextView) findViewById(R.id.status);
        testMessage = (TextView) findViewById(R.id.test_message);
        connect = (Button) findViewById(R.id.connect);
        address = (EditText) findViewById(R.id.address);
        connect.setOnClickListener(connectBtnListener);
        activityStart = (Button) findViewById(R.id.activityStart);
        activityStop = (Button) findViewById(R.id.activityStop);
        activityStart.setOnClickListener(activityStartBtnListener);
        activityStop.setOnClickListener(activityStopBtnListener);

        activityDropdown = (Spinner) findViewById(R.id.activity_list);
        adapter = ArrayAdapter.createFromResource(this,R.array.activity_list, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        activityDropdown.setAdapter(adapter);
        activityDropdown.setOnItemSelectedListener(activityListItemSelectListener);
    }

    View.OnClickListener connectBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String addr[] = address.getText().toString().split(":");
            if(addr.length !=2 ) {
                Toast.makeText(getApplicationContext(),"Invalid Ip address", Toast.LENGTH_SHORT).show();
            }
            else{
                int port = Integer.parseInt(addr[1]);
                clientSocketThread = new ClientSocketThread(GetSensorData.this,addr[0],port);
                clientSocketThread.start();
            }
        }
    };

    View.OnClickListener activityStartBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clientSocketThread.startActivity(activityName);
            if(flag){
                startSendingPeriodData();
                flag = false;
            }
        }
    };

    View.OnClickListener activityStopBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clientSocketThread.stopActivity();
        }
    };

    AdapterView.OnItemSelectedListener activityListItemSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            activityName = (String) adapter.getItem(position);
            Toast.makeText(getApplicationContext(), ""+adapter.getItem(position),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public void startSendingPeriodData() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                clientSocketThread.sendTestMessage();
                startSendingPeriodData();
            }
        },15000);
    }


}
