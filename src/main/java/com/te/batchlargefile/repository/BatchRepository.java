package com.te.batchlargefile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.te.batchlargefile.entity.DataFile;

@Repository
public interface BatchRepository extends JpaRepository<DataFile, Integer> {

}
