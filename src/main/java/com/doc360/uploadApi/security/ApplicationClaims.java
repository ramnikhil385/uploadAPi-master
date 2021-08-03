package com.doc360.uploadApi.security;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Placeholder bean for all security information received from JWT token.
 * 
 * @author Sudheer Rangaboina, Tarun Verma
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@NoArgsConstructor
@Getter
@Setter
public class ApplicationClaims implements Serializable {
	private static final long serialVersionUID = 1L;

	private String appId;
	private String userName;
	private String actor;
	private String domain;
	private String created;
	private String nonce;
	private String passwordDigest;
	private String edssUserName;
	private String edssUserCred;
}
