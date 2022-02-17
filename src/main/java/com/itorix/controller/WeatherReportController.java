package com.itorix.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.itorix.services.WeatherReportService;

@RestController
@RequestMapping(value = "api/weatherReports")
public class WeatherReportController {

    @Autowired
    private WeatherReportService weatherReportService;

    @PostMapping("getWeatherReportForCity")
    public ResponseEntity<Object> getWeatherReportForCity(@RequestParam(name = "cityName", required = false)String cityName){
        return weatherReportService.getWeatherReportForCity(cityName);
    }
}
