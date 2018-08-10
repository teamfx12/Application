package com.example.jaeheekim.sign_up.userManagement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.jaeheekim.sign_up.GlobalVar;
import com.example.jaeheekim.sign_up.MainActivity;
import com.example.jaeheekim.sign_up.R;
import com.example.jaeheekim.sign_up.RequestHttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

public class DeleteAccountActivity extends AppCompatActivity {

    protected EditText Text_Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);
        Text_Password = findViewById(R.id.Password);
    }
    // to communication with Server
    public class NetworkTaskDelete extends AsyncTask<Void, Void, String> {
        private String url;                     // Server URL
        private String values;                  // Values passing to Server form Android
        //constructor
        public NetworkTaskDelete(String url, String values) {
            this.url = url;
            this.values = values;
        }
        //start from here
        @Override
        protected String doInBackground(Void... params) {
            String result;                      // Variable to store value from Server "url"
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);     // get result from this "url"
            return result;
        }
        // start after done doInBackground, result will be s in this function
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String msg;                         // msg to show to the user
            String title;                       // title of Msg
            try {
                JSONObject json_result = new JSONObject(s);             // make JSONObject to store data from the Server
                title = json_result.getString("status");                // title will be value of s's "status"
                if (title.equals("ok")) {
                    msg = "Thank you for using our Application";
                    ShowDialog(title, msg);
                    GlobalVar.setFlag(true);
                    return;
                } else {
                    msg = "Msg : " + json_result.getString("msg");
                    ShowDialog(title, msg);
                }
            } catch (JSONException e) {
                title = "Error";
                msg = "JSON parsing Error";
                ShowDialog(title, msg);
            }
            GlobalVar.setFlag(true);
        }

        private void ShowDialog(final String title, String Msg){
            AlertDialog.Builder ad = new AlertDialog.Builder(DeleteAccountActivity.this);
            ad.setTitle(title);
            ad.setMessage(Msg);
            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if(title.equals("ok")) {
                        Intent toLogin = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(toLogin);
                        finish();
                    }
                }
            });
            ad.show();
        }
    }

    public void onClickDelete(View view){
        switch (view.getId()){
            case R.id.btnDelete: {
                String function = "function=delete-account&";
                String pw ="passwd="+this.Text_Password.getText().toString();
                String token = "&token="+ GlobalVar.getToken();
                final String url = "http://teamf-iot.calit2.net/user";
                final String values = function+pw+token;

                if (pw.length() == 0) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(DeleteAccountActivity.this);
                    ad.setTitle("Text Error");
                    ad.setMessage("Please Enter your Password");
                    ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ad.show();
                    return;
                }

                AlertDialog.Builder check = new AlertDialog.Builder(DeleteAccountActivity.this);
                check.setTitle("Check");
                check.setMessage("Are you sure delete your account???");
                check.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(GlobalVar.getFlag() == true) {
                            GlobalVar.setFlag(false);
                            NetworkTaskDelete networkTaskDelete = new NetworkTaskDelete(url, values);
                            networkTaskDelete.execute();
                            MainActivity.mainActivity.finish();
                        }
                        dialog.dismiss();
                    }
                });
                check.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                check.show();
            }
        }
    }
}