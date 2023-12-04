package com.example.dao.mysql;

import com.example.model.entity.mysql.RoadLaneDynamicDirectionHistory;
import com.example.model.entity.mysql.pk.RoadLaneDynamicDirectionHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadLaneDynamicDirectionHistoryRepository extends JpaRepository<RoadLaneDynamicDirectionHistory, RoadLaneDynamicDirectionHistoryKey> {
}
