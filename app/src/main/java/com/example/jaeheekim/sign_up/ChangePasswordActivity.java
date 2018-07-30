package com.example.jaeheekim.sign_up;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordActivity extends AppCompatActivity {

    protected EditText currentPW;
    protected EditText newPW;
    protected EditText conformNewPW;
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);         // content connection with activity_log_in
        currentPW = findViewById(R.id.currentPW);                   // get User input
        newPW = findViewById(R.id.newPW);
        conformNewPW = findViewById(R.id.conformNewPW);
    }

    public void onClickChange(View view){
        switch (view.getId()){
            case R.id.btnChange: {
                // get data sent form user
                String JSON_base[] = {"function", "token", "currentpw", "newpw"};
                String input_str[] = new String[4];
                // [0] : function // [1] : token // [2] : currentpw // [3] : newpw
                input_str[0] = "change-pw";
                input_str[1] = GlobalVar.getToken();
                input_str[2] = this.currentPW.getText().toString();
                input_str[3] = this.newPW.getText().toString();
                // pw are not entered
                for(int i = 2 ;i < input_str.length; i++) {
                    if(input_str[i].length() == 0) {
                        AlertDialog.Builder ad = new AlertDialog.Builder(ChangePasswordActivity.this);
                        ad.setTitle("Text Error");
                        ad.setMessage("Please Enter your " + JSON_base[i]);
                        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        ad.show();
                        return;
                    }
                }
                // new password and conform password are not matched
                if (!input_str[3].equals(this.conformNewPW.getText().toString())){
                    AlertDialog.Builder ad = new AlertDialog.Builder(ChangePasswordActivity.this);
                    ad.setTitle("PassWord conform Error");
                    ad.setMessage("your new two password are not same");
                    ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ad.show();
                    return;
                }
                // new password is same with current password
                else if (input_str[2].equals(input_str[3])){
                    AlertDialog.Builder ad = new AlertDialog.Builder(ChangePasswordActivity.this);
                    ad.setTitle("PassWord change Error");
                    ad.setMessage("Please make your new and current password different");
                    ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ad.show();
                    return;
                }
                String url = "http://teamf-iot.calit2.net/user";
                String values = "";
                // make data to JSON format
                for(int i=0;i<input_str.length;i++) {
                    values = values + JSON_base[i] + "=";
                    values = values + input_str[i];
                    if(i!=input_str.length-1) {
                        values = values + "&";
                    }
                }
                // call Method to communicate with server
                if(flag == true) {
                    flag = false;
                    NetworkTaskChange networkTaskChange = new NetworkTaskChange(url, values);
                    networkTaskChange.execute();
                }
            }
        }
    }
    // to communication with Server
    public class NetworkTaskChange extends AsyncTask<Void, Void, String> {
        private String url;                     // Server URL
        private String values;                  // Values passing to Server form Android
        //constructor
        public NetworkTaskChange(String url, String values) {
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
                // if user can change their password,
                if (title.equals("ok")) {
                    msg = "Your password is changed successfully";      // show this message then exit
                    showDialog(title, msg);
                    return;
                }else if(title.equals("token_expired")){                // when passing token is expired
                    msg = "Msg : " +json_result.getString("msg");
                    showDialog("Error", msg);
                }
                else {
                    msg = "Msg : " + json_result.getString("msg");
                    showDialog("Error", msg);
                }
            } catch (JSONException e) {
                msg = "JSON parsing Error";
                showDialog("Error", msg);
            }
            flag = true;
        }
        private void showDialog(final String title, String Msg){
            AlertDialog.Builder ad = new AlertDialog.Builder(ChangePasswordActivity.this);
            ad.setTitle(title);
            ad.setMessage(Msg);
            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if(title.equals("ok")) {
                        Intent to_main = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(to_main);
                        finish();
                    }
                    else if(title.equals("token_expired")) {
                        Intent to_login = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(to_login);
                        finish();
                    }
                }
            });
            ad.show();
        }
    }
}
