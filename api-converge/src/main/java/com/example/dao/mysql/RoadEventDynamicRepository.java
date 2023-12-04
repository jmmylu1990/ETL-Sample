package com.example.dao.mysql;

import com.example.model.entity.mysql.RoadEventDynamic;
import com.example.model.entity.mysql.pk.RoadEventDynamicKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadEventDynamicRepository extends JpaRepository<RoadEventDynamic, RoadEventDynamicKey> {
}
