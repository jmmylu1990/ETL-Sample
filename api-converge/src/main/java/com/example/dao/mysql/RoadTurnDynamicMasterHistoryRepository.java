package com.example.dao.mysql;

import com.example.model.entity.mysql.RoadTurnDynamicMasterHistory;
import com.example.model.entity.mysql.pk.RoadTurnDynamicMasterHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadTurnDynamicMasterHistoryRepository extends JpaRepository<RoadTurnDynamicMasterHistory, RoadTurnDynamicMasterHistoryKey> {
}
