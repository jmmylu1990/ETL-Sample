package com.example.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.example.exception.ResourceException;
import com.example.service.interfaces.PushFileToImpalaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.component.ETLToolComponent;

@RestController
@RequestMapping(value = "/toolApi", produces = MediaType.APPLICATION_JSON)
public class ApiController {

    @Autowired
    private ETLToolComponent etlToolComponent;

    @Autowired
    private PushFileToImpalaService pushFileToImpalaService;

    @GetMapping(value = "/compressETLResult/{jobId}/{infoDate}")
    public long compressETLResult(
            @PathVariable String jobId,
            @PathVariable @DateTimeFormat(pattern = "yyyyMMdd") Date infoDate
    ) {
        return etlToolComponent.compressETLResult(jobId, infoDate, true);
    }

    @GetMapping(value = "/compressETLResult/All/{infoDate}")
    public long compressETLResult(@PathVariable @DateTimeFormat(pattern = "yyyyMMdd") Date infoDate) {
        return etlToolComponent.compressAllETLResult(infoDate, true);
    }

    @GetMapping(value = "/importETLResult/{jobId}/{infoDate}")
    public Map<String, Long> importETLResult(
            @PathVariable String jobId,
            @PathVariable @DateTimeFormat(pattern = "yyyyMMdd") Date infoDate
    ) {
        return etlToolComponent.importETLResult(jobId, infoDate);
    }

    @GetMapping(value = "/importETLResult/All/{infoDate}")
    public Map<String, Long> importETLResult(
            @PathVariable @DateTimeFormat(pattern = "yyyyMMdd") Date infoDate
    ) {
        return etlToolComponent.importETLResult(infoDate);
    }

    @GetMapping(value = "/generateApply")
    public Map<String, Long> generateApply() {

        return etlToolComponent.generateApply();
    }

    @GetMapping(value = "/updataDateStatsForEndDate")
    public Map<String, Object> updataDateStatsForEndDate() {

        return etlToolComponent.updataDatasetStatsForEndDateTest();
    }

    // GVP資料集上架排程
    @GetMapping(value = "/pushRoadmgmtTtimeTheLastDay")
    public void pushRoadmgmtTtimeTheLastDay() {

        pushFileToImpalaService.pushRoadmgmtTtimeTheLastDay();
    }

    // 資料目錄檔案上傳
    @GetMapping(value = "/pushFileToFileServer/{jobId}")
    public void pushFileToFileServer( @PathVariable String jobId){

        pushFileToImpalaService.pushFileToFileServer(jobId);
    }

    @GetMapping(value = "/pushFileToFileServer/all")
    public void pushFileToFileServer(){

        pushFileToImpalaService.pushFileToFileServer();
    }

    }


