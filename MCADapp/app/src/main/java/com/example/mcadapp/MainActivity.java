package com.example.mcadapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.mcadapp.Chats.Contacts.ContactActivity;
import com.example.mcadapp.DashBoard.DashBoard;
import com.example.mcadapp.Utils.FetchUserDetails;

import static com.android.volley.VolleyLog.TAG;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected static Intent loginIntent,chatIntent;

    protected static DrawerLayout drawer;
    protected static FrameLayout frameLayout;
    protected static Fragment fragment = null;
    protected static FragmentManager fragmentManager;
    protected static FragmentTransaction fragmentTransaction;
    protected static BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //bottom navigation
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // check login
        checkLogin();



        //frame manager
        frameLayout = (FrameLayout) findViewById(R.id.frame_container);
        fragment = new HomeFragement();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container,fragment);
        fragmentTransaction.setTransition(fragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();


    }

    @Override
    protected void onDestroy() {
        SharedPreferences sp = getSharedPreferences("login",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_logout){

            SharedPreferences sp = getSharedPreferences("login",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.commit();

            Toast.makeText(getApplicationContext(),"Logged Out", Toast.LENGTH_SHORT).show();
            loginIntent = new Intent(MainActivity.this,Login.class);
            startActivity(loginIntent);

        }
        else if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void checkLogin(){
        try{
            FetchUserDetails user = new FetchUserDetails(getApplicationContext());
            String username = user.getUsername();
            String password = user.getPassword();
            SharedPreferences sp = getSharedPreferences("login",Context.MODE_PRIVATE);
            String isLoggedIn = sp.getString("isLoggedin","false");

            if( username==null || password==null){
                loginIntent = new Intent(getApplicationContext().getApplicationContext(), Login.class );
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
            }
            else if(isLoggedIn == "false" ){
                Login.login(username,password,getApplicationContext());
            }

        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"file does not exist",Toast.LENGTH_SHORT).show();
            loginIntent = new Intent(getApplicationContext().getApplicationContext(), Login.class );
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            e.printStackTrace();
        }
    }

    protected BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Log.d(TAG,"called");
            fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_chat:
                    fragment = new ContactActivity();
                    Toast.makeText(getApplicationContext(),"Opening Chats",Toast.LENGTH_SHORT).show();
                    break;
                //return true;
                case R.id.navigation_home:
                    fragment = new HomeFragement();
                    Toast.makeText(getApplicationContext(),"Opening Home Page",Toast.LENGTH_SHORT).show();
                    break;
                //return true;
                case R.id.navigation_dashboard:
                    fragment = new DashBoard();
                    Toast.makeText(getApplicationContext(),"Opening Dashboard",Toast.LENGTH_SHORT).show();
                    break;
                //return true;
            }

            if(fragment != null){

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frame_container, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
            }
            return true;
        }
    };

}
