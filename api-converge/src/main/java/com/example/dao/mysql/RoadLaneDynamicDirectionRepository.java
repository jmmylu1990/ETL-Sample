package com.example.dao.mysql;

import com.example.model.entity.mysql.RoadLaneDynamicDirection;
import com.example.model.entity.mysql.pk.RoadLaneDynamicDirectionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadLaneDynamicDirectionRepository extends JpaRepository<RoadLaneDynamicDirection, RoadLaneDynamicDirectionKey> {
}
