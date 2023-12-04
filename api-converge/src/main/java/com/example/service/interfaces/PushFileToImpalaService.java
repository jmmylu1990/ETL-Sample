package com.example.service.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.example.exception.ResourceException;

import java.io.IOException;
import java.text.ParseException;

public interface PushFileToImpalaService {

    public void pushRoadmgmtTtimeTheLastDay();

    public void pushFileToFileServer();

    public void pushFileToFileServer(String jobId);



}
