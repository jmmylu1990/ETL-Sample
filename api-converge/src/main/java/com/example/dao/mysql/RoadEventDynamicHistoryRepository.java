package com.example.dao.mysql;

import com.example.model.entity.mysql.RoadEventDynamicHistory;
import com.example.model.entity.mysql.pk.RoadEventDynamicHistoryKey;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Repository
public interface RoadEventDynamicHistoryRepository extends JpaRepository<RoadEventDynamicHistory, RoadEventDynamicHistoryKey> {

}
