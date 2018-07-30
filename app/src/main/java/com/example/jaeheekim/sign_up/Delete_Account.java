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

public class Delete_Account extends AppCompatActivity {

    protected EditText Text_Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete__account);

        Text_Password = findViewById(R.id.Password);
    }

    public class NetworkTask_delete extends AsyncTask<Void, Void, String> {

        private String url;
        private String values;

        public NetworkTask_delete(String url, String values) {
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
                    Msg = "We mailed your password";
                    Show_dialog(title, Msg);
                    Intent intent_main = new Intent(getApplicationContext(), Log_inActivity.class);
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
            AlertDialog.Builder ad = new AlertDialog.Builder(Delete_Account.this);
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

    public void onClickDelete(View view){
        switch (view.getId()){
            case R.id.btnDelete: {
                String function = "function=delete-pw&";
                String pw ="pw="+this.Text_Password.getText().toString();
                String token = GlobalVar.getToken();
                String url = "http://teamf-iot.calit2.net/user";
                String values = function+pw+"&"+token;

                if (pw.length() == 0) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(Delete_Account.this);
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

                AlertDialog.Builder check = new AlertDialog.Builder(Delete_Account.this);
                check.setTitle("Check");
                check.setMessage("Are you sure to delete??");
                check.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                check.show();

                NetworkTask_delete networkTask_delete = new NetworkTask_delete(url, values);
                networkTask_delete.execute();
            }
        }
    }
}