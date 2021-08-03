package com.doc360.elastic.resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OOSProperties {

    @Value("${rio.oos.root-folder}")
    private String rootFolder;
    
    @Value("${rio.oos.access-key-id}")
    private String rioAccessKeyId;

    @Value("${rio.oos.secret-access-key}")
    private String rioAccessSecretKey;

    @Value("${rio.oos.bucket-name}")
    private String rioBucketName;

    @Value("${rio.oos.s3-end-point}")
    private String rioS3EndPoint;

    @Value("${rio.oos.root-folder}")
    private String rioRootFolder;


    public String getRioAccessKeyId() {
		return rioAccessKeyId;
	}

	public String getRioAccessSecretKey() {
		return rioAccessSecretKey;
	}

	public String getRioBucketName() {
		return rioBucketName;
	}

	public String getRioS3EndPoint() {
		return rioS3EndPoint;
	}

	public String getRioRootFolder() {
		return rioRootFolder;
	}

    public String getRootFolder() {
        return rootFolder;
    }
}