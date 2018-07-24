package com.example.jaeheekim.sign_up;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText ID;
    private EditText First_name;
    private EditText Last_name;
    private EditText E_mail;
    private EditText Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ID = findViewById(R.id.ID);
        First_name = findViewById(R.id.First_name);
        Last_name = findViewById(R.id.Last_name);
        E_mail = findViewById(R.id.E_mail);
        Password = findViewById(R.id.Password);
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
                    Msg = "Signed up now\nHello!" + json_result.getString("fname") + " " + json_result.getString("lname");
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
    }
    public void onclick_register(View v) {
        switch (v.getId()) {
            case R.id.Done: {
                String JSON_base[] = {"ID=", "firstName=", "lastName=","email=","passwd="};
                String input_str[] = new String[5];
                //[0] : fname // [1] : lname // [2] : id // [3] : passwd
                input_str[0] = this.ID.getText().toString();
                input_str[1] = this.First_name.getText().toString();
                input_str[2] = this.Last_name.getText().toString();
                input_str[3] = this.E_mail.getText().toString();
                input_str[4] = this.Password.getText().toString();

                for(int i = 0 ;i< 5; i++) {
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
                String url = "http://teamf-iot.calit2.net/user/signup";
                String values = null;
                for(int i=0;i<5;i++) {
                    values = values + JSON_base[i];
                    values = values + input_str[i];
                    if(i!=4) {
                        values = values + "&";
                    }
                }
                RegisterActivity.NetworkTask networkTask = new RegisterActivity.NetworkTask(url, values);
                networkTask.execute();
            }
        }
    }
}
