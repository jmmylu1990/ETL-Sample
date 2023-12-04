package com.example.dao.mysql;

import com.example.model.entity.mysql.RoadLaneDynamicMaster;
import com.example.model.entity.mysql.pk.RoadLaneDynamicMasterKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadLaneDynamicMasterRespository extends JpaRepository<RoadLaneDynamicMaster, RoadLaneDynamicMasterKey> {
}
