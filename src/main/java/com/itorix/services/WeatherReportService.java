package com.itorix.services;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequestWithBody;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.omg.CORBA.portable.ApplicationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class WeatherReportService {

    private static final String api_key = "5deac7ea928e658855dc191775919f00";
    private static final String weather_report_url = "https://api.openweathermap.org/data/2.5/weather?q=";
    private static final List<String> cityNames = Arrays.asList("Mumbai", "Chennai", "Delhi", "Kolkata","hello");


    public ResponseEntity<Object> getWeatherReportForCity(String cityName) {
        List<JSONObject> objectList = new ArrayList<>();
        List<String> cityNames = null;
        if (cityName != null && !cityName.trim().isEmpty()) {
            cityNames = Arrays.asList(cityName);
        } else {
            cityNames = this.cityNames;
        }
        long cityNamesSize = cityNames.size() - 1;
        int count = 0;
        while (count <= cityNamesSize) {
            try {
                HttpRequestWithBody request = Unirest.post(weather_report_url + cityNames.get(count) + "&appid=" + api_key);
                HttpResponse<String> response = request.getHttpRequest().asString();
                if (response.getStatus() == 200) {
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(response.getBody());
                    //return ResponseEntity.ok().body(jsonObject);
                    objectList.add(jsonObject);
                    count ++;
                }
                else if(response.getStatus() == 429){
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(response.getBody());
                    return new ResponseEntity<>("Your account is temporary blocked due to exceeding of requests limitation of your subscription type", HttpStatus.BAD_REQUEST);
                }
                else {
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(response.getBody());
                    String error = "";
                    if (jsonObject.containsKey("message")) {
                        error = jsonObject.get("message").toString();
                    }
                    if(error.equalsIgnoreCase("Invalid API key. Please see http://openweathermap.org/faq#error401 for more info.")) {
                        return new ResponseEntity<>("Invalid API key", HttpStatus.BAD_REQUEST);
                    }
                    else {
                        jsonObject.put("name",cityNames.get(count));
                        objectList.add(jsonObject);
                        count ++;
                    }
                }
            } catch (Exception e) {
                return new ResponseEntity<>("some Internal Server Problem please try again later", HttpStatus.BAD_REQUEST);
            }
        }
        return ResponseEntity.ok().body(objectList);
    }

}
