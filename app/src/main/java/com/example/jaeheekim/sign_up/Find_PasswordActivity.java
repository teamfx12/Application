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

public class Find_PasswordActivity extends AppCompatActivity {

    protected EditText Text_Fname;
    protected EditText Text_Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find__password);

        Text_Fname=findViewById(R.id.First_name);
        Text_Email=findViewById(R.id.E_mail);
    }

    public class NetworkTask_find extends AsyncTask<Void, Void, String> {

        private String url;
        private String values;

        public NetworkTask_find(String url, String values) {
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
                    Msg = "We mailed your password";
                    Show_dialog(title,Msg);
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
            AlertDialog.Builder ad = new AlertDialog.Builder(Find_PasswordActivity.this);
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
                String function = "function=find-pw&";

                String fname ="fname="+this.Text_Fname.getText().toString()+"&";
                String email ="email="+this.Text_Email.getText().toString();
                String url = "http://teamf-iot.calit2.net/user";
                String values = function+fname+email;

                if (email.length() == 0 || fname.length() == 0) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(Find_PasswordActivity.this);
                    ad.setTitle("Text Error");
                    ad.setMessage("Please Enter your First name or Email");
                    ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ad.show();
                    return;
                }
                NetworkTask_find networkTask = new NetworkTask_find(url, values);
                networkTask.execute();
            }
        }
    }
}
