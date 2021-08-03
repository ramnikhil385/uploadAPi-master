package com.doc360.elastic.resource;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.doc360.api.exception.CreateDocException;
import com.doc360.apibridge.utility.CommonUtils;
import com.doc360.apibridge.utility.IConstants;
import com.doc360.apibridge.utility.MailUtil;
import com.doc360.upload.database.pre_processor.TUpstreamException;
import com.doc360.upload.database.pre_processor.TUpstreamMaster;
import com.doc360.upload.database.rio.DocClassACLLookup;
import com.doc360.upload.database.rio.MasterRecon;
import com.doc360.upload.database.rio.UploadDBService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Component
@Scope(value = "prototype")
public class UploadFileProcessor {
	private String contentRoot;
	private String date;
	private String dirName;
	private String documentClass;
	private String docId;
	private String originalDate;
	private String parent;
	private String contentFile;
	private String extension;
	private String originalExtension;
	private String destination;
	private String errorFolder;
	private Date receivedTime;
	private JsonArray groupNames;
	private MultipartFile originalFile;
	private JSONObject obj;
	@Value("${output.local.content-root}")
	private String outputPath;
	@Value("${output.local.tmp}")
	private String tempPath;
	@Value("${error.local.content-root}")
	private String errorPath;
	@Autowired
	private Environment environment;
	@Autowired
	private RestHighLevelClient rioRestHighLevelClient;
	@Autowired
	private RestHighLevelClient rioAltTwoRestHighLevelClient;
	@Autowired
	private RestHighLevelClient rioRestElrHighLevelClient;
	@Autowired
	private RestHighLevelClient rioRestElr2HighLevelClient;
	@Value("${enable.elr.host}")
	private String enableELR;
	private String batch_id;
	@Autowired
	private UploadDBService uploadDbService;
	@Autowired
	private MetaDataValidator metaDataValidator;
	@Autowired
	private MailUtil mailUtil;
	private HttpHeaders httpHeaders;

	private long fileSize;

	private MasterRecon masterRecon;
	private TUpstreamMaster upstreamMaster;
	private TUpstreamException upstreamException;
	List<Future<TUpstreamMaster>> futureList = new ArrayList<>();

	public void init(MultipartFile uploadFile, HttpHeaders httpHeaders) {
		this.contentRoot = outputPath;
		this.batch_id = String.valueOf(new SecureRandom().nextInt(1000000));
		this.originalFile = uploadFile;
		this.fileSize = uploadFile.getSize();
		this.receivedTime = new Date(System.currentTimeMillis());
		this.httpHeaders = httpHeaders;
	}

	/**
	 * Patch the metadata for known issues before processing
	 */
	private void preprocessPatch() {
		// If missing original creation date, use today's
		if (obj.get(MetadataFields.ORIGINAL_CREATION_DATE) == null
				|| obj.get(MetadataFields.ORIGINAL_CREATION_DATE).equals(" ")) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			obj.put(MetadataFields.ORIGINAL_CREATION_DATE, simpleDateFormat.format(new Date()));
		}
		// If missing policy number, use a blank space
		if (obj.containsKey(MetadataFields.POLICY_NUMBER) || obj.get(MetadataFields.POLICY_NUMBER) == null) {

			obj.put(MetadataFields.POLICY_NUMBER, " ");

		}
	}

	/**
	 * Initialize variables for metadata processing
	 */
	private void initVariables() {
		this.documentClass = (String) obj.get(MetadataFields.DOCUMENT_CLASS);

		this.date = (String) obj.get(MetadataFields.ORIGINAL_CREATION_DATE);

		String date2 = (String) obj.get(MetadataFields.ORIGINAL_CREATION_DATE);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date3 = new Date();
		try {
			date3 = df.parse(date2);
		} catch (ParseException e) {
			log.error("Error occured during date parse in initVariables() method {} {}", e.getMessage());
		}
		String finalDate = df.format(date3);
		this.dirName = finalDate.substring(0, finalDate.lastIndexOf("-"));

		this.originalDate = obj.get(MetadataFields.ORIGINAL_CREATION_DATE).toString();
		this.contentFile = originalFile.getOriginalFilename();
		this.originalExtension = originalFile.getOriginalFilename()
				.substring(originalFile.getOriginalFilename().lastIndexOf('.'));
		this.extension = this.originalExtension;
		this.destination = this.contentRoot + "/" + documentClass + "/" + finalDate + "/" + batch_id + "/";
		this.errorFolder = this.errorPath + "/" + documentClass + "/" + finalDate + "/" + batch_id + "/";
		this.docId = obj.get(MetadataFields.GLOBAL_DOC_ID).toString();
		// TODO need to get GG from RIO DB
		Gson googleJson = new Gson();
		this.groupNames = googleJson.toJsonTree(this.findGroupsByDocClass(documentClass)).getAsJsonArray();
	}

	/**
	 * Enrich the original metadata object
	 */
	private void enrichMetadata() {
		obj.put(MetadataFields.BATCH_ID, batch_id);
		obj.put(MetadataFields.URL_PATH, System.getenv("PARENT_HOSTNAME") + destination + docId + extension);
		obj.put(MetadataFields.GROUP_NAMES, this.groupNames);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date2 = new Date();
		String date = date2.toString();
		 
		try {
			 date = df.format(date2);
			 date2 = df.parse(date);
		} catch (ParseException e) {
			log.error("Error occured during date parse in enrichMetadata() method {} {}", e.getMessage());
		}
		long epochTimeCreationDate = date2.getTime();
		
		if(!obj.containsKey(MetadataFields.ORIGINAL_CREATION_DATE)) {
			obj.put(MetadataFields.ORIGINAL_CREATION_DATE, epochTimeCreationDate);
		}else if(obj.containsKey(MetadataFields.ORIGINAL_CREATION_DATE)) {
			String date3 = obj.get(MetadataFields.ORIGINAL_CREATION_DATE).toString();
			Date date4 = new Date();
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			df2.setTimeZone(TimeZone.getTimeZone("GMT"));
			if(date3.contains(":")) {
				try {
					date4 = df2.parse(date3);
				} catch (ParseException e) {
					log.error("Error occured during date parse in enrichMetadata() method {} {}", e.getMessage());
				}
				long epoch = date4.getTime();
				obj.put(MetadataFields.ORIGINAL_CREATION_DATE, epoch);
			}else {
				SimpleDateFormat df3 = new SimpleDateFormat("yyyy-MM-dd");
				df3.setTimeZone(TimeZone.getTimeZone("GMT"));
				try {
					date4 = df3.parse(date3);
				} catch (ParseException e) {
					log.error("Error occured during date parse in enrichMetadata() method {} {}", e.getMessage());
				}
				long epoch = date4.getTime();
				obj.put(MetadataFields.ORIGINAL_CREATION_DATE, epoch);
				
			}
		}
	}

	private void manageFiles() throws Exception {
		File destinationDir = new File(destination);
		try {
			synchronized (this) {
				if (!destinationDir.exists() && !destinationDir.mkdirs()) {
						throw new IOException("Failed to create " + destinationDir.getAbsolutePath());
				}
			}
			File newFile = new File(destination + "/" + docId + extension);

			if (!newFile.exists()) {
				log.trace("Moving {}", originalFile.getOriginalFilename());
				FileUtils.copyInputStreamToFile(originalFile.getInputStream(), newFile);
			}
			log.info("Moving {} with content Size {}", originalFile.getOriginalFilename(), newFile.length());
			obj.put(MetadataFields.CONTENT_SIZE, newFile.length());
			if (extension.equalsIgnoreCase(".pdf")
					|| extension.equalsIgnoreCase(".PDF")) {
				PDDocument document = new PDDocument();
				document = PDDocument.load(newFile);
				obj.put(IConstants.PAGE_COUNT_KEY, document.getNumberOfPages());
			}
		} catch (IOException ioex) {
			log.error("Error occured during ingestion to file server {} {}", obj.get(MetadataFields.URL_PATH),
					originalFile.getOriginalFilename());
			moveForReingestion();
			throw new IOException("Error occured during ingestion to file server\n " + obj.get(MetadataFields.URL_PATH)
			+ "\n" + ioex);
		}
		catch (Exception e) {
			log.error("Error occured during ingestion to file server in manageFiles() method {} ", e.getMessage());
			throw new Exception("Error occured during ingestion to file server\n {} " + e);
		} 
	}

	private void moveForReingestion() throws IOException {

		try {
			File newFile = new File(errorFolder + "/" + docId + extension);
			File errorFile = new File(errorFolder + "/" + docId + IConstants.JSON_EXTENSION_CONSTANT);
			FileUtils.writeStringToFile(errorFile, obj.toJSONString());
			FileUtils.copyInputStreamToFile(originalFile.getInputStream(), newFile);
		} catch (Exception e) {
			log.error("Error occured during ingestion to file server in moveForReingestion() method {} {}", e.getMessage());
		}

	}

	/**
	 * Process a single metadata object
	 * 
	 * @throws Exception
	 */
	public UploadResponseManager processMetadata(JSONObject obj) throws Exception {
		this.obj = obj;
		// Patch for known issues
		this.preprocessPatch();
		// Gather required data
		this.initVariables();
		Map<String, List<String>> errorMap = metaDataValidator.validate(documentClass, obj, originalFile.getName());
		if (!errorMap.isEmpty()) {
			prepareUpstreamException(errorMap);
			createTUpstreamMaster(IConstants.FATAL_STATUS);
			return new UploadResponseManager("", new Timestamp(this.receivedTime.getTime()), HttpStatus.SC_BAD_REQUEST,
					new JSONObject(errorMap));
		}
		createTUpstreamMaster(IConstants.COMPLETED_STATUS);
		updateMasterRecon(IConstants.PROCESSING_STATUS);

		// Enrich original metadata
		this.enrichMetadata();
		// Generate ElasticSearch manifest
		try {
			
			 Map<String, String> preferences = this.uploadDbService.getDocClassUIPreferences(this.documentClass);
	            String index = documentClass + "_" + dirName;

	            // Check for index suffix configuration
	            if (preferences != null && preferences.containsKey(MetadataFields.RIO_INDEX_SUFFIX_KEY) && StringUtils.isNotBlank(preferences.get(MetadataFields.RIO_INDEX_SUFFIX_KEY)) && preferences.get(MetadataFields.RIO_INDEX_SUFFIX_KEY).contains("_v")) {
	                    index += preferences.get(MetadataFields.RIO_INDEX_SUFFIX_KEY).substring(preferences.get(MetadataFields.RIO_INDEX_SUFFIX_KEY).lastIndexOf("_v"));
	            }


			this.manageFiles();
			return createESentries(obj, index);

		} catch (Exception e) {
			log.error("Error in processMetadata(): {}", e.getMessage());
			mailUtil.sendMail("Error Occured on- " + CommonUtils.getHostName() + " \n " + e.getMessage());
			updateMasterRecon(IConstants.ERROR_STATUS);
			throw new CreateDocException("Internal Server Error", e);
		}
	}

	private void prepareUpstreamException(Map<String, List<String>> errorMap) {
		upstreamException = new TUpstreamException();
		upstreamException.setXcptDatetime(new Timestamp(System.currentTimeMillis()));
		upstreamException.setXcptStatus(new JSONObject(errorMap).toJSONString());
		upstreamException.setXcptType(IConstants.EXCEPTION_CODE_VALIDATION);
	}

	private UploadResponseManager createESentries(JSONObject obj, String index) throws IOException {

		try {
			IndexRequest indexRequestELR = new IndexRequest(index, MetadataFields.ES_DOCUMENT_TYPE,
					obj.get(MetadataFields.GLOBAL_DOC_ID).toString());
			indexRequestELR.source(obj.toString(), XContentType.JSON);
			log.debug("indexRequestELR ELR ", indexRequestELR);
			IndexResponse responseELR = rioRestHighLevelClient.index(indexRequestELR, RequestOptions.DEFAULT);
			log.debug("Response ELR ", responseELR);
			if(StringUtils.equalsIgnoreCase(enableELR, "true")) {
				IndexRequest indexRequestCTC = new IndexRequest(index, MetadataFields.ES_DOCUMENT_TYPE,
						obj.get(MetadataFields.GLOBAL_DOC_ID).toString());
				indexRequestCTC.source(obj.toString(), XContentType.JSON);
				log.debug("indexRequest CTC ", indexRequestCTC);
				IndexResponse responseCTC = rioAltTwoRestHighLevelClient.index(indexRequestCTC, RequestOptions.DEFAULT);
				log.debug("Response CTC ", responseCTC);
			}

			updateMasterRecon(IConstants.COMPLETED_STATUS);
			log.info(" File Uploaded successfully for global Doc Id :" +  responseELR.getId() );
			return new UploadResponseManager(responseELR.getId(), new Timestamp(this.receivedTime.getTime()),
					HttpStatus.SC_CREATED, new JSONObject());
		} catch (Exception e) {
			log.error("Exception occured in createESentries() method {}",e.getMessage());
			throw new IOException("Exception occured during  createESentries " + e.getMessage());
		}

	}

	private void updateMasterRecon(String status) throws Exception {

					if (!status.equalsIgnoreCase(IConstants.PROCESSING_STATUS))
						completeUploadProcess(status);
					else
						createMasterRecon(status);

	}

	private TUpstreamMaster createTUpstreamMaster(String status) throws Exception {
					return insertTUpstreamMaster(status);
			}
		

			private TUpstreamMaster insertTUpstreamMaster(String status) throws Exception{
				log.debug("Inserting TUpstreamMaster");
				upstreamMaster = new TUpstreamMaster();
				upstreamMaster.setDateReceived(new Timestamp(receivedTime.getTime()));
				upstreamMaster.setDateProcessed(new Timestamp(System.currentTimeMillis()));
				upstreamMaster.setBatchName(contentFile);
				upstreamMaster.setBatchSize(fileSize);
				upstreamMaster.setSuccessfulDocCount(1);
				upstreamMaster.setHostname(httpHeaders.getHost().toString());
				upstreamMaster.setTotalDocCount(1);
				upstreamMaster.setSuccessfulDocCount(1);
				upstreamMaster.setSuccessfulDocCount(1);
				upstreamMaster.setStatus(status);
				log.debug(uploadDbService.insertTUpstreamMaster(upstreamMaster).toString());
				if (status.equalsIgnoreCase(IConstants.FATAL_STATUS)) {
					upstreamException.setTUpstreamMaster(upstreamMaster);
					log.debug(uploadDbService.insertTUpstreamException(upstreamException).toString());
				}				
				return upstreamMaster;
			}


	private void createMasterRecon(String status) throws Exception {
		masterRecon = new MasterRecon();
		masterRecon.setBatchFile(upstreamMaster.getParentId() + "_" + batch_id+"_"+
				StringUtils.abbreviate(contentFile, 17));
		masterRecon.setBatchSource(httpHeaders.getHost().toString());
		masterRecon.setStatus(status);
		masterRecon.setDataGroup(documentClass);
		masterRecon.setDataVolume(this.contentRoot);
		masterRecon.setTotalSize((int) fileSize);
		masterRecon.setTotalContent(1);
		masterRecon.setTotalMetadata(1);
		masterRecon.setReceivedTime(receivedTime);
		MasterRecon insertMasterRecon = this.uploadDbService.insertMasterRecon(masterRecon);
		this.batch_id= insertMasterRecon.getId().toString();
		log.debug("Master recon created {}", insertMasterRecon.toString());
	}

	private void completeUploadProcess(String status) throws Exception {
		masterRecon.setStatus(status);
		masterRecon.setCompletedTime(new Date(System.currentTimeMillis()));
		log.debug("Master recon updated {}", this.uploadDbService.insertMasterRecon(masterRecon).toString());
	}

	private List<String> findGroupsByDocClass(String docClass) {
		List<DocClassACLLookup> reduced = this.uploadDbService.getGlobalGroupDocClass(docClass);
		reduced.sort(Comparator.comparing(DocClassACLLookup::getPriority));
		Collections.reverse(reduced);
		// Walk through each pattern (by priority desc)
		for (DocClassACLLookup item : reduced) {
			// If priority == 0 (default) go with it
			if (item.getPriority() == 0) {
				return Arrays.asList(item.getGroupNames().split(","));
			} else {
				String pattern = item.getDocClassAttributeValueRegex();
                String attributeKey = item.getDocClassAttributeName();
                if (obj.containsKey(attributeKey) && obj.get(attributeKey).toString().matches(pattern)) {
                    return Arrays.asList(item.getGroupNames().split(","));
			}
		}
		}
		return null;
	}
	
	/**
	 * @param epochDate
	 * @return
	 */
	private String getDateFromEpoch(String epochDate) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = new Date(Long.parseLong(epochDate));
			return format.format(date);
		} catch (Exception e) {
			// ignore this error
		}
		return format.format(new Date());
	}

}