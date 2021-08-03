package com.doc360.api.exception;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.doc360.elastic.resource.UploadResponseManager;

public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<String> errors = new ArrayList<String>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.add(error.getField() + ": " + error.getDefaultMessage());
		}
		for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
			errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
		}
		Map<String, List<String>> errorMap= new HashMap<String, List<String>>();
		List<String> generalError= new ArrayList<String>();
		generalError.add(ex.getLocalizedMessage());
		errorMap.put("file1", errors);
		errorMap.put("message", generalError);
		UploadResponseManager apiError = new UploadResponseManager(null,new Timestamp(System.currentTimeMillis()),
				HttpStatus.BAD_REQUEST.value(),	new JSONObject(errorMap));
		
		return handleExceptionInternal(ex, apiError, headers, HttpStatus.BAD_REQUEST, request);
	}
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<String> errors = new ArrayList<String>();
		StringBuilder error=new StringBuilder(); 
		if(StringUtils.equalsAnyIgnoreCase( ex.getRequestPartName(),"file1")) {
			error.append("Data File for Ingestion is missing in request");
			errors.add(error.toString());
		}else if(StringUtils.equalsAnyIgnoreCase( ex.getRequestPartName(),"metaData")) {
			error.append("Meta Data Json missing in request");
			errors.add(error.toString());
		}
		Map<String, List<String>> errorMap= new HashMap<String, List<String>>();
		List<String> generalError= new ArrayList<String>();
		generalError.add(ex.getLocalizedMessage());
		errorMap.put("error", errors);
		errorMap.put("message", generalError);
		UploadResponseManager apiError = new UploadResponseManager(null,new Timestamp(System.currentTimeMillis()),
				HttpStatus.BAD_REQUEST.value(),	new JSONObject(errorMap));

		return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler({ MethodArgumentTypeMismatchException.class })
	public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
			WebRequest request) {
		List<String> errors = new ArrayList<String>();
		String error = ex.getName() + " should be of type " + ex.getRequiredType().getName();
		errors.add(error);
		Map<String, List<String>> errorMap = new HashMap<String, List<String>>();
		List<String> generalError = new ArrayList<String>();
		generalError.add(ex.getLocalizedMessage());
		errorMap.put("error", errors);
		errorMap.put("message", generalError);
		UploadResponseManager apiError = new UploadResponseManager(null, new Timestamp(System.currentTimeMillis()),
				HttpStatus.BAD_REQUEST.value(), new JSONObject(errorMap));
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler({ ParseException.class })
	public ResponseEntity<Object> handleMetaDataParseException(ParseException ex, WebRequest request) {
		List<String> errors = new ArrayList<String>();
		String error = "Meta Data json not in correct format";
		errors.add(error);
		Map<String, List<String>> errorMap = new HashMap<String, List<String>>();
		List<String> generalError = new ArrayList<String>();
		generalError.add(ex.getLocalizedMessage());
		errorMap.put("error", errors);
		errorMap.put("message", generalError);
		UploadResponseManager apiError = new UploadResponseManager(null, new Timestamp(System.currentTimeMillis()),
				HttpStatus.BAD_REQUEST.value(), new JSONObject(errorMap));
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ IOException.class })
	public ResponseEntity<Object> handleNotEnoughSpaceException(IOException ex, WebRequest request) {
		List<String> errors = new ArrayList<String>();
		String error = "";
		if (ex.getLocalizedMessage().contains("not enough space")) {
			error = "File can't be uploaded due to storage failure";
		} else {
			error = "Error occured while storing file";
		}
		errors.add(error);
		Map<String, List<String>> errorMap = new HashMap<String, List<String>>();
		List<String> generalError = new ArrayList<String>();
		generalError.add(ex.getLocalizedMessage());
		errorMap.put("error", errors);
		errorMap.put("message", generalError);
		UploadResponseManager apiError = new UploadResponseManager(null, new Timestamp(System.currentTimeMillis()),
				HttpStatus.INTERNAL_SERVER_ERROR.value(), new JSONObject(errorMap));
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	@ExceptionHandler(CreateDocException.class)
	public final ResponseEntity<Object> handleCreateDocExceptions(CreateDocException ex, WebRequest request) {
		List<String> errors = new ArrayList<String>();
		String error = "Error occured during upload";
		int statusCode;
		if(ex.getMessage().contains("File Extension Not Supported")) {
			error = "File Extension Not Supported";
			statusCode=HttpStatus.BAD_REQUEST.value();
			
		}else {
			statusCode=HttpStatus.INTERNAL_SERVER_ERROR.value();
		}
		errors.add(error);
		Map<String, List<String>> errorMap = new HashMap<String, List<String>>();
		List<String> generalError = new ArrayList<String>();
		generalError.add(ex.getLocalizedMessage());
		errorMap.put("error", errors);
		errorMap.put("message", generalError);
		UploadResponseManager apiError = new UploadResponseManager(null, new Timestamp(System.currentTimeMillis()),
				statusCode, new JSONObject(errorMap));
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), statusCode==HttpStatus.INTERNAL_SERVER_ERROR.value()?HttpStatus.INTERNAL_SERVER_ERROR:
			HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
		List<String> errors = new ArrayList<String>();
		String error = "Error occured during upload";
		errors.add(error);
		Map<String, List<String>> errorMap = new HashMap<String, List<String>>();
		List<String> generalError = new ArrayList<String>();
		generalError.add(ex.getLocalizedMessage());
		errorMap.put("error", errors);
		errorMap.put("message", generalError);
		UploadResponseManager apiError = new UploadResponseManager(null, new Timestamp(System.currentTimeMillis()),
				HttpStatus.INTERNAL_SERVER_ERROR.value(), new JSONObject(errorMap));
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}