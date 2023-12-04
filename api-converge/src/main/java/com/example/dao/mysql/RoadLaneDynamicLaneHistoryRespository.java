package com.example.dao.mysql;

import com.example.model.entity.mysql.RoadLaneDynamicLaneHistory;
import com.example.model.entity.mysql.pk.RoadLaneDynamicLaneHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadLaneDynamicLaneHistoryRespository extends JpaRepository<RoadLaneDynamicLaneHistory, RoadLaneDynamicLaneHistoryKey> {
}
