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

public class Change_PasswordActivity extends AppCompatActivity {

    protected EditText Current_PW;
    protected EditText New_PW;
    protected EditText Re_New_PW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change__password);

        Current_PW = findViewById(R.id.CurrentPW);
        New_PW = findViewById(R.id.NewPW);
        Re_New_PW = findViewById(R.id.ReNewPW);
    }

    public class NetworkTask_change extends AsyncTask<Void, Void, String> {

        private String url;
        private String values;

        public NetworkTask_change(String url, String values) {
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
                    Msg = "Your password is changed successfully";
                    Show_dialog(title, Msg);
                    Intent intent_main = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent_main);
                    return;
                } else {
                    Msg = "Msg : " + json_result.getString("msg");
                }
            } catch (JSONException e) {
                title = "Error";
                Msg = "JSON parsing Error";
            }
            Show_dialog(title, Msg);
        }
        private void Show_dialog(String title, String Msg){
            AlertDialog.Builder ad = new AlertDialog.Builder(Change_PasswordActivity.this);
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

    public void onclick_find(View view){
        switch (view.getId()){
            case R.id.Find: {
                String function = "function=change-pw&";
                String current_pw ="currentpw="+this.Current_PW.getText().toString();
                String new_pw = "newpw="+this.New_PW.getText().toString();
                String re_new_pw = this.Re_New_PW.getText().toString();
                String url = "http://teamf-iot.calit2.net/user";
                String values = function+current_pw+"&"+new_pw;

                if (current_pw.length() == 0 || new_pw.length() == 0 || re_new_pw.length() == 0) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(Change_PasswordActivity.this);
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
                if (!current_pw.equals(re_new_pw)){
                    AlertDialog.Builder ad = new AlertDialog.Builder(Change_PasswordActivity.this);
                    ad.setTitle("PassWord Error");
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
                NetworkTask_change networkTask_change = new NetworkTask_change(url, values);
                networkTask_change.execute();
            }
        }
    }
}
