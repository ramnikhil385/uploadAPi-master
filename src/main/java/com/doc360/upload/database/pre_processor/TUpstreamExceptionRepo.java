package com.doc360.upload.database.pre_processor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TUpstreamExceptionRepo extends JpaRepository<TUpstreamException, Integer> {
}
