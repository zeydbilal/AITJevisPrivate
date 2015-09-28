/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.batchmode;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Lamprecht Werner
 */
public class SensorMap {

    private InputStream inputStream;
    private String urlToRead;
    private String url;
    private String port;
    private String database;

    public SensorMap() {

    }

    public void connection() {
        URL url;
        HttpURLConnection conn;
        Logger.getLogger(SensorMap.class.getName()).log(Level.INFO, "Host name: " + getUrlToRead());

        try {
            url = new URL(getUrlToRead());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            this.inputStream = conn.getInputStream();

        } catch (MalformedURLException ex) {
            Logger.getLogger(SensorMap.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProtocolException ex) {
            Logger.getLogger(SensorMap.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SensorMap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Map<String, LinkedHashMap<String, String>> getSensorMap() {

        Map<String, LinkedHashMap<String, String>> map = null;
        try {
            String jsonString = IOUtils.toString(inputStream);
            //String jsonTestString ="{\"T\": {  \"MacAddr\": \"28524295050000AB\",  \"Temperature\": \"24.19\",  \"Time\": \"2014-05-26T07:06:43+02:00\"}}";
            Gson gson = new Gson();
            Type complexMap = new TypeToken<Map<String, LinkedHashMap<String, String>>>() {
            }.getType();
            map = gson.fromJson(jsonString, complexMap);
        } catch (IOException ex) {
            Logger.getLogger(SensorMap.class.getName()).log(Level.SEVERE, null, ex);
        }
        return map;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUrl() {
        //http://192.71.247.119
        return url;
    }

    public String getPort() {
        //return "3000";
        return port;
    }

    public String getDatabase() {
        //return "db";
        return database;
    }

    public String getUrlToRead() {
        return this.urlToRead = getUrl() + ":" + getPort() + "/" + getDatabase();
    }
}
