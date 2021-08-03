/**
 * 
 */
package com.doc360.apibridge.utility;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.doc360.upload.database.ui.ApplicationEntity;
import com.doc360.uploadApi.security.AppIdPreference;
import com.doc360.uploadApi.security.Application;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Mapper bean to convert application entity bean to Application domain model.
 * 
 * @author Tarun Verma
 *
 */
@Service
public class ApplicationMapper implements Mapper<ApplicationEntity, Application> {

	/**
	 * Map the entity bean to domain bean.
	 * @throws IOException 
	 * @throws Exception 
	 * 
	 * @see com.optum.dms.doc360.rest.types.services.mapper.Mapper#map(java.lang.Object)
	 */
	@Override
	public Application map(ApplicationEntity input) throws IOException  {
		if (input == null) {
			return null;
		}
		
		Application application = new Application();
		application.setId(input.getId());
		application.setApplicationDescription(input.getApplicationDescription());
		application.setApplicationId(input.getAppId());
		application.setApplicationUserId(input.getApplicationUserId());
		application.setApplicationPassword(input.getApplicationPassword());
		
		// parse the json structure, and set in the domain object
		if (StringUtils.isNotBlank(input.getPreferences())) {
			try {
				AppIdPreference preferences = new ObjectMapper().readValue(input.getPreferences(), AppIdPreference.class);
				application.setPreferences(preferences);
			} catch (IOException e) {
				throw new IOException("Error occurred while parsing application preferences JSON String.", e);
			}
		}
		return application;
	}

}
