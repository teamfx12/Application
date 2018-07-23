package com.example.jaeheekim.sign_up;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    protected EditText Text_ID;
    protected EditText Text_Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Text_ID = findViewById(R.id.TextID);
        Text_ID = findViewById(R.id.TextPassword);

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
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
            ad.setTitle("Response from Server");
            ad.setMessage(s);
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
                if (id.length() == 0 || passwd.length() == 0) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
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
                String url = "http://192.168.33.99/user/login";
                id = "account=" + id;
                passwd = "passwd=" + passwd;
                String values = id + "&" + passwd;
                //String values = "firstName=GEONUNG&lastName=CHO&email=fakem1333@gmail.com&passwd=apple";
                NetworkTask networkTask = new NetworkTask(url, values);
                networkTask.execute();
            }
        }
    }
}

