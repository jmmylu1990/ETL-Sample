package com.example.dao.mysql;

import com.example.model.entity.mysql.RoadStaticInfoDetail;
import com.example.model.entity.mysql.pk.RoadStaticInfoDetailKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadStaticInfoDetailResposity extends JpaRepository<RoadStaticInfoDetail, RoadStaticInfoDetailKey> {
}
