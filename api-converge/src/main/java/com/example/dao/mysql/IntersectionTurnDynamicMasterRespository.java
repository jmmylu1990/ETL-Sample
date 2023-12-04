package com.example.dao.mysql;

import com.example.model.entity.mysql.IntersectionTurnDynamicMaster;
import com.example.model.entity.mysql.pk.IntersectionTurnDynamicMasterKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntersectionTurnDynamicMasterRespository extends JpaRepository<IntersectionTurnDynamicMaster, IntersectionTurnDynamicMasterKey> {
}
