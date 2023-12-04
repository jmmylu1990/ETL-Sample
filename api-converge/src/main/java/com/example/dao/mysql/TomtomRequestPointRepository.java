package com.example.dao.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dao.GenericScheduleJobRepository;
import com.example.model.entity.mysql.ScheduleJob;
import com.example.model.entity.mysql.TomtomRequestPoint;

@Repository
public interface TomtomRequestPointRepository extends JpaRepository<TomtomRequestPoint, Integer> {

}
