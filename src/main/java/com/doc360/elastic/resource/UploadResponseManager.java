package com.doc360.elastic.resource;

import java.sql.Timestamp;

import org.json.simple.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class UploadResponseManager {
private String global_doc_id;
//private String compound_doc_id;
private Timestamp receivedDate;
private int status;	
private JSONObject errorMap ;
}
