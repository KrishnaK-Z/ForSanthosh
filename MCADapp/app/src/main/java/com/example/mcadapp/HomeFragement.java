package com.example.mcadapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mcadapp.Utils.FetchUserDetails;


public class HomeFragement extends Fragment {

    protected static String TAG = "HomeFragment:";
    protected static TextView _username_ ;
    protected static View view = null;

    protected static FrameLayout frameLayout;
    protected static Fragment fragment = null;
    protected static FragmentManager fragmentManager;
    protected static FragmentTransaction fragmentTransaction;
    protected FetchUserDetails userDetails;

    public  HomeFragement(){
        //requires empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.main_content, container, false);
        _username_ = (TextView) view.findViewById(R.id.textview_name);
        userDetails = new FetchUserDetails(view.getContext());
        String name = userDetails.getName();
        if(name != null){
            _username_.setText("Welcome "+name);
        }

        return view;
    }

}
