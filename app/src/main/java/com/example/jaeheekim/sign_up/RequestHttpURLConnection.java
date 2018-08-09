package com.example.jaeheekim.sign_up;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequestHttpURLConnection {
    public String request(String _url, String _params){
        HttpURLConnection urlConn = null;
        //String body = "firstName=GEONUNG&lastName=CHO&email=fakem1333@gmail.com&passwd=apple";
        //String body = _params.toString();
        String body = _params;
        try{
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Accept-Charset", "UTF-8");
            urlConn.setRequestProperty("Context_Type", "application/json");
            urlConn.setConnectTimeout(15000);
            urlConn.setReadTimeout(10000);
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            OutputStream os;
            try {
                os = urlConn.getOutputStream();
                os.write( body.getBytes("UTF-8") );
                os.flush();
                os.close();
            }catch (IOException e){
                return null;
            }

            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));
            String buf = reader.readLine().toString();
            return buf;
        } catch (MalformedURLException e) { // for URL.
            e.printStackTrace();
        } catch (IOException e) { // for openConnection().
            e.printStackTrace();
            return "ERROR";
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }
        return null;
    }
}
