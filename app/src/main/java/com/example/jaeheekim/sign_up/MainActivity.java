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

public class MainActivity extends AppCompatActivity {

    protected EditText Text_ID;
    protected EditText Text_Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Text_ID = findViewById(R.id.TextID);
        Text_Password = findViewById(R.id.TextPassword);

        // 위젯에 대한 참조.
        //tv_outPut = (TextView) findViewById(R.id.tv_outPut);
        // URL 설정.
        //String url = "http://192.168.33.99/user/signup";
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
                if (title.equals("OK")) {
                    //Msg = "Please Check your Email \" "+json_result.getString("email") + " \" and click your link" ;
                    Msg = "Login\nHello!" + json_result.getString("fname") + " " + json_result.getString("lname");
                    Show_dialog(title,Msg);
                    //Intent location = new Intent(getApplicationContext(), Current_LocationActivity.class);
                    //startActivity(location);
                    return;
                } else {
                    Msg = "Msg : " + json_result.getString("Msg");
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
    public void onclick_Find(View v) {
        switch (v.getId()) {
            case R.id.Forgot: {
                Intent find = new Intent(getApplicationContext(), Find_IDActivity.class);
                startActivity(find);
            }
        }
    }

    public void onclick_login(View v) {
        switch (v.getId()) {
            case R.id.Sign_in: {
                String id = this.Text_ID.getText().toString();
                String passwd = this.Text_Password.getText().toString();
                String function;
                if (id.length() == 0 || passwd.length() == 0) {
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
                String url = "http://teamf-iot.calit2.net/user/login";
                function = "funcition=login&";
                id = "id=" + id;
                passwd = "passwd=" + passwd;
                String values = function + id + "&" + passwd;
                //String values = "firstName=GEONUNG&lastName=CHO&email=fakem1333@gmail.com&passwd=apple";
                NetworkTask networkTask = new NetworkTask(url, values);
                networkTask.execute();
            }
        }
    }
}