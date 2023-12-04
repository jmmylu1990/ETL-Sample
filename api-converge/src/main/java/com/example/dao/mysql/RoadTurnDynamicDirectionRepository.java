package com.example.dao.mysql;

import com.example.model.entity.mysql.RoadTurnDynamicDirection;
import com.example.model.entity.mysql.pk.RoadTurnDynamicDirectionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadTurnDynamicDirectionRepository extends JpaRepository<RoadTurnDynamicDirection, RoadTurnDynamicDirectionKey> {
}
