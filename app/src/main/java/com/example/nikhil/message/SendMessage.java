package com.example.nikhil.message;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class SendMessage extends AppCompatActivity {

    //EditText senderID_editText;
    EditText receiver_editText;
    EditText message_editText;
    Button send_button;
    Button pick_contacts_button;
    TextView contactsDisplay;
    String username;
    String password;
    String senderID;
    String username_pref_key = "username_pref";
    String password_pref_key = "password_pref";
    ArrayList<Contact> selectedContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        //senderID_editText = (EditText)findViewById(R.id.sender_id_id);
        receiver_editText = (EditText)findViewById(R.id.phone_number_id);
        message_editText = (EditText)findViewById(R.id.message_id);
        send_button = (Button)findViewById(R.id.send_button_id);
        pick_contacts_button = (Button)findViewById(R.id.pick_contacts_id);
        contactsDisplay = (TextView)findViewById(R.id.chosen_contacts_id);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        username = prefs.getString(username_pref_key, "");
        password = prefs.getString(password_pref_key, "");
        Log.e("username and passowrd", username+" "+password);
        senderID = "GIRISH";


        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkForEmpty() && connectionOK()){

                    if(username.isEmpty() || password.isEmpty())
                        Toast.makeText(getApplicationContext(), "Please set username and password in Settings", Toast.LENGTH_SHORT).show();
                    else {
                        if(!receiver_editText.getText().toString().isEmpty())
                            sendRequest(senderID, receiver_editText.getText().toString(),
                                    message_editText.getText().toString(), username, password);
                        if(selectedContacts != null){

                            for(int i=0; i<selectedContacts.size(); i++){

                                sendRequest(senderID, selectedContacts.get(i).phone,
                                        message_editText.getText().toString(), username, password);
                            }
                        }
                    }
                }
            }
        });

        pick_contacts_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ContactsPickerActivity.class);
                startActivityForResult(intent, 123);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 123 && resultCode == RESULT_OK){

            selectedContacts = data.getParcelableArrayListExtra("SelectedContacts");
            processNumbers(selectedContacts);

            String display="";
            for(int i=0;i<selectedContacts.size();i++){

                display += (i+1)+". "+selectedContacts.get(i).toString()+"\n";


            }
            contactsDisplay.setText("Selected Contacts : \n\n"+display);

        }

    }
    public boolean checkForEmpty(){



        if(message_editText.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter message", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean connectionOK(){

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(!isConnected)
            makeToast("Please switch on the data connection or wifi");
        return isConnected;
    }

    public String processMessage(String message){

        message = message.replaceAll(" ", "+");
        message = message.replaceAll("\n", "%0A");

        return message;
    }

    public void processNumbers(ArrayList<Contact> selectedContacts){

        for(int i=0; i<selectedContacts.size(); i++){

            selectedContacts.get(i).phone.replaceAll(" ", "");
        }
    }
    public void sendRequest(String senderID, String phone_number, String message, String username, String password){

        try{
            String request1 = "http://110.173.182.98/API/WebSMS/Http/v1.0a/index.php?username=";
            String request2 = "&password=";
            String request3 = "&sender=";
            String request4 = "&to=";
            String request5 = "&message=";

            message = processMessage(message);
            String request = request1+username+request2+password+request3+senderID+request4+phone_number
                    +request5+message;


            URL url = new URL(request);
            Log.e("url", url.toString());

            new requestClass().execute(url);
        }
        catch(Exception e){
            Log.e("url exception", e.toString());

        }
    }

    public class requestClass extends AsyncTask<URL, Integer, String>{


        @Override
        protected String doInBackground(URL... params) {
            String data = "";
            try {
                URL url = params[0];
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                BufferedInputStream isw = new BufferedInputStream(in);



                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                sb.append(reader.readLine() + "\n");
                String line="0";
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                in.close();
                data = sb.toString();
                Log.e("data", data);

            }
            catch(Exception e) {
                Log.e("error", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            makeToast(s);
        }
    }

    public void makeToast(String str){

        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }

    public void createDialog(String str){


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Info")
                .setIcon(R.mipmap.info_black)
                .setMessage(str)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        username = prefs.getString(username_pref_key, "");
        password = prefs.getString(password_pref_key, "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.send_message_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.settings_id) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        }
        else if(item.getItemId() == R.id.info_id){
            String str = "Designed and developed by \nNikhil Bhatia under \nThe Computer Machine technologies\n" +
                    "for Girish";
            createDialog(str);
            return true;
        }
        else
            return true;
    }
}


