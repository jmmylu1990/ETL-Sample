package com.example.dao.mysql;

import com.example.model.entity.mysql.IntersectionStaticInfoMaster;
import com.example.model.entity.mysql.pk.IntersectionStaticInfoMasterKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntersectionStaticInfoMasterRepository extends JpaRepository<IntersectionStaticInfoMaster, IntersectionStaticInfoMasterKey> {
}
