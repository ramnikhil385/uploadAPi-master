/**
 * Created on: Jun 16, 2015
 */
package com.doc360.apibridge.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;


import lombok.extern.slf4j.Slf4j;

/**
 * This class is used to define the content type mapping.
 * 
 * @author Tarun Verma
 *
 */
@Component
@Slf4j
public class ContentTypeMapping implements IConstants {

	@Value("${document.content.type.mapping.file}")
	private Resource mappingFile;

	private HashMap<String, AbstractMap.SimpleImmutableEntry<String, String>> contentTypeMap = null;

	/**
	 * Load the content mapping file
	 */
	@PostConstruct
	private void loadContentTypeMapping() {
		if (contentTypeMap == null) {
			contentTypeMap = new HashMap<String, AbstractMap.SimpleImmutableEntry<String, String>>();
			BufferedReader reader = null;
			try {
				log.info("Mapping File: " + mappingFile);

				// read the file
				reader = new BufferedReader(new InputStreamReader(mappingFile.getInputStream()));
				String line = null;
				String[] keyValue = null;
				while ((line = reader.readLine()) != null) {
					keyValue = line.split(",");

					// always enter the first occurrence content type
					if (!contentTypeMap.containsKey(keyValue[0])) {
						// key in the entry would be File Extension, and
						// value would be MIME type
						contentTypeMap.put(keyValue[0],
								new SimpleImmutableEntry<String, String>(keyValue[1], keyValue[2]));
					}
				}

				log.info("Content Type Map: " + contentTypeMap);
			} catch (IOException ioe) {
				log.error("Error during reading the content type mapping file.", ioe);
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException ioe) {
				}
			}
		}
	}

	/**
	 * Get the MIME type based on the content type.
	 * 
	 * @param contentType
	 * @return String
	 */
	public String getMimeType(String contentType) {
		SimpleImmutableEntry<String, String> value = contentTypeMap.get(contentType);
		return (value != null) ? value.getValue() : null;
	}

	/**
	 * Get the file extension based on the content type.
	 * 
	 * @param contentType
	 * @return String
	 */
	public String getFileExtension(String contentType) {
		SimpleImmutableEntry<String, String> value = contentTypeMap.get(contentType);
		return (value != null) ? value.getKey() : null;
	}

	/**
	 * Get the content type based on the file extension.
	 * 
	 * @param fileExtension
	 * @return String
	 */
	public String getContentTypeForFileExtension(String fileExtension) {
		// scan the map
		for (Map.Entry<String, AbstractMap.SimpleImmutableEntry<String, String>> entry : contentTypeMap.entrySet()) {
			// map value is another pair, which key is file extension
			if (fileExtension.equalsIgnoreCase(entry.getValue().getKey())) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Get the MIME type based on the file extension.
	 * 
	 * @param fileExtension
	 * @return String
	 */
	public String getMimeTypeForFileExtension(String fileExtension) {
		// scan the map
		for (AbstractMap.SimpleImmutableEntry<String, String> value : contentTypeMap.values()) {
			// map value is another pair, which key is file extension
			if (fileExtension.equalsIgnoreCase(value.getKey())) {
				return value.getValue();
			}
		}
		return null;
	}
	
	public String getFileExtensionForMimeType(String mimeType) {
		for (Map.Entry<String, AbstractMap.SimpleImmutableEntry<String, String>> entry : contentTypeMap.entrySet()) {
			// map value is another pair, which key is file extension
			if (mimeType.equalsIgnoreCase(entry.getValue().getValue())) {
				return entry.getValue().getKey();
			}
		}
		return null;
	}
	
}
