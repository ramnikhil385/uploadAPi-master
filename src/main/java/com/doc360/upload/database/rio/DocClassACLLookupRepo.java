package com.doc360.upload.database.rio;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface DocClassACLLookupRepo extends JpaRepository<DocClassACLLookup, Integer> {
    @Cacheable("docClassACL")
    List<DocClassACLLookup> findByDocClassName(String docClass);
}
