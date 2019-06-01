package com.example.mcadapp.DashBoard;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mcadapp.Mcad.ListenActivity;
import com.example.mcadapp.R;
import com.example.mcadapp.WifiConnector.WifiConnector;


public class DashBoard extends Fragment{

    protected static String TAG = DashBoard.class.getName();

    protected static View view = null;
    protected static FrameLayout frameLayout;
    protected static Fragment fragment = null;
    protected static FragmentManager fragmentManager;
    protected static FragmentTransaction fragmentTransaction;

    protected ImageButton wifiBtn,mcadBtn;
    Intent wificonnector,mcadActivity;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_dash_board, container, false);
        wifiBtn = (ImageButton) view.findViewById(R.id.wifiBtn);
        mcadBtn = (ImageButton) view.findViewById(R.id.mcadBtn);

        wificonnector = new Intent(view.getContext(), WifiConnector.class);
        mcadActivity  = new Intent(view.getContext(), ListenActivity.class);

        wifiBtn.setOnClickListener(connectBtnListener);
        mcadBtn.setOnClickListener(mcadBtnListener);
        return view;
    }

    public View.OnClickListener connectBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(wificonnector);
        }
    };

    public View.OnClickListener mcadBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(mcadActivity);
        }
    };

}
