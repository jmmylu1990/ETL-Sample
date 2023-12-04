package com.example.dao.mysql;

import com.example.model.entity.mysql.RoadTurnDynamicDirectionHistory;
import com.example.model.entity.mysql.pk.RoadTurnDynamicDirectionHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadTurnDynamicDirectionHistoryRepository extends JpaRepository<RoadTurnDynamicDirectionHistory, RoadTurnDynamicDirectionHistoryKey> {
}
