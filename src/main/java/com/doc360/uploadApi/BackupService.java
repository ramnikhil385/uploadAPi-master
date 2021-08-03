package com.doc360.uploadApi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.doc360.apibridge.utility.CommonUtils;
import com.doc360.elastic.resource.RioConnector;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class BackupService {
	@Autowired
	private RioConnector rioOOSConnector;
	
	@Value("${OOSUpload.enable}")
	private String OOSUploadEnable;
	
	@Value("${backup.local.content-root}")
	private String backupPath;
	/**
	 * Clear all the cache in this application.
	 */
	@Scheduled(cron = "${backup.poller.cron}")
	public void backupService() {
		String hostName = CommonUtils.getHostName();
		log.info("Starting Backing service on host {}",hostName);
		Path root = Paths.get(backupPath);
		try {
			if(StringUtils.equalsIgnoreCase(OOSUploadEnable, "true")) {
			Files.walk(root)
						.filter(Files::isRegularFile)
							.filter(p -> p.toFile().length() > 0)
							.forEach(p-> {
									try {
										rioOOSConnector.putFile(p.toFile(), p.getParent().toString());
									} catch (IOException e) {
										e.printStackTrace();
										log.error("Error Occured running Backup service job {} {}",p, e);
									} catch (Exception e) {
										e.printStackTrace();
										log.error("Error Occured running Backup service job {} {}",p, e);
									}
							});
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.error("Error Occured running Backup service job {} {}", root, e);
		}

	
	}

}