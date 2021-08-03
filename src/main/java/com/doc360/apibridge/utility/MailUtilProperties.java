package com.doc360.apibridge.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class MailUtilProperties {

	@Value("${mail.user}")
	private String user;
	
	@Value("${mail.pwd}")
	private String pwd;

	@Value("${mail.from}")
	private String from;

	@Value("${mail.subject}")
	private String subject;

	@Value("${mail.to}")
	private String to;

	@Value("${mail.cc}")
	private String cc;

	@Value("${mail.host}")
	private String host;

	
	public String getUser() {
		return user;
	}
	
	public String getPwd() {
		return pwd;
	}
	
	public String getFrom() {
		return from;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public String getTo() {
		return to;
	}
	
	public String getCc() {
		return cc;
	}

	public String getHost() {
		return host;
	}
	

	
}
