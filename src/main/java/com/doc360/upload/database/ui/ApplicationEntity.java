/**
 * 
 */
package com.doc360.upload.database.ui;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * Entity object for ApplicationEntity configuration.
 * 
 * @author Tarun Verma
 *
 */
@Entity
@Table(name = "r_apps")
@Data
public class ApplicationEntity {
	@Id
	@Column(name = "id")
	Integer id;

	@Column(name = "app_id")
	String appId;

	@Column(name = "app_desc")
	String applicationDescription;

	@Column(name = "app_pwd")
	String applicationPassword;

	@Column(name = "app_username")
	String applicationUserId;

	@Column(name = "preferences")
	String preferences;
}
