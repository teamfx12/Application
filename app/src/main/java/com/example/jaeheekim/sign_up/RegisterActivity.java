package com.example.jaeheekim.sign_up;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText First_name;
    private EditText Last_name;
    private EditText E_mail;
    private EditText Password;
    private Boolean email_checked=false;
    //private ConstraintLayout layout;
    //protected Button Check;
    //protected Button Done;
    //protected InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        First_name = findViewById(R.id.First_name);
        Last_name = findViewById(R.id.Last_name);
        E_mail = findViewById(R.id.E_mail);
        Password = findViewById(R.id.Password);
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

    public class NetworkTask_regi extends AsyncTask<Void, Void, String> {

        private String url;
        private String values;

        public NetworkTask_regi(String url, String values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String Msg;
            String title;
            try {
                JSONObject json_result = new JSONObject(s);
                title = json_result.getString("status");
                if (title.equals("ok")) {
                    Msg = "Signed up now\nHello!";
                    Intent intent_to_main = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent_to_main);
                } else {
                    Msg = "msg : " + json_result.getString("msg");
                }
            } catch (JSONException e) {
                title = "Error";
                Msg = "JSON parsing Error";
            }
            Show_dialog(title, Msg);
        }
    }

    public void Show_dialog(String title, String Msg){
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

    public void onclick_register(View v) {
        switch (v.getId()) {
            case R.id.Done: {
                String JSON_base[] = {"fname=", "lname=","email=","passwd="};
                String input_str[] = new String[4];
                //[0] : fname // [1] : lname // [2] : id // [3] : passwd
                input_str[0] = this.First_name.getText().toString();
                input_str[1] = this.Last_name.getText().toString();
                input_str[2] = this.E_mail.getText().toString();
                input_str[3] = this.Password.getText().toString();

                for(int i = 0 ;i< 4; i++) {
                    if(input_str[i].length() == 0) {
                        AlertDialog.Builder ad = new AlertDialog.Builder(RegisterActivity.this);
                        ad.setTitle("Text Error");
                        ad.setMessage("Please Enter");
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
                String values = "function=sign-up&";
                for(int i=0;i<4;i++) {
                    values = values + JSON_base[i];
                    values = values + input_str[i];
                    if(i!=3) {
                        values = values + "&";
                    }
                }
                if(email_checked == true) {
                    NetworkTask_regi networkTask_regi = new NetworkTask_regi(url, values);
                    networkTask_regi.execute();
                }
                else{
                    Show_dialog("Error","You need to check your email first");
                }
            }
        }
    }

    public class NetworkTask_email extends AsyncTask<Void, Void, String> {

        private String url;
        private String values;

        public NetworkTask_email(String url, String values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String Msg;
            String title;
            try {
                JSONObject json_result = new JSONObject(s);
                title = json_result.getString("status");
                if (title.equals("ok")) {
                    Msg = "You can use this email";
                    email_checked = true;
                    //이후 다른 사용가능한 이메일을 입력하지 않았을때
                }
                else {
                    Msg = "Message : It exist already";
                }
            } catch (JSONException e) {
                title = "Error";
                Msg = "JSON parsing Error";
            }
            Show_dialog(title, Msg);
        }
    }

    public void onclick_check(View view){
        switch (view.getId()) {
            case R.id.Check: {
                String email = "email=" + this.E_mail.getText().toString();
                String function = "function=mail-check&";

                String url = "http://teamf-iot.calit2.net/user";
                String values = function + email;

                NetworkTask_email networkTask_email = new NetworkTask_email(url, values);
                networkTask_email.execute();
            }
        }
    }
}