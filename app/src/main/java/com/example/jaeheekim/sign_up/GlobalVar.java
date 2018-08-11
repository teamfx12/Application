package com.example.jaeheekim.sign_up;

import android.annotation.TargetApi;
import android.app.Application;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.os.Build;

import com.google.android.gms.maps.model.LatLng;

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
    private static int heartRate;
    private static int pnnPercent;
    private static boolean flag = true;
    private static String historicalData = null;
    private static String realTimeData = null;

    private static int DataSize = 100;
    private Location currentBestLocation = null;
    private static LatLng mLocation;

    @Override
    public void onCreate(){

        super.onCreate();
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
    }

    public static void setHistoricalData(String historicalData){
        GlobalVar.historicalData = GlobalVar.historicalData + historicalData;
    }
    public static String getHistoricalData(){ return historicalData; }

    public static void setRealTimeData(String realTimeData){
        GlobalVar.realTimeData = GlobalVar.realTimeData + realTimeData;
    }
    public static String getRealTimeData(){ return realTimeData; }

    public static String getToken(){
        return token;
    }

    public static void setToken(String token){
        GlobalVar.token = token;
    }

    public static LatLng getmLocation(){
        return mLocation;
    }

    public static void setmLocation(LatLng location){
        GlobalVar.mLocation = location;
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

    public static boolean getFlag(){
        return flag;
    }

    public static void setFlag(boolean flag){
        GlobalVar.flag = flag;
    }

    public static int getHeartRate(){
        return heartRate;
    }

    public static void setHeartRate(int heartRate, int pnnPercent){
        GlobalVar.heartRate = heartRate;
        GlobalVar.pnnPercent =  pnnPercent;
    }

    public static int getPnnPercent() { return pnnPercent;}

    public static void setTokenExpire(Date tokenExpire) { GlobalVar.tokenExpire = tokenExpire; }

    public static boolean isTokenExpired() throws ParseException {
        if(currentDate().before(GlobalVar.tokenExpire))
            return true;
        else
            return false;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void makeTokenExpired() throws ParseException {
        currentDate = currentDate();
        GlobalVar.tokenExpire = currentDate;
        GlobalVar.token = "";
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static Date currentDate() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        currentDate = format.parse(format.format(calendar.getTime()));

        return currentDate;
    }
}
