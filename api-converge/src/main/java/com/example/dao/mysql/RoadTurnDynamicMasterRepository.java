package com.example.dao.mysql;

import com.example.model.entity.mysql.RoadTurnDynamicMaster;
import com.example.model.entity.mysql.pk.RoadTurnDynamicMasterKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadTurnDynamicMasterRepository extends JpaRepository<RoadTurnDynamicMaster,RoadTurnDynamicMasterKey> {
}
