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

import org.apache.commons.lang3.time.FastDateFormat;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;

public class JWTTokenClient {
	private static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'.'SSS'Z'";
    private static final FastDateFormat ISO_DATE_FORMATTER = FastDateFormat.getInstance(ISO_DATE_FORMAT,
                  TimeZone.getTimeZone(ZoneOffset.UTC));
    public static final String JWT_TOKEN_ISSUER = "DOC360_API_CLIENT";
    public static final String JWT_CLAIM_APPID = "appId";
    public static final String JWT_CLAIM_USERNAME = "username";
    public static final String JWT_CLAIM_CRED_DIGEST = "passworddigest";
    public static final String JWT_CLAIM_NONCE = "nonce";
    public static final String JWT_CLAIM_CREATED = "created";
    public static final String JWT_CLAIM_ACTOR = "actor";
    public static final String JWT_CLAIM_DOMAIN = "domain";
	private static final String UTF_8 = "UTF-8";
	private static final String SHA_256 = "SHA-256";

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

           // create password digest   appPassword is provided to the application development team
           String passwordDigest = buildPasswordDigest(nonce, created, appPassword);

           // create the jwt token and pass all the required claims
           Date expDate = Date.from(Instant.now().plus(5l, ChronoUnit.MINUTES));
           // build the JWT token for the user
           Algorithm algorithm = Algorithm.HMAC256(secretToken);   // will be provided to the client’s development team
           JWTCreator.Builder jwt = JWT.create();
           jwt.withIssuer(JWT_TOKEN_ISSUER);
           jwt.withExpiresAt(expDate);
           jwt.withClaim(JWT_CLAIM_USERNAME, userid);
           jwt.withClaim(JWT_CLAIM_ACTOR, userid);
           jwt.withClaim(JWT_CLAIM_APPID, appId);                  // app id is generated specific to the client
           jwt.withClaim(JWT_CLAIM_DOMAIN, domain);
           jwt.withClaim(JWT_CLAIM_NONCE, nonce);
           jwt.withClaim(JWT_CLAIM_CREATED, created);
           jwt.withClaim(JWT_CLAIM_CRED_DIGEST, passwordDigest);

           // return the token
           return jwt.sign(algorithm);
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
//           System.out.println("Password Digest with SHA2: " + passwordDigest);
           sha2.reset();

           return passwordDigest;
    }

}
