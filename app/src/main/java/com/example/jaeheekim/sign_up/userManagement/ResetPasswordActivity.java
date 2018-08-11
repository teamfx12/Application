package com.example.jaeheekim.sign_up.userManagement;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.jaeheekim.sign_up.GlobalVar;
import com.example.jaeheekim.sign_up.R;
import com.example.jaeheekim.sign_up.RequestHttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

public class ResetPasswordActivity extends AppCompatActivity {

    protected EditText textFname;
    protected EditText textEmail;
    protected ProgressDialog nDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);                // content connection with activity_register
        textFname=findViewById(R.id.firstName);                          // get user input
        textEmail=findViewById(R.id.email);

        nDialog = new ProgressDialog(ResetPasswordActivity.this);
    }

    // when user touch Find bottom
    public void onClickReset(View view){

        nDialog.setMessage("Loading..");
        nDialog.setTitle("Checking");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);
        nDialog.show();

        switch (view.getId()){
            case R.id.btnResetPassword: {
                // get data sent form user
                String JSON_base[] = {"function", "fname", "email"};
                String input_str[] = new String[3];
                // [0] : function // [1] : fname // [2] : email
                input_str[0] = "find-pw";
                input_str[1] = this.textFname.getText().toString();
                input_str[2] = this.textEmail.getText().toString();
                // email or pw is not entered
                for(int i = 1 ;i < input_str.length; i++) {
                    if(input_str[i].length() == 0) {
                        AlertDialog.Builder ad = new AlertDialog.Builder(ResetPasswordActivity.this);
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
                if(GlobalVar.getFlag() == true) {
                    GlobalVar.setFlag(false);
                    NetworkTaskReset networkTaskReset = new NetworkTaskReset(url, values);
                    networkTaskReset.execute();


                }
            }
        }
    }

    // to communication with Server to check ID duplication
    public class NetworkTaskReset extends AsyncTask<Void, Void, String> {

        private String url;                         // Server URL
        private String values;                      // data passing to Server from Android
        // constructor
        public NetworkTaskReset(String url, String values) {
            this.url = url;
            this.values = values;
        }
        // start from here
        @Override
        protected String doInBackground(Void... params) {
            String result;       // Variable to store value from Server "url"
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // get result from this "url"
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
                // if user entered right email and first name
                if (title.equals("ok")) {
                    msg = "We mailed your password";
                    showDialog(title,msg);
                    GlobalVar.setFlag(true);
                    return;
                } else {
                    msg = "Msg : " + json_result.getString("msg");
                    showDialog("Error", msg);
                }
            } catch (JSONException e) {
                msg = "JSON parsing Error";
                showDialog("Error", msg);
            }
            GlobalVar.setFlag(true);
        }
        private void showDialog(final String title, String Msg){
            AlertDialog.Builder ad = new AlertDialog.Builder(ResetPasswordActivity.this);
            ad.setTitle(title);
            ad.setMessage(Msg);
            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    // if temp password is sent, go login screen
                    if(title.equals("ok")) {
                        Intent toMain = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(toMain);
                        finish();
                    }
                }
            });
            nDialog.dismiss();
            ad.show();
        }
    }
}
