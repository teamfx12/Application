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

public class MainActivity extends AppCompatActivity {

    protected EditText Text_Email;
    protected EditText Text_Password;
    protected Button Sign_in;
    protected InputMethodManager imm;
    protected ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Text_Email = findViewById(R.id.TextEmail);
        Text_Password = findViewById(R.id.TextPassword);
        layout = (ConstraintLayout)findViewById(R.id.layout);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        Sign_in = (Button)findViewById(R.id.Sign_in);

        layout.setOnClickListener(myClickListener);
        Sign_in.setOnClickListener(myClickListener);
        // 위젯에 대한 참조.
        //tv_outPut = (TextView) findViewById(R.id.tv_outPut);
        // URL 설정.
        //String url = "http://192.168.33.99/user/signup";
    }

    View.OnClickListener myClickListener = new View.OnClickListener(){

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
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        //private ContentValues values;
        private String values;

        public NetworkTask(String url, String values) {
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
                    //Msg = "Please Check your Email \" "+json_result.getString("email") + " \" and click your link" ;
                    Msg = "Login\nHello!";
                    Show_dialog(title,Msg);
                    Intent location = new Intent(getApplicationContext(), Current_LocationActivity.class);
                    startActivity(location);
                    return;
                } else {
                    Msg = "Message : " + json_result.getString("msg");
                }
            } catch (JSONException e) {
                title = "Error";
                Msg = "JSON parsing Error";
            }
            Show_dialog(title, Msg);
        }
        private void Show_dialog(String title, String Msg){
            AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
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
    }

    public void onclick_register(View v) {
        switch (v.getId()) {
            case R.id.Register: {
                Intent reg = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(reg);
            }
        }
    }
    public void onclick_find(View v) {
        switch (v.getId()) {
            case R.id.Find: {
                Intent find = new Intent(getApplicationContext(), Find_PasswordActivity.class);
                startActivity(find);
            }
        }
    }

    public void onclick_login(View v) {
        switch (v.getId()) {
            case R.id.Sign_in: {
                String email = this.Text_Email.getText().toString();
                String pw = this.Text_Password.getText().toString();
                String function;
                if (email.length() == 0 || pw.length() == 0) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                    ad.setTitle("Text Error");
                    ad.setMessage("Please Enter your ID or Password");
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
                function = "function=sign-in&";
                email = "email=" + email;
                pw = "passwd=" + pw;
                String values = function + email + "&" + pw;
                //String values = "firstName=GEONUNG&lastName=CHO&email=fakem1333@gmail.com&passwd=apple";
                NetworkTask networkTask = new NetworkTask(url, values);
                networkTask.execute();
            }
        }
    }
}