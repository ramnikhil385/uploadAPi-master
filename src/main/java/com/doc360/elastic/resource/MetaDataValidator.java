package com.doc360.elastic.resource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.doc360.apibridge.utility.IConstants;
import com.doc360.apibridge.utility.MetaDataDateValidator;
import com.doc360.upload.database.pre_processor.RBatchtypeDetail;
import com.doc360.upload.database.rio.UploadDBService;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MetaDataValidator {
	
	@Value("${required.json-format-PIQ}")
	private String fieldsString;
	@Autowired
	private UploadDBService uploadDBServie;

	public Map<String, List<String>> validate(String groupName, JSONObject metadataDocument,
			String fileName) {
		fileName="error";
		Map<String, MandatoryFieldMapper> mandatoryFieldObjectMAP = getRequiredFieldsByDataGroup(groupName);
		Map<String, List<String>> errorMap = new HashMap<String, List<String>>();
		if (mandatoryFieldObjectMAP.size() == 0) {
			log.warn("Mandatory fields are not present in database for the group : {}", groupName);
			return errorMap;
		}

		log.info("Validating metadata ");
		log.info(metadataDocument.toJSONString());
		
		List<String> docMissingFields = new ArrayList<>();
		for (Map.Entry<String, MandatoryFieldMapper> map : mandatoryFieldObjectMAP.entrySet()) {
			String mandatoryField = map.getKey();
			StringBuilder errorMessage = new StringBuilder();
			List<String>errorMessg= new ArrayList<String>();
			String value = (String) metadataDocument.get(mandatoryField);
			MandatoryFieldMapper mandatoryFieldMapper = null;
			mandatoryFieldMapper = map.getValue();
			if (mandatoryFieldMapper != null && mandatoryFieldMapper.isRequired() && StringUtils.isEmpty(value)) {
				docMissingFields.add(mandatoryField);
				errorMessage = errorMessage.append(IConstants.METADATA_FIELD + mandatoryFieldMapper.getLabel() + " does not have any value");
				errorMessg.add(errorMessage.toString());
				errorMap.put(fileName, errorMessg);
			}
			if(value == null || (value.equalsIgnoreCase(" ") && mandatoryFieldMapper.getType().equalsIgnoreCase("Date"))) {
				value = "";
			}
			validateMetadata(errorMap, fileName, docMissingFields, mandatoryField, errorMessage, value,
					mandatoryFieldMapper, metadataDocument);
		}

		return errorMap;
	}
	
	private String formatDate(MandatoryFieldMapper mandatoryFieldMapper, String value, String mandatoryField, JSONObject metadataDocument) {
		
			if((StringUtils.containsAny(mandatoryFieldMapper.getType(), "Date", "DateTime") && !StringUtils.isEmpty(value))||((mandatoryField.endsWith("_dt") ||mandatoryField.endsWith("_date")) && !!StringUtils.isEmpty(value))) {
				if(value.contains("-") || value.contains("/")) {
					log.info("{} {}","value of date", value);
					MetaDataDateValidator dateValidator= new MetaDataDateValidator();
					SimpleDateFormat df= null;
					if(value.contains(":")) {
						 df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					}
					else {
						df = new SimpleDateFormat("yyyy-MM-dd");
					}
					df.setTimeZone(TimeZone.getTimeZone("GMT"));
					try {
						Date date = df.parse(dateValidator.convertDateFromString(value));
						log.info("{} {}","value of date at 86", date);
						long epoch = date.getTime();
						log.info("{} {}","value of epoch date at 88", String.valueOf(epoch));
						value = String.valueOf(epoch);
						metadataDocument.put(mandatoryField, value);
					} catch (java.text.ParseException e) {
						log.error(e.getMessage());
					}
					}else {
					return value;
						
				}
				
			}
		return value;
	}

	private Map<String, MandatoryFieldMapper> getRequiredFieldsByDataGroup(String groupName) {
		JSONObject fieldsJson = null;
		JSONObject metadataJson = null;
		JSONParser parser = new JSONParser();
		Map<String, MandatoryFieldMapper> mandatoryFields = new HashMap<String, MandatoryFieldMapper>();
		RBatchtypeDetail rbatchTypeForDocClass = new RBatchtypeDetail() ;
		try {
			rbatchTypeForDocClass = uploadDBServie.findRbatchTypeForDocClass(groupName);
			if (rbatchTypeForDocClass != null && rbatchTypeForDocClass.getRequiredColumns() != null) {
				fieldsJson = (JSONObject) parser.parse(rbatchTypeForDocClass.getRequiredColumns());
				metadataJson = getMetadataJSON((JSONObject) parser.parse(rbatchTypeForDocClass.getMetadataJson()));
			} else {
				log.info("No Result Found in  RBatchtypeDetail for doc class: " + groupName);
				return mandatoryFields;
			}

		} catch (NoResultException ex) {
			log.error("{} {} {}", "No Result Found in  RBatchtypeDetail for doc class { }", groupName, ex.getMessage());
			return mandatoryFields;
		} catch (ParseException e) {
			log.error("{} {} {}", "Error occured parsing Required Json for Validation", rbatchTypeForDocClass,e.getMessage());
			return mandatoryFields;
		} catch (Exception exc) {
			log.error("{} {} {}", "Error occured during findRbatchTypeForDocClass", exc.getMessage());
			return mandatoryFields;
		}
		
		Iterator<String> keys = fieldsJson.keySet().iterator();
		while(keys.hasNext()) {
		    String key = keys.next();
		    if (fieldsJson.get(key) instanceof JSONObject) {
		    	MandatoryFieldMapper metaData= new Gson().fromJson(fieldsJson.get(key).toString(), MandatoryFieldMapper.class);
		    	metaData.setLabel(null != metadataJson.get(key) ? metadataJson.get(key).toString() : key );
		    	mandatoryFields.put(key, metaData);
		    }
		}
		return mandatoryFields;
	}
	private String validateMetadata(Map<String, List<String>> errorMap, String fileName, 
			List<String> docMissingFields, String mandatoryField, StringBuilder errorMessage, String value,
			MandatoryFieldMapper mandatoryFieldMapper, JSONObject metadataDocument) {
		List<String>errorMessg= new ArrayList<String>();
		
		
		
		String dateChange = "";
		String time = "";
		/* Adding changes for HH mm ss START */
		boolean validTime = false;
		if(mandatoryFieldMapper.getType().equalsIgnoreCase("DateTime")) {
		
			if (value.contains(":")) {
				int index = value.indexOf(":")-3;
				dateChange = value.substring(0, index).trim();
				time = value.substring(index).trim();
				validTime = time.matches("([0-1]?\\d|2[0-3]):([0-5]?\\d):([0-5]?\\d)");
			}else {
				dateChange = value;
			}
		}else {
			dateChange = value;
		}
		
		
		
		if(!StringUtils.isEmpty(value)) {
			MetaDataDateValidator dateValidator= new MetaDataDateValidator();
			
			if (mandatoryFieldMapper != null && mandatoryFieldMapper.getType().equalsIgnoreCase("Numeric")
					&& !StringUtils.isNumeric(value)) {
				if(!mandatoryFieldMapper.isValidation()) {
					return value="";
				}
				docMissingFields.add(mandatoryField);
				errorMessage = errorMessage.append((mandatoryFieldMapper.isRequired() ? IConstants.METADATA_FIELD : "Field ") + mandatoryFieldMapper.getLabel() + " of type :"
						+ mandatoryFieldMapper.getType() + " has invalid value");
				errorMessg.add(errorMessage.toString());
				errorMap.put(fileName, errorMessg);
			}
			if (mandatoryFieldMapper != null && mandatoryFieldMapper.getSize()>0 && mandatoryFieldMapper.getSize() < value.length()) {
				docMissingFields.add(mandatoryField);
				errorMessage = errorMessage.append((mandatoryFieldMapper.isRequired() ? IConstants.METADATA_FIELD : "Field ") + mandatoryFieldMapper.getLabel() + " has invalid length");
				errorMessg.add(errorMessage.toString());
				errorMap.put(fileName, errorMessg);
			}
			
			if (mandatoryFieldMapper != null && mandatoryFieldMapper.getType().equalsIgnoreCase("Date")
					&& !dateValidator.validateDate(dateChange, mandatoryFieldMapper.isRequired(), mandatoryFieldMapper.isValidation()) && 
					!(StringUtils.equalsIgnoreCase(mandatoryField, IConstants.CREATION_DATE_KEY) ) ) {
				if(!mandatoryFieldMapper.isValidation()) {
					return value="";
				}
				docMissingFields.add(mandatoryField);
				errorMessage = errorMessage.append((mandatoryFieldMapper.isRequired() ? IConstants.METADATA_FIELD : "Field ") + mandatoryFieldMapper.getLabel() + " has invalid Date Format");
				errorMessg.add(errorMessage.toString());
				errorMap.put(fileName, errorMessg);
			}else if(mandatoryFieldMapper != null && mandatoryFieldMapper.getType().equalsIgnoreCase("DateTime")
					&& !dateValidator.validateDate(dateChange, mandatoryFieldMapper.isRequired(), mandatoryFieldMapper.isValidation()) && 
					!(StringUtils.equalsIgnoreCase(mandatoryField, IConstants.CREATION_DATE_KEY) && (!time.equalsIgnoreCase("") && !validTime))) {
				if(!mandatoryFieldMapper.isValidation()) {
					return value="";
				}
				docMissingFields.add(mandatoryField);
				errorMessage = errorMessage.append((mandatoryFieldMapper.isRequired() ? IConstants.METADATA_FIELD : "Field ") + mandatoryFieldMapper.getLabel() + " has invalid Date Time Format");
				errorMessg.add(errorMessage.toString());
				errorMap.put(fileName,errorMessg);
			}
			
			formatDate(mandatoryFieldMapper, value, mandatoryField, metadataDocument);
		}
		return value;
	}
	
	/**
	 * Reverse the JSONObject and send new one
	 * 
	 * @param jsonObject
	 * @return
	 */
	private JSONObject getMetadataJSON(JSONObject jsonObject) {
		Iterator<String> keys = jsonObject.keySet().iterator();
		JSONObject metadataJson = new JSONObject();
		try {
			while(keys.hasNext()) {
			    String key = keys.next();
			    if (jsonObject.get(key) != null) {
			    	metadataJson.put(jsonObject.get(key), key);      
			    }
			}
		} catch (Exception e) {
			log.error("Error in getMetadataJSON()", e);
		}
		return metadataJson;
	}
}
