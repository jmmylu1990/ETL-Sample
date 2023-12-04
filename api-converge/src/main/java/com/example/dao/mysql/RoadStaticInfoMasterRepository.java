package com.example.dao.mysql;

import com.example.model.entity.mysql.RoadStaticInfoMaster;
import com.example.model.entity.mysql.pk.RoadStaticInfoMasterKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadStaticInfoMasterRepository extends JpaRepository<RoadStaticInfoMaster, RoadStaticInfoMasterKey> {
}
