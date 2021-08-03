package com.doc360.apibridge.utility;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class CommonUtils {

	private static final String JASYPT_CRED = "a$nK.E)nQ2%8GcI";
//	private static final String JASYPT_CRED = "doc360";
//	a$nK.E)nQ2%8GcI}
	private static String HOSTNAME = "localhost";
	/**
	 * Encrypt the passed value.
	 * 
	 * @param passwd
	 * @return String Encrypted string
	 */
	
	static {

		// get the host name
		HOSTNAME = getHostName();
		MDC.put("hostName", HOSTNAME);

	}
	
	public static String encrypt(String passwd) {
		// Encrypt
		BasicTextEncryptor encryptor = new BasicTextEncryptor();
		// This is a required passwd for Jasypt. You will have to use the same
		// passwd to
		// retrieve decrypted passwd later.
		// This passwd is not the passwd we are trying to encrypt taken from
		// properties file.
		encryptor.setPassword(JASYPT_CRED);
		return encryptor.encrypt(passwd);
	}

	/**
	 * Decrypt the passed string.
	 * 
	 * @param encryptPasswd
	 * @return String
	 */
	public static String decrypt(String encryptPasswd) {
		// decrypt
		BasicTextEncryptor encryptor = new BasicTextEncryptor();
		encryptor.setPassword(JASYPT_CRED);
		return encryptor.decrypt(encryptPasswd);
	}

	public static void main(String[] args) {
		System.out.println(decrypt("C5HhUsZNhP43oRZYTMYDRDbhvQLkPVFt"));
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
	public String buildPasswordDigest(String nonce, String created, String password)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest sha2 = MessageDigest.getInstance(IConstants.ALGORITHM_SHA_256);
		sha2.update(nonce.getBytes(IConstants.CHARSET_UTF_8));
		sha2.update(created.getBytes(IConstants.CHARSET_UTF_8));
		sha2.update(password.getBytes(IConstants.CHARSET_UTF_8));
		String passwordDigest = new String(Base64.encodeBase64(sha2.digest()));
		// log.debug("Password Digest with SHA2: {}", passwordDigest);
		sha2.reset();

		return passwordDigest;
	}
	

	public static String getHostName() {
		Process process = null;
		String hostname = null;
		if (System.getProperty("os.name").startsWith("Windows")) {
			/* Windows will always set the 'COMPUTERNAME' variable */
			hostname = System.getenv("COMPUTERNAME");
		} else {
			/*
			 * If it is not Windows then it is most likely a Unix-like operating
			 * system such as Solaris, AIX, HP-UX, Linux or MacOS.
			 * 
			 * Most modern shells (such as Bash or derivatives) sets the
			 * HOSTNAME variable so lets try that first.
			 */
			hostname = System.getenv("HOSTNAME");
			if (hostname != null) {
				return hostname;
			} else {
				/*
				 * If the above returns null *and* the OS is Unix-like then you
				 * can try an exec() and read the output from the 'hostname'
				 * command which exist on all types of Unix/Linux.
				 */
				try {
					process = Runtime.getRuntime().exec("hostname");
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
					hostname = stdInput.readLine();

				} catch (IOException e) {
					log.error("Unable to get the local host name; using default value.");
				}
			}
		}
		return hostname;
	}
}
