package com.example.jaeheekim.sign_up;

import com.google.android.gms.maps.model.LatLng;

public class PolarInfo {
    private static String ID;

    public PolarInfo(String ID){

        PolarInfo.ID = ID;
    }

    public static void setID (String ID) {
        PolarInfo.ID = ID;
    }
    public static String getID() {
        return PolarInfo.ID;
    }
}
