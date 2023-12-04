package com.example.model.dto.source.other;

import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

public @Data
class RoadmgmtTtime implements Serializable {


    private String seqId;
    private String roadId;
    private String roadDesc;
    private Integer length;
    private Integer tTime;
    private Integer tTimeHistory;
    private Double tTimeDiffRatio;
    private Double speed;
    private Double speedHistory;
    private Double speedTh1;
    private Double speedTh2;
    private Integer moeLevel;
    private Double tti;
    private String groupId;
    private String cronId;
    private String datasourceId;
    private String datasourceType;
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date recvTime;
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date srcUpdateTime;
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date infoTime;
    @CsvFormat(pattern = "yyyy-MM-dd")
    private Date infoDate;
}
