package com.example.jaeheekim.sign_up;

import android.annotation.TargetApi;
import android.app.Application;
import android.icu.text.SimpleDateFormat;
import android.os.Build;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class GlobalVar extends Application{

    private static String token = null;
    private static String email = null;
    private static String fname = null;
    private static String lname = null;
    private static Date tokenExpire;
    private static Date currentDate;

    @Override
    public void onCreate(){

        super.onCreate();
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
    }

    public static String getToken(){
        return token;
    }

    public static void setToken(String token){
        GlobalVar.token = token;
    }

    public static String getEmail(){
        return email;
    }

    public static void setEmail(String email){
        GlobalVar.email = email;
    }

    public static String getFname(){
        return fname;
    }

    public static void setFname(String fname){
        GlobalVar.fname = fname;
    }

    public static String getLname(){
        return lname;
    }

    public static void setLname(String lname){
        GlobalVar.lname = lname;
    }

    public static void setTokenExpire(Date tokenExpire) { GlobalVar.tokenExpire = tokenExpire; }

    @TargetApi(Build.VERSION_CODES.N)
    public static  boolean isTokenExpired(Date tokenExpire) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        currentDate = format.parse(format.format(calendar.getTime()));
        if(currentDate.before(tokenExpire))
            return true;
        else
            return false;
    }
}
