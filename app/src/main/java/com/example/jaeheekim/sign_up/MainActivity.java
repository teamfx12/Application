package com.example.jaeheekim.sign_up;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView User_name;
    private String fname;
    private String lname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);                 // content connection with activity_main
        fname = GlobalVar.getFname();                           //
        lname = GlobalVar.getLname();
        User_name = findViewById(R.id.User_name);
        User_name.setText(fname+" "+lname);
    }

    public void onClick_menu(View view){

    }

    public void onClick_Find_route(View view){
        switch (view.getId()) {
            case R.id.Find_route: {
                Intent to_map = new Intent(getApplicationContext(), temActivity.class);
                startActivity(to_map);
            }
        }
    }

    public void onClick_current(View view){
        switch (view.getId()) {
            case R.id.Current: {
                Intent to_Gmap = new Intent(getApplicationContext(), Current_LocationActivity.class);
                startActivity(to_Gmap);
            }
        }
    }
}