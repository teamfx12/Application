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

public class DeleteAccountActivity extends AppCompatActivity {

    protected EditText Text_Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);
        Text_Password = findViewById(R.id.Password);
    }

    public class NetworkTaskDelete extends AsyncTask<Void, Void, String> {

        private String url;
        private String values;

        public NetworkTaskDelete(String url, String values) {
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
                    Msg = "Thank you for using our Application";
                    ShowDialog(title, Msg);
                    return;
                } else {
                    Msg = "Msg : " + json_result.getString("msg");
                }
            } catch (JSONException e) {
                title = "Error";
                Msg = "JSON parsing Error";
            }
            ShowDialog(title, Msg);
        }
        private void ShowDialog(final String title, String Msg){
            AlertDialog.Builder ad = new AlertDialog.Builder(DeleteAccountActivity.this);
            ad.setTitle(title);
            ad.setMessage(Msg);
            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if(title == "ok") {
                        Intent toLogin = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(toLogin);
                        finish();
                    }
                }
            });
            ad.show();
        }
    }
    public void onClickDelete(View view){
        switch (view.getId()){
            case R.id.btnDelete: {
                String function = "function=delet-account&";
                String pw ="pw="+this.Text_Password.getText().toString();
                String token = "token="+GlobalVar.getToken();
                String url = "http://teamf-iot.calit2.net/user";
                String values = function+pw+"&"+token;

                if (pw.length() == 0) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(DeleteAccountActivity.this);
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

                AlertDialog.Builder check = new AlertDialog.Builder(DeleteAccountActivity.this);
                check.setTitle("Check");
                check.setMessage("Are you sure to delete??");
                check.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                check.show();

                NetworkTaskDelete networkTaskDelete = new NetworkTaskDelete(url, values);
                networkTaskDelete.execute();
            }
        }
    }
}