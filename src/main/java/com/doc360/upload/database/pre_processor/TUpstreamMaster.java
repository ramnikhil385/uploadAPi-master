package com.doc360.upload.database.pre_processor;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the t_upstream_master database table.
 * 
 */
@ToString
@Data
@Entity
@Table(name="t_upstream_master")
@NamedQuery(name="TUpstreamMaster.findAll", query="SELECT t FROM TUpstreamMaster t")
@NamedQuery(name="TUpstreamMaster.findMaxParentId", query="SELECT max(t.parentId) FROM TUpstreamMaster t")

public class TUpstreamMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="parent_id")
	private Integer parentId;

	@Column(name="batch_name")
	private String batchName;

	@Column(name="batch_size")
	private Long batchSize;

	@Column(name="batch_split_count")
	private Integer batchSplitCount;

	@Column(name="batch_type_id")
	private Integer batchTypeId;

	@Column(name="date_processed")
	private Timestamp dateProcessed;

	@Column(name="date_received")
	private Timestamp dateReceived;

	@Column(name="exception_code")
	private String exceptionCode;

	@Column(name="exception_count")
	private Integer exceptionCount;

	private String hostname;

	private String status;

	@Column(name="successful_doc_count")
	private Integer successfulDocCount;

	@Column(name="summary_sent")
	private Boolean summarySent;

	@Column(name="total_doc_count")
	private Integer totalDocCount;

	@Column(name="total_page_count")
	private Integer totalPageCount;

	public TUpstreamMaster() {
	}

	public Integer getParentId() {
		return this.parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getBatchName() {
		return this.batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public Long getBatchSize() {
		return this.batchSize;
	}

	public void setBatchSize(Long batchSize) {
		this.batchSize = batchSize;
	}

	public Integer getBatchSplitCount() {
		return this.batchSplitCount;
	}

	public void setBatchSplitCount(Integer batchSplitCount) {
		this.batchSplitCount = batchSplitCount;
	}

	public Integer getBatchTypeId() {
		return this.batchTypeId;
	}

	public void setBatchTypeId(Integer batchTypeId) {
		this.batchTypeId = batchTypeId;
	}

	public Timestamp getDateProcessed() {
		return this.dateProcessed;
	}

	public void setDateProcessed(Timestamp dateProcessed) {
		this.dateProcessed = dateProcessed;
	}

	public Timestamp getDateReceived() {
		return this.dateReceived;
	}

	public void setDateReceived(Timestamp dateReceived) {
		this.dateReceived = dateReceived;
	}

	public String getExceptionCode() {
		return this.exceptionCode;
	}

	public void setExceptionCode(String exceptionCode) {
		this.exceptionCode = exceptionCode;
	}

	public Integer getExceptionCount() {
		return this.exceptionCount;
	}

	public void setExceptionCount(Integer exceptionCount) {
		this.exceptionCount = exceptionCount;
	}

	public String getHostname() {
		return this.hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getSuccessfulDocCount() {
		return this.successfulDocCount;
	}

	public void setSuccessfulDocCount(Integer successfulDocCount) {
		this.successfulDocCount = successfulDocCount;
	}

	public Boolean getSummarySent() {
		return this.summarySent;
	}

	public void setSummarySent(Boolean summarySent) {
		this.summarySent = summarySent;
	}

	public Integer getTotalDocCount() {
		return this.totalDocCount;
	}

	public void setTotalDocCount(Integer totalDocCount) {
		this.totalDocCount = totalDocCount;
	}

	public Integer getTotalPageCount() {
		return this.totalPageCount;
	}

	public void setTotalPageCount(Integer totalPageCount) {
		this.totalPageCount = totalPageCount;
	}

}