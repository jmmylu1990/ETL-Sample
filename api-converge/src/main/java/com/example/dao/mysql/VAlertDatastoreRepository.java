package com.example.dao.mysql;

import com.example.model.entity.mysql.TomtomRequestPoint;
import com.example.model.entity.mysql.VAlertDatastore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VAlertDatastoreRepository extends JpaRepository<VAlertDatastore,String> {
}
