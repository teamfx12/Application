package com.example.jaeheekim.sign_up;

import android.app.Application;

import com.google.android.gms.maps.model.LatLng;

public class DeviceInfo extends Application {

    private static String ID;
    private static String name;

    private static LatLng location;

    private static int AQI;
    private static int CO;
    private static int O3;
    private static int SO2;
    private static int NO2;

    public DeviceInfo(String ID, String name, LatLng location, int AQI){

        DeviceInfo.ID = ID;
        DeviceInfo.name = name;
        DeviceInfo.location = location;
        DeviceInfo.AQI = AQI;
    }

    public static void setID (String ID) {
        DeviceInfo.ID = ID;
    }
    public static String getID() {
        return DeviceInfo.ID;
    }

    public static void setName(String name) {
        DeviceInfo.name = name;
    }
    public static String getName() {
        return DeviceInfo.name;
    }

    public static void setLocation(LatLng location) {
        DeviceInfo.location = location;
    }
    public static LatLng getLocation() {
        return DeviceInfo.location;
    }

    public static void setAQI(int AQI) {
        DeviceInfo.AQI = AQI;
    }
    public static int getAQI() {
        return DeviceInfo.AQI;
    }

    public static void setCO(int CO) {
        DeviceInfo.CO = CO;
    }
    public static int getCO() {
        return DeviceInfo.CO;
    }

    public static void setO3(int O3) {
        DeviceInfo.O3 = O3;
    }
    public static int getO3() {
        return DeviceInfo.O3;
    }

    public static void setSO2(int SO2) {
        DeviceInfo.SO2 = SO2;
    }
    public static int getSO2() {
        return DeviceInfo.SO2;
    }

    public static void setNO2(int NO2) {
        DeviceInfo.NO2 = NO2;
    }
    public static int getNO2() {
        return DeviceInfo.NO2;
    }
}
