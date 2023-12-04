package com.example.dao.mysql;

import com.example.model.entity.mysql.RoadLaneDynamicLane;
import com.example.model.entity.mysql.pk.RoadLaneDynamicLaneKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadLaneDynamicLaneRepository extends JpaRepository<RoadLaneDynamicLane, RoadLaneDynamicLaneKey> {
}
