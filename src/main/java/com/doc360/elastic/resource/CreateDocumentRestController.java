package com.doc360.elastic.resource;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.doc360.api.exception.CreateDocException;
import com.doc360.apibridge.utility.ContentTypeMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/doc360/api/${rest.api.version}/types")
@CrossOrigin(origins = "*")
public class CreateDocumentRestController {
	 
	@Autowired
	private ApplicationContext appContext;

	@Autowired
	RestHighLevelClient rioRestHighLevelClient;
	
	@Autowired
	UploadFileProcessor upload;
	
	@Autowired
	ContentTypeMapping contentTypeMapping;
	
	@Value("${multipart.restrictFileTypes}")
	private String supportedExtension;
	
	@Value("${OOSUpload.enable}")
	private String OOSUploadEnable;
	
	@Value("${backup.local.content-root}")
	private String backupPath;
	
	@PostMapping("/{docClassName}/documents/create")
	@CrossOrigin
	public Callable<UploadResponseManager> insert(@RequestHeader HttpHeaders httpHeaders, @PathVariable("docClassName") final String docClassName, @RequestPart("metaData") String metaDataJson,
			@RequestPart("file1") MultipartFile uploadFile) {
		 return new Callable<UploadResponseManager>() {

			@Override
			public UploadResponseManager call() throws Exception {
				log.info("File uploading started", uploadFile.getOriginalFilename());
				String contentType = checkSupportedExtensions(uploadFile);
				String u_compound_doc_id = UUID.randomUUID().toString();
				if(StringUtils.equalsIgnoreCase(OOSUploadEnable, "true")) {
					backupOrginalFile(docClassName, uploadFile);
				}
				UploadFileProcessor upload= appContext.getBean(UploadFileProcessor.class);
				upload.init(uploadFile, httpHeaders);
				JSONObject json = initializeJson(metaDataJson, u_compound_doc_id, docClassName, contentType);
				return upload.processMetadata(json);
			}
		};
		
	}

	private String checkSupportedExtensions(MultipartFile uploadFile) throws Exception {
		String extension = FilenameUtils.getExtension(uploadFile.getOriginalFilename());
		//kbansal3 added lowercase to fix extension case issue
		if (!supportedExtension.contains(StringUtils.lowerCase(extension))) {
			throw new CreateDocException("File Extension Not Supported");
		}
		String contentType = contentTypeMapping.getContentTypeForFileExtension(extension);

		return contentType;
	}

	private JSONObject initializeJson(String metaDataJson, String u_compound_doc, String docClassName, String contentType) throws ParseException {
		JSONObject json = new JSONObject();
		JSONParser parser = null;
		try {
			parser = new JSONParser();
			JSONObject jsonOrginal = (JSONObject) parser.parse(metaDataJson);
			jsonOrginal.keySet().forEach(keyStr ->
			{
				Object keyvalue = jsonOrginal.get(keyStr);
				keyvalue= StringUtils.upperCase(keyvalue!=null ?keyvalue.toString():"");
				json.put(keyStr, keyvalue);

			});
			
			log.info("initialize meta Data with plolicy number: ",json.get("u_policy_num") !=null ? json.get("u_policy_num") : null);
			log.info("GLOBAL_DOC_ID is :" + u_compound_doc );
			json.put(MetadataFields.GLOBAL_DOC_ID, u_compound_doc);
			json.put(MetadataFields.CONTENT_TYPE, StringUtils.remove(contentType, "."));
			json.put(MetadataFields.DOCUMENT_CLASS, docClassName);

		}
		catch(ParseException e) {
			log.error("{} {} {}","Error occured during parsing Meta data", metaDataJson, e.getMessage());
			throw e;
		}
		return json;
	}
	
	private void backupOrginalFile(String docClassName, MultipartFile originalFile) throws Exception {

		File destinationDir = new File(backupPath + "/" + docClassName);
		try {
			synchronized (this) {
				if (!destinationDir.exists() && !destinationDir.mkdirs()) {
						throw new IOException("Failed to create " + destinationDir.getAbsolutePath());
				}
			}
			File newFile = new File(backupPath + "/" + docClassName + "/" + originalFile.getOriginalFilename());
			FileUtils.copyInputStreamToFile(originalFile.getInputStream(), newFile);
		} catch (Exception e) {
			log.error("Error occured during back up of file to file server in backupOrginalFile() method {} {}", e.getMessage(), destinationDir);
			throw e;
		}

	}
}
