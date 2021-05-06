package com.example.findingnearplaces.bojo;

import android.text.InputType;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//this class will retrieve the data from URL using HTTP URL Connection
public class DownLoadUrl {
    //this method will return the json format and get the data using HTTP connection
    public String ReadData(String placeUrl) throws IOException {
        String Data = "";
        //to read url we need file to handle methods
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;


        try {

            URL url = new URL(placeUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            //to read the data in URL
            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            //read each line one by one
             String line = " " ;
             while ((line = bufferedReader.readLine())!=null){
                 stringBuffer.append(line);

             }
             Data = stringBuffer.toString();
             bufferedReader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            inputStream.close();
            httpURLConnection.disconnect();
        }
        return Data;
    }

}
