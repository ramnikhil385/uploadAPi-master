package com.doc360.apibridge.utility;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * This class provides the access to the application context.
 * 
 * @author Sudheer Rangaboina
 *
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

	private static ApplicationContext context;

	/**
	 * Return the application context for this app.
	 * 
	 * @return ApplicationContext
	 */
	public static ApplicationContext getApplicationContext() {
		return context;
	}

	/**
	 * Sets the application context in this app.
	 * 
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		context = ac;
	}
}