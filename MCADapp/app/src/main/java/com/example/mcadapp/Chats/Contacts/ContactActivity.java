package com.example.mcadapp.Chats.Contacts;

import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.support.v7.widget.RecyclerView;
import com.android.volley.VolleyError;

import com.example.mcadapp.R;
import com.example.mcadapp.Utils.Config;
import com.example.mcadapp.Utils.FetchUserDetails;
import com.example.mcadapp.Utils.IResult;
import com.example.mcadapp.Utils.VolleyService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactActivity   extends Fragment {

    //define VolleyService
    public IResult mResultCallback = null;
    public VolleyService mVolleyService;
    protected String TAG = "ContactActivity";


    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Map> contacts = new ArrayList<>();
    private static List<Map> tmpcontacts = new ArrayList<>();
    private static AddContact addContact = new AddContact();
    private static FloatingActionButton addContactBtn;
    private static Context thisContext;

    public ContactActivity(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        thisContext = container.getContext();
        View view = inflater.inflate(R.layout.contact_content, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.contacts);


        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        contactAdapter = new ContactAdapter(contacts);
        recyclerView.setAdapter(contactAdapter);
        loadContacts();
        addContactBtn = (FloatingActionButton) view.findViewById(R.id.add_contact_btn);
        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddContactDialogBox();
            }
        });
        return view;
    }


    protected void showAddContactDialogBox(){
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View promptView = layoutInflater.inflate(R.layout.add_contact_user, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(promptView);

        final EditText contactno = (EditText) promptView.findViewById(R.id.add_contact);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String mobileno = contactno.getText().toString();
                        Map contact = new HashMap();
                        contact.put("name",mobileno);
                        contact.put("mobile",mobileno);
                        addContact.addContact(getContext(),mobileno);
                        contactAdapter.addContact(contact);


                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void loadContacts(){
        String Url = Config.HOST+"get_contact/";
        JSONObject params = new JSONObject();
        FetchUserDetails user = new FetchUserDetails(getContext());
        try {
            Url += user.getUsername();
            params.put("password",user.getPassword());

            this.initVolleyCallback();
            mVolleyService = new VolleyService(mResultCallback,getContext());
            mVolleyService.postDataVolley(Config.POSTTYPE,Url,params);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //used by add contacts
    public void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                try {
                    JSONArray data = (JSONArray) response.get("data");
                    Log.d(TAG, "Volley JSON post" + data.toString());
                    for (int i=0;i<data.length();i++){
                        JSONObject obj = data.getJSONObject(i);
                        Map contact = new HashMap();
                        contact.put("name",obj.get("name"));
                        contact.put("mobile",obj.get("mobile"));
                        contactAdapter.addContact(contact);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");
            }
        };
    }

}
