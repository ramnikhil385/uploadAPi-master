package com.doc360.apibridge.utility;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;

/**
 * Utility script to validate the password digest, and generate new JWT Token.
 * 
 * @author Sudheer Rangaboina, Tarun Verma
 *
 */
public class WSSESecurity {

	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(WSSESecurity.class);
	public static final String JWT_TOKEN_ISSUER = "DOC360_API_CLIENT";
	public static final String JWT_CLAIM_APPID = "appId";
	public static final String JWT_CLAIM_USERNAME = "username";
	public static final String JWT_CLAIM_CRED_DIGEST = "passworddigest";
	public static final String JWT_CLAIM_NONCE = "nonce";
	public static final String JWT_CLAIM_CREATED = "created";
	public static final String JWT_CLAIM_ACTOR = "actor";
	public static final String JWT_CLAIM_DOMAIN = "domain";
	public static final String JWT_CLAIM_EDSS_USERNAME = "edssUserName";
	public static final String JWT_CLAIM_EDSS_USERCRED = "edssUserCred";
	private static final String UTF_8 = "UTF-8";
	private static final String SHA_256 = "SHA-256";
	private static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'.'SSS'Z'";
	private static final FastDateFormat ISO_DATE_FORMATTER = FastDateFormat.getInstance(ISO_DATE_FORMAT,
			TimeZone.getTimeZone(ZoneOffset.UTC));

	/**
	 * Validate the password digest value based on nonce, created and
	 * application password.
	 * 
	 * @param nonce
	 * @param created
	 * @param pwd
	 * @param pwdDigest
	 * @return boolean
	 */
	public static boolean validate(String nonce, String created, String pwd, String pwdDigest) {

		boolean flag = false;
		try {
			if (StringUtils.isBlank(nonce)) {
				throw new SecurityException("Nonce value not found in userNameToken Header");
			}
			if (StringUtils.isBlank(pwd)) {
				throw new SecurityException("valid user details match not found");
			}
			if (StringUtils.isBlank(pwdDigest)) {
				throw new SecurityException("passwordDigest value not found in userNameToken Header");
			}
			// generate the digest from passed value
			MessageDigest sha2 = MessageDigest.getInstance(SHA_256);
			sha2.update(Base64.getDecoder().decode(nonce));
			sha2.update(created.getBytes(UTF_8));
			sha2.update(pwd.getBytes(UTF_8));
			String digestString = new String(Base64.getEncoder().encodeToString(sha2.digest()));
			System.out.println("Password Digest with SHA2: " + digestString);
			sha2.reset();

			System.out.println("Nonce: " + nonce + ", Timestamp: " + created + ", Computed digest: " + digestString
					+ ", Provided digest: " + pwdDigest);
			if (digestString.equals(pwdDigest)) {
				System.out.println("validated user successfully");
				return true;
			} else {
				System.out.println("Nonce: " + nonce + ", Timestamp: " + created + ", Computed digest: " + digestString
						+ ", Provided digest: " + pwdDigest);
				throw new SecurityException("User passwordDigest doesn't match");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error occurred while validating the passwordDigest");
		}
		return flag;
	}

	/**
	 * Build the password digest using nonce, created date string and app id
	 * password.
	 * 
	 * @param nonce
	 * @return String
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String buildPasswordDigest(String password)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return buildPasswordDigest(generateNonce(), generateCreated(), password);
	}

	/**
	 * Build the password digest using nonce, created date string and app id
	 * password.
	 * 
	 * @param nonce
	 * @param created
	 * @param password
	 * @return String
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String buildPasswordDigest(String nonce, String created, String password)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest sha2 = MessageDigest.getInstance(SHA_256);
		sha2.update(nonce.getBytes(UTF_8));
		sha2.update(created.getBytes(UTF_8));
		sha2.update(password.getBytes(UTF_8));
		String passwordDigest = new String(Base64.getEncoder().encodeToString(sha2.digest()));
//		System.out.println("Password Digest with SHA2: " + passwordDigest);
		sha2.reset();

		return passwordDigest;
	}

	/**
	 * Generate the Nonce value based on current timestamp
	 * 
	 * @return String
	 */
	public static String generateNonce() {
		return Long.toString(new Date().getTime());
	}

	/**
	 * Generate the created value based on current timestamp
	 * 
	 * @return String
	 */
	public static String generateCreated() {
		return ISO_DATE_FORMATTER.format(new Date());
	}

	public static void main(String[] args) {
		// if (args.length != 4) {
		// System.out.println(
		// "Invalid usage. Need to pass all 4 parameters in the order: nonce,
		// created, pwd and pwdDigest");
		// System.exit(-1);
		// }
		// WSSESecurity.validate(args[0], args[1], args[2], args[3]);

		if (args.length != 5) {
			System.out.println(
					"Invalid usage. Need to pass all 5 parameters in the order: userid, domain, appId, appPassword, secretToken");
			System.exit(-1);
		}
		try {
			System.out.println("JWT: " + WSSESecurity.getJwtToken(args[0], args[1], args[2], args[3], args[4]));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Build the JWT token for the request
	 *
	 * @param userid
	 * @param domain
	 * @param appId
	 * @param appPassword
	 * @param secretToken
	 * @return String
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String getJwtToken(String userid, String domain, String appId, String appPassword, String secretToken)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		// generate the nonce based on current timestamp
		String nonce = generateNonce();
		System.out.println("Nonce: " + nonce);
		String created = generateCreated();
		System.out.println("Created: " + created);

		// create password digest
		String passwordDigest = buildPasswordDigest(nonce, created, appPassword);

		// create the jwt token and pass all the required claims
		Date expDate = Date.from(Instant.now().plus(30l, ChronoUnit.DAYS));
		// build the JWT token for the user
		Algorithm algorithm = Algorithm.HMAC256(secretToken);
		JWTCreator.Builder jwt = JWT.create();
		jwt.withIssuer(JWT_TOKEN_ISSUER);
		jwt.withExpiresAt(expDate);
		jwt.withClaim(JWT_CLAIM_USERNAME, userid);
		jwt.withClaim(JWT_CLAIM_ACTOR, userid);
		jwt.withClaim(JWT_CLAIM_APPID, appId);
		jwt.withClaim(JWT_CLAIM_DOMAIN, domain);
		jwt.withClaim(JWT_CLAIM_NONCE, nonce);
		jwt.withClaim(JWT_CLAIM_CREATED, created);
		jwt.withClaim(JWT_CLAIM_CRED_DIGEST, passwordDigest);

		// return the token
		return jwt.sign(algorithm);
	}
}