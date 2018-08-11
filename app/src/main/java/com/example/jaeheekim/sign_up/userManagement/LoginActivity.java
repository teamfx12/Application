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

import com.example.jaeheekim.sign_up.GlobalVar;
import com.example.jaeheekim.sign_up.MainActivity;
import com.example.jaeheekim.sign_up.R;
import com.example.jaeheekim.sign_up.RequestHttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class LoginActivity extends AppCompatActivity {

    protected EditText textEmail;
    protected EditText textPassword;
    protected ProgressDialog nDialog;
    //protected Button Sign_in;
    //protected InputMethodManager imm;
    //protected ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);                            // content connection with activity_log_in
        textEmail = findViewById(R.id.textEmail);                            // get User input
        textPassword = findViewById(R.id.textPassword);
        nDialog = new ProgressDialog(LoginActivity.this);
        //layout = (ConstraintLayout)findViewById(R.id.layout);
        //imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //Sign_in = (Button)findViewById(R.id.Sign_in);

        //layout.setOnClickListener(myClickListener);
        //Sign_in.setOnClickListener(myClickListener);
        // 위젯에 대한 참조.
        //tv_outPut = (TextView) findViewById(R.id.tv_outPut);
        // URL 설정.
        //String url = "http://192.168.33.99/user/signup";
    }

    /*View.OnClickListener myClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            hideKeyBoard();
            switch (view.getId()){
                case R.id.layout :
                    break;
                case R.id.Sign_in:
                    break;
            }
        }
    };

    private void hideKeyBoard(){
        imm.hideSoftInputFromWindow(Text_Email.getWindowToken(),0);
        imm.hideSoftInputFromWindow(Text_Password.getWindowToken(),0);
    }*/

    public void onClickLogin(View v) {

        nDialog.setMessage("Loading..");
        nDialog.setTitle("Check");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);
        nDialog.show();

        switch (v.getId()) {
            case R.id.btnSignIn: {
                // get data sent form user
                String JSON_base[] = {"function", "email", "passwd"};
                String input_str[] = new String[3];
                // [0] : function // [1] : email // [2] : passwd
                input_str[0] = "sign-in";
                input_str[1] = this.textEmail.getText().toString();
                input_str[2] = this.textPassword.getText().toString();
                // email or pw is not entered
                for(int i = 1 ;i < input_str.length; i++) {
                    if(input_str[i].length() == 0) {
                        AlertDialog.Builder ad = new AlertDialog.Builder(LoginActivity.this);
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
                    NetworkTask networkTask = new NetworkTask(url, values);
                    networkTask.execute();
                }
            }
        }
    }
    // to communication with Server
    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;                     // Server URL
        private String values;                  // Values passing to Server form Android
        //constructor
        public NetworkTask(String url, String values) {
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
            if(s == null){
                showDialog("connection error","try latter",0);
                return;
            }


            String msg = null;                  // msg to show to the user
            String title;                       // title of Msg
            int isTemp;                         // Whether the passing password is temp password or not
            try {
                JSONObject json_result = new JSONObject(s);             // make JSONObject to store data from the Server
                title = json_result.getString("status");                // title will be value of s's "status"
                // if user login,
                if (title.equals("ok")) {
                    msg = "Login\nHello!";                              // show this Msg
                    isTemp = json_result.getInt("is_temp");             // if it is temp password, is_temp is 1
                    GlobalVar.setToken(json_result.getString("token")); // store token to Global Variable
                    GlobalVar.setEmail(json_result.getString("email"));
                    GlobalVar.setFname(json_result.getString("fname"));
                    GlobalVar.setLname(json_result.getString("lname"));
                    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); //format of token_expire
                    GlobalVar.setTokenExpire(format.parse(json_result.getString("token_expire")));
                    //send title : ok, msg : Login\nHello, is_temp : ?
                    showDialog(title,msg,isTemp);
                    GlobalVar.setFlag(true);
                    return;
                }
                else {
                    //title is "error"
                    msg = "Message : " + json_result.getString("msg");
                }
            } catch (JSONException e) {
                msg = "JSON parsing Error";
            } catch (ParseException e) {        // catch passing error
                e.printStackTrace();
            }
            showDialog("Error", msg, 0);
            GlobalVar.setFlag(true);
        }

        // use own showDialog to check isTemp.
        private void showDialog(final String title, String msg, final int isTemp){
            final AlertDialog.Builder ad = new AlertDialog.Builder(LoginActivity.this);
            ad.setTitle(title);     // set title and msg
            ad.setMessage(msg);
            // if user touch "yes" bottom
            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();   // first dialog dismiss
                    // if user login with temp password let user change the password
                    if(title.equals("ok") && isTemp == 1){
                        Intent toChange = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                        startActivity(toChange);
                        finish();
                    }
                    // user login successfully. go to MainActivity
                    else if(title.equals("ok")) {
                        Intent toMain = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(toMain);
                        finish();
                    }
                }
            });
            nDialog.dismiss();
            ad.show();
        }
    }
    // change to Register screen
    public void onClickRegister(View v) {
        switch (v.getId()) {
            case R.id.btnRegister: {
                Intent toRegi = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(toRegi);
            }
        }
    }
    // change to Finding Password screen
    public void onClickReset(View v) {
        switch (v.getId()) {
            case R.id.btnResetPassword: {
                Intent find = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivity(find);
            }
        }
    }
}