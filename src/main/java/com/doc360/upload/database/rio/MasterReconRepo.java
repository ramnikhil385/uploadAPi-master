package com.doc360.upload.database.rio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterReconRepo extends JpaRepository<MasterRecon, Integer> {
    MasterRecon findByBatchFile(String batchFile);
    List<MasterRecon> findByStatus(String status);
}
