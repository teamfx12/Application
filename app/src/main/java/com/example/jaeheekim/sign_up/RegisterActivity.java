package com.example.jaeheekim.sign_up;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private Boolean emailChecked=false;
    private String emailCheck;
    private boolean flag=true;
    private CheckBox checkBox;
    //private ConstraintLayout layout;
    //protected Button Check;
    //protected Button Done;
    //protected InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);                     // content connection with activity_register
        firstName = findViewById(R.id.firstName);                       // get user input
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        checkBox = findViewById(R.id.checkBox);
        //layout = (ConstraintLayout) findViewById(R.id.layout);
        //Check = (Button) findViewById(R.id.Check);
        //Done = (Button) findViewById(R.id.Done);
        //imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //layout.setOnClickListener(myClickListener);
        //Check.setOnClickListener(myClickListener);
        //Done.setOnClickListener(myClickListener);
    }
    /*View.OnClickListener myClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            hideKeyBoard();
            switch (view.getId()){
                case R.id.layout :
                    break;
                case R.id.Check:
                    break;
                case R.id.Done:
                    break;
            }
        }
    };

    private void hideKeyBoard(){
        imm.hideSoftInputFromWindow(First_name.getWindowToken(),0);
        imm.hideSoftInputFromWindow(Last_name.getWindowToken(),0);
        imm.hideSoftInputFromWindow(E_mail.getWindowToken(),0);
        imm.hideSoftInputFromWindow(Password.getWindowToken(),0);
    }*/

    // to show the message to user
    public void showDialog(final String title, String Msg){
        AlertDialog.Builder ad = new AlertDialog.Builder(RegisterActivity.this);
        ad.setTitle(title);
        ad.setMessage(Msg);
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }

    // when user touch ID Check bottom
    public void onClickCheck(View view){
        switch (view.getId()) {
            case R.id.btnCheck: {
                String function = "function=mail-check&";
                String email = "email=" + this.email.getText().toString();
                emailCheck = this.email.getText().toString();

                String url = "http://teamf-iot.calit2.net/user";
                String values = function + email;           // make data to JSON format
                // send request to server
                if(flag == true && emailCheck.contains("@")) {
                    NetworkTaskEmail networkTaskEmail = new NetworkTaskEmail(url, values);
                    networkTaskEmail.execute();
                } else if(!emailCheck.contains("@")){
                    emailChecked = false;
                    showDialog("Error","It is not correct form");
                }
            }
        }
    }

    // to communication with Server to check ID duplication
    public class NetworkTaskEmail extends AsyncTask<Void, Void, String> {

        private String url;                         // Server URL
        private String values;                      // data passing to Server from Android
        // constructor
        public NetworkTaskEmail(String url, String values) {
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
                // ID user enter is not in database
                if (title.equals("ok")) {
                    msg = "You can use this email";         // show this massage
                    emailChecked = true;                    // and set emailChecked true
                }
                else {  // ID user enter is in database already
                    msg = "Message : It exist already";     // show this massage
                    emailChecked = false;                   // and set emailChecked false
                }
            } catch (JSONException e) {
                msg = "JSON parsing Error";
            }
            showDialog("Error", msg);
            flag = true;
        }
    }

    // when user touch Done bottom
    public void onClickRegister(View v) {
        switch (v.getId()) {
            case R.id.btnDone: {
                // get data from user
                String JSON_base[] = {"function", "fname", "lname", "email", "passwd"};
                String input_str[] = new String[5];
                // [0] : function // [1] : fname // [2] : lname // [3] : id // [4] : passwd
                input_str[0] = "sign-up";
                input_str[1] = this.firstName.getText().toString();
                input_str[2] = this.lastName.getText().toString();
                // user need to check ID duplication before register
                if (emailChecked == true && emailCheck.equals(this.email.getText().toString()))
                    input_str[3] = this.email.getText().toString();
                else {
                    emailChecked = false;
                    input_str[3] = " ";
                }
                input_str[4] = this.password.getText().toString();
                // conform everything has entered
                for (int i = 1; i < input_str.length; i++) {
                    if (input_str[i].length() == 0) {
                        AlertDialog.Builder ad = new AlertDialog.Builder(RegisterActivity.this);
                        ad.setTitle("Text Error");
                        ad.setMessage("Please Enter your" + JSON_base[i]);
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
                for (int i = 0; i < input_str.length; i++) {
                    values = values + JSON_base[i] + "=";
                    values = values + input_str[i];
                    if (i != input_str.length - 1) {
                        values = values + "&";
                    }
                }
                // after email duplication check, send request to Server
                if (emailChecked == true && flag == true && checkBox.isChecked()) {
                    flag = false;
                    NetworkTaskRegi networkTaskRegi = new NetworkTaskRegi(url, values);
                    networkTaskRegi.execute();
                } else if(emailChecked == false) {
                    showDialog("Error", "You need to check your email first");
                } else if(!checkBox.isChecked()) {
                    showDialog("Error", "You need to check our checkBox");
                }
            }
        }
    }

    // to communication with Server to register
    public class NetworkTaskRegi extends AsyncTask<Void, Void, String> {

        private String url;                         // Server URL
        private String values;                      // data passing to Server from Android
        // constructor
        public NetworkTaskRegi(String url, String values) {
            this.url = url;
            this.values = values;
        }
        // start from here
        @Override
        protected String doInBackground(Void... params) {
            String result; // 요청 결과를 저장할 변수.
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
                // is everything ok
                if (title.equals("ok")) {
                    msg = "Signed up now\nHello!\nPlease verify your email";    // show this message
                } else {
                    // title is "Error"
                    msg = "Message : " + json_result.getString("msg");
                }
            } catch (JSONException e) {
                title ="Error";
                msg = "JSON parsing Error";
            }
            // use own showDialog to check everything gonna be alright, then go to Login Screen
            this.showDialog(title, msg);
            flag = true;
        }

        // use own showDialog to check user can register
        public void showDialog(final String title, String msg){
            AlertDialog.Builder ad = new AlertDialog.Builder(RegisterActivity.this);
            ad.setTitle(title);
            ad.setMessage(msg);
            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    // if user submit their information well, let user be able to login
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
}