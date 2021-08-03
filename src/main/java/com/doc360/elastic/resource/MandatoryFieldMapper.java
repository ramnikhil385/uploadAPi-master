package com.doc360.elastic.resource;

import java.util.ArrayList;
import java.util.List;


public class MandatoryFieldMapper {
	private boolean required;
    private int size;
    private String type="";
	private List<String> mandatoryFields = new ArrayList<>();
	private static boolean validation= true;
	private String label;
    
	
	public List<String> getMandatoryFields() {
		return mandatoryFields;
	}
	public void setMandatoryFields(List<String> mandatoryFields) {
		this.mandatoryFields = mandatoryFields;
	}
	
	public MandatoryFieldMapper(String type, String required, String size) {
		this.type=type;
		this.required= Boolean.valueOf(required);
		this.size= Integer.valueOf(size);
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isValidation() {
		return validation;
	}
	public void setValidation(boolean validation) {
		this.validation = validation;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
}
