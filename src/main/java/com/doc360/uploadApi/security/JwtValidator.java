package com.doc360.uploadApi.security;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.doc360.apibridge.utility.CommonUtils;
import com.doc360.apibridge.utility.IConstants;
import com.doc360.upload.database.rio.UploadDBService;
import com.doc360.uploadApi.security.config.ApplicationSecurityConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is used to parse JWT token sent in the request header/parameter
 * with Atompub calls.
 * 
 * @author Sudheer Rangaboina
 *
 */
@Slf4j
@Component
public class JwtValidator implements IConstants {

	@Autowired
	private ApplicationSecurityConfig applicationSecurityConfig;

	@Autowired
	private UploadDBService doc360TypesService;

	@Autowired
	private CommonUtils doc360Util;

	/**
	 * Validate the passed token, and populate the values in ApplicationClaims.
	 * 
	 * @param token
	 * @return ApplicationClaims
	 * @throws IOException 
	 */
	public ApplicationClaims getApplicationClaims(String token) throws IOException {

		try {
			// prepare the algorithm with one secret key
			Algorithm algorithm = Algorithm.HMAC256(applicationSecurityConfig.getSecurityCred());
			// create verifier for given issuer
			JWTVerifier verifier = JWT.require(algorithm).withIssuer(applicationSecurityConfig.getTokenIssuerName())
					.build();

			// decode the passed token
			DecodedJWT jwt = verifier.verify(token);

			// prepare the claims map retrieved from token
			Map<String, Claim> claimsMap = jwt.getClaims();

			// gather all the values from claims map
			if (claimsMap != null && !claimsMap.isEmpty()) {
				// place holder for all claims
				ApplicationClaims appClaims = new ApplicationClaims();

				// extract the application claim values
				String appID = getClaim(JWT_CLAIM_APPID, claimsMap);
				String nonce = getClaim(JWT_CLAIM_NONCE, claimsMap);
				String createdTimestamp = getClaim(JWT_CLAIM_CREATED, claimsMap);
				String passwordDigest = getClaim(JWT_CLAIM_CRED_DIGEST, claimsMap);

				// use the application preference based on app id
				Application application = doc360TypesService.findByApplicationId(appID);
				if (application == null || application.getPreferences() == null) {
					throw new SecurityException(
							"Invalid application id value provided in the application claims: " + appID);
				}
				AppIdPreference appIdpreference = application.getPreferences();

				// validate passwordDigest if key contains, and securityCheck
				// for APPID is true
				if (claimsMap.containsKey(JWT_CLAIM_CRED_DIGEST) && appIdpreference.isSecurityCheck()) {
					// validate passwordDigest
					boolean isValid = validate(nonce, createdTimestamp, application.getApplicationPassword(),
							passwordDigest);
					if (!isValid) {
						throw new SecurityException("Invalid password digest provided in the application claims.");
					}
				}

				// save the values sent in JWT token
				appClaims.setAppId(appID);
				appClaims.setNonce(nonce);
				appClaims.setPasswordDigest(passwordDigest);
				appClaims.setCreated(createdTimestamp);
				appClaims.setUserName(getClaim(JWT_CLAIM_USERNAME, claimsMap));
				appClaims.setActor(getClaim(JWT_CLAIM_ACTOR, claimsMap));
				appClaims.setDomain(getClaim(JWT_CLAIM_DOMAIN, claimsMap));
				appClaims.setEdssUserName(getClaim(JWT_CLAIM_DOMAIN, claimsMap, false));
				appClaims.setEdssUserCred(getClaim(JWT_CLAIM_DOMAIN, claimsMap, false));
				// return the app claims to the caller
				return appClaims;
			} else {
				throw new SecurityException("Invalid client access. Application claims not found in the token.");
			}
		} catch (JWTVerificationException ex) {
			log.error("Exception while validating JWT Token", ex);
			throw ex;
		}

	}

	/**
	 * Get the required claim for the given key.
	 * 
	 * @param claimName
	 * @param claimMap
	 * @return String
	 */
	private String getClaim(String claimName, Map<String, Claim> claimMap) {
		return getClaim(claimName, claimMap, true);
	}

	/**
	 * Get the desired claim for the given key.
	 * 
	 * @param claimName
	 * @param claimMap
	 * @param isRequired
	 * @return String
	 */
	private String getClaim(String claimName, Map<String, Claim> claimMap, boolean isRequired) {

		Claim claim = claimMap.get(claimName);
		if (claim == null) {
			if (isRequired) {
				throw new InvalidClaimException(claimName + " is not present in the token");
			} else {
				return null;
			}
		} else {
			String claimValue = claim.asString();
			if (StringUtils.isNotBlank(claimValue)) {
				return claimValue;
			} else {
				if (isRequired) {
					throw new InvalidClaimException(claimName + " value is empty or not present");
				} else {
					return null;
				}
			}
		}
	}

	/**
	 * Validate the application security based on nonce, created time stamp,
	 * application password, and password digest sent by the client.
	 * 
	 * @param nonce
	 * @param created
	 * @param pwd
	 * @param pwdDigest
	 * @return boolean true if digest was correct, otherwise false
	 */
	private boolean validate(String nonce, String created, String pwd, String pwdDigest) {

		boolean flag = false;
		try {
			// check for value presence first
			if (StringUtils.isBlank(nonce)) {
				throw new SecurityException("Nonce value not found in JWT token.");
			}
			if (StringUtils.isBlank(pwd)) {
				throw new SecurityException("Application password was not set in the configuration.");
			}
			if (StringUtils.isBlank(pwdDigest)) {
				throw new SecurityException("PasswordDigest value not found in JWT token.");
			}

			// generate the digest from passed value
			String digestString = doc360Util.buildPasswordDigest(nonce, created, pwd);

			// compare the passed and new generated digest
			if (digestString.equals(pwdDigest)) {
				log.info("PasswordDigest matched. Valid Token value was passed.");
				return true;
			} else {
				log.debug("PasswordDigest doesn't match. Invalid Token value was passed.");
				throw new SecurityException("Unauthorized Access! Invalid password digest was passed in JWT token.");
			}

		} catch (Exception e) {
			log.error("Error occurred while validating the password digest.", e);
		}
		return flag;
	}

}
