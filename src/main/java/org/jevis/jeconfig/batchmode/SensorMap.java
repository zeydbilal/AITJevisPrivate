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

    static InputStream inputStream;

    public SensorMap(String urlToRead) {

        URL url;
        HttpURLConnection conn;
        Logger.getLogger(SensorMap.class.getName()).log(Level.INFO, "Host name: " + urlToRead);

        try {
            url = new URL(urlToRead);
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

    public static void main(String[] args) {

        SensorMap sensorMapClass = new SensorMap("http://localhost:3001/db");
        Map<String, LinkedHashMap<String, String>> sm = sensorMapClass.getSensorMap(inputStream);

        for (Map.Entry<String, LinkedHashMap<String, String>> sensorEntry : sm.entrySet()) {
            if (sensorEntry.getValue().size() != 3) {
                continue;
            }
            String sensorName = sensorEntry.getKey();
            String unit = (String) sensorEntry.getValue().keySet().toArray()[1];
            System.out.println(sensorName + " + " + unit);
        }
    }

    /**
     *
     * @param inputStream json string
     * @return Map<String, Map<String, String>> returns a map with sensor meta
     * data
     *
     * e.g. "T": { "MacAddr": "28524295050000AB", "Temperature": "sd.22",
     * "Time": "2015-08-09T14:27:43+02:00" }, "RH": { "MacAddr":
     * "28524295050000AB", "Relative Humidity": "33.33", "Time":
     * "2015-08-09T14:27:43+02:00" }
     *
     * for (String key : map.keySet()) {
     *
     * }
     * use the following code to get sensor name and unit
     *
     * for (Map.Entry<String, LinkedHashMap<String, String>> sensorEntry :
     * map.entrySet()) { if(sensorEntry.getValue().size()!=3){ continue; }
     * String sensorName = sensorEntry.getKey(); String unit = (String)
     * sensorEntry.getValue().keySet().toArray()[1];
     * System.out.println(sensorName + " + " + unit); }
     *
     */
    public Map<String, LinkedHashMap<String, String>> getSensorMap(InputStream inputStream) {

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
}
