package com.doc360.upload.database.pre_processor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TUpstreamMasterRepo extends JpaRepository<TUpstreamMaster, Integer> {
	int findMaxParentId();
}
