package com.doc360.elastic.resource;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component("prototype")
public class RioConnector {


	@Autowired
	private OOSProperties oosProperties;

	private AmazonS3 s3Client;

	private final String dateFormat = "MM-dd-yyyy";
	
	/**
	 * Initializes AmazonS3 Client to connect to Optum
	 * Object Storage.  Initializer runs on Post Construct
	 * so that it gets setup when app is starting
	 */
	@PostConstruct
	private void initializeS3Client(){
		BasicAWSCredentials credentials = new BasicAWSCredentials(oosProperties.getRioAccessKeyId(), oosProperties.getRioAccessSecretKey());
		s3Client = AmazonS3ClientBuilder
				.standard()
				.withPathStyleAccessEnabled(Boolean.TRUE)
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(oosProperties.getRioS3EndPoint(), "us-east-1"))
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.build();
	}

	/**
	 * Takes in a files and stores in the Optum Object Storage.
	 * Returns true if object has been pushed successfully
	 *
	 * @param uploadFile - The file to be pushed into Optum Object Storageß
	 * @return - String containing the file The File Path File to be stored to Optum Object Storage
	 * @throws IOException 
	 */
	public String putFile(File uploadFile, String contentGroup) throws Exception {
		if (uploadFile == null || uploadFile.length() <= 0) {
			log.error("File passed to putFile Function is {}", uploadFile);
			return "";
		}
		try {
			String key = getOOSKey(contentGroup) + "/" + uploadFile.getName();
			PutObjectResult result = s3Client
					.putObject(new PutObjectRequest(oosProperties.getRioBucketName(), key, uploadFile));
			if (result != null) {
				log.info("File uploaded to Optum Object Storage: {} Data group {}", uploadFile.getName(),
						contentGroup);
				return oosProperties.getRioBucketName() + "/" + key;
			} else {
				log.info("Retrying upload after wait");
				TimeUnit.MILLISECONDS.sleep(2000);
				result = s3Client.putObject(new PutObjectRequest(oosProperties.getRioBucketName(), key, uploadFile));

				if (null != result) {
					log.info("File uploaded to Optum Object Storage after wait: {} Data group {}",
							uploadFile.getName(), contentGroup);
					return oosProperties.getRioBucketName() + "/" + key;
				}

			}
		} catch (Exception e) {
			log.error("Error occured uploading file to Optum Object Storage: {} Data group {} Exception{}",
					uploadFile.getName(), contentGroup, e);
			throw e;
		} finally {
			if (null != uploadFile && uploadFile.exists()) {
				uploadFile.delete();
			}
		}
		log.error("Could not Upload file to Optum Object Storage. File: {}", uploadFile.getName());
		return "";
	}

	/**
	 * Gets the current date and formats it to MM-dd-yyyy
	 * @return Formatted Date String
	 */
	private String getCurrentDateString(){
		LocalDate date = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		return date.format(formatter);
	}

	/**
	 * Constructs the OOS Key needed to push into
	 * Optum Object Storage
	 *
	 * @return String containing they key needed for OOS
	 */
	private String getOOSKey(String contentGroup){
		return oosProperties.getRioRootFolder() + "/" + getCurrentDateString() + "/" + contentGroup;
	}
}
