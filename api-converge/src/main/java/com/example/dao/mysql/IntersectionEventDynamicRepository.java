package com.example.dao.mysql;

import com.example.model.entity.mysql.IntersectionEventDynamic;
import com.example.model.entity.mysql.pk.IntersectionEventDynamicKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntersectionEventDynamicRepository extends JpaRepository<IntersectionEventDynamic, IntersectionEventDynamicKey> {
}
