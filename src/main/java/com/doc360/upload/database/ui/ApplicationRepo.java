/**
 * 
 */
package com.doc360.upload.database.ui;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository inteface for ApplicationEntity entity.
 * 
 * @author Tarun Verma
 *
 */
@Repository
public interface ApplicationRepo extends CrudRepository<ApplicationEntity, Integer> {
	/**
	 * Find the application based on app id
	 * 
	 * @param appId
	 * @return ApplicationEntity
	 */
	ApplicationEntity findByAppId(String appId);

	/**
	 * Get all the applications.
	 * 
	 * @see org.springframework.data.repository.CrudRepository#findAll()
	 */
	Iterable<ApplicationEntity> findAll();

}
