/**
 * 
 */
package com.doc360.uploadApi.security;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Domain class to represent application.
 * 
 * @author Tarun Verma
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@NoArgsConstructor
@Getter
@Setter
public class Application implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;

	private String applicationId;

	private String applicationDescription;

	private String applicationPassword;

	private String applicationUserId;

	private AppIdPreference preferences;

}
