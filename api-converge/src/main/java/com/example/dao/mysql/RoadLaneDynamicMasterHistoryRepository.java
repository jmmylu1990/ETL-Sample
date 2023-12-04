package com.example.dao.mysql;

import com.example.model.entity.mysql.RoadLaneDynamicMasterHistory;
import com.example.model.entity.mysql.pk.RoadLaneDynamicMasterHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadLaneDynamicMasterHistoryRepository extends JpaRepository<RoadLaneDynamicMasterHistory, RoadLaneDynamicMasterHistoryKey> {
}
