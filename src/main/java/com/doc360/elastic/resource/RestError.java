/**
 * Created on: Jun 23, 2015
 */
package com.doc360.elastic.resource;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

/**
 * This class is used to report errors to REST API clients.
 * 
 * @author Tarun Verma
 *
 */
public class RestError implements Serializable {

	private static final long serialVersionUID = 1L;

	private HttpStatus status;
	private String message;
	private String requestURI;

	/**
	 * Constructor.
	 * 
	 * @param status
	 * @param message
	 * @param requestURI
	 */
	public RestError(HttpStatus status, String message, String requestURI) {
		this.status = status;
		this.message = message;
		this.requestURI = requestURI;
	}

	/**
	 * @return the status
	 */
	public HttpStatus getStatus() {
		return status;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the requestURI
	 */
	public String getRequestURI() {
		return requestURI;
	}

}
