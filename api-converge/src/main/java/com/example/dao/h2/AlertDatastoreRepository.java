package com.example.dao.h2;

import com.example.model.entity.h2.AlertDatastore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertDatastoreRepository extends JpaRepository<AlertDatastore, String> {

}