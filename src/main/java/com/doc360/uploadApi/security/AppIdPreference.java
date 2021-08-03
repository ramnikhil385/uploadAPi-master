package com.doc360.uploadApi.security;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Domain class for ApplicationEntity preference.
 * 
 * @author Sudheer Rangaboina
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@NoArgsConstructor
@Getter
@Setter
public class AppIdPreference implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean securityCheck;
	private boolean dateConversion;
	private String serverCategory;
	private String serverUserName;
	private String serverPwd;

}
