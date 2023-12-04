package com.example.model.entity.mysql.pk;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Embeddable
public @Data class RoadEventDynamicHistoryKey implements Serializable {

    @Column(name = "road_id")
    private String roadID;

    @Column(name = "object_id")
    private String objectId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date srcUpdateTime;

}