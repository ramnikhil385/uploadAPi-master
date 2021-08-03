package com.doc360.upload.database.pre_processor;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


/**
 * The persistent class for the r_batchtype_detail database table.
 * 
 */
@Slf4j
@Data
@ToString
@Entity
@Table(name="r_batchtype_detail")
@NamedQuery(name="RBatchtypeDetail.findAll", query="SELECT r FROM RBatchtypeDetail r")
public class RBatchtypeDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="btch_tp_id")
	private Integer btchTpId;

	@Column(name="batch_job_type")
	private String batchJobType;

	@Column(name="btch_pth")
	private String btchPth;

	@Column(name="data_tpe_id")
	private Integer dataTpeId;

	@Column(name="deliv_id")
	private Integer delivId;

	@Column(name="doc_cls_nm")
	private String docClsNm;

	@Column(name="is_json")
	private Boolean isJson;

	@Column(name="metadata_json")
	private String metadataJson;

	private Boolean multidirswitch;

	private String multikey;

	@Column(name="pst_cnvsn_cd")
	private String pstCnvsnCd;

	@Column(name="recon_lvl")
	private Integer reconLvl;

	@Column(name="required_columns")
	private String requiredColumns;

	@Column(name="src_id")
	private Integer srcId;

	private Boolean tobatch;

	private Boolean tooos;

	private Boolean torio;

	private String upstream;

	public RBatchtypeDetail() {
	}

	public Integer getBtchTpId() {
		return this.btchTpId;
	}

	public void setBtchTpId(Integer btchTpId) {
		this.btchTpId = btchTpId;
	}

	public String getBatchJobType() {
		return this.batchJobType;
	}

	public void setBatchJobType(String batchJobType) {
		this.batchJobType = batchJobType;
	}

	public String getBtchPth() {
		return this.btchPth;
	}

	public void setBtchPth(String btchPth) {
		this.btchPth = btchPth;
	}

	public Integer getDataTpeId() {
		return this.dataTpeId;
	}

	public void setDataTpeId(Integer dataTpeId) {
		this.dataTpeId = dataTpeId;
	}

	public Integer getDelivId() {
		return this.delivId;
	}

	public void setDelivId(Integer delivId) {
		this.delivId = delivId;
	}

	public String getDocClsNm() {
		return this.docClsNm;
	}

	public void setDocClsNm(String docClsNm) {
		this.docClsNm = docClsNm;
	}

	public Boolean getIsJson() {
		return this.isJson;
	}

	public void setIsJson(Boolean isJson) {
		this.isJson = isJson;
	}

	public String getMetadataJson() {
		return this.metadataJson;
	}

	public void setMetadataJson(String metadataJson) {
		this.metadataJson = metadataJson;
	}

	public Boolean getMultidirswitch() {
		return this.multidirswitch;
	}

	public void setMultidirswitch(Boolean multidirswitch) {
		this.multidirswitch = multidirswitch;
	}

	public String getMultikey() {
		return this.multikey;
	}

	public void setMultikey(String multikey) {
		this.multikey = multikey;
	}

	public String getPstCnvsnCd() {
		return this.pstCnvsnCd;
	}

	public void setPstCnvsnCd(String pstCnvsnCd) {
		this.pstCnvsnCd = pstCnvsnCd;
	}

	public Integer getReconLvl() {
		return this.reconLvl;
	}

	public void setReconLvl(Integer reconLvl) {
		this.reconLvl = reconLvl;
	}

	public String getRequiredColumns() {
		return this.requiredColumns;
	}

	public void setRequiredColumns(String requiredColumns) {
		this.requiredColumns = requiredColumns;
	}

	public Integer getSrcId() {
		return this.srcId;
	}

	public void setSrcId(Integer srcId) {
		this.srcId = srcId;
	}

	public Boolean getTobatch() {
		return this.tobatch;
	}

	public void setTobatch(Boolean tobatch) {
		this.tobatch = tobatch;
	}

	public Boolean getTooos() {
		return this.tooos;
	}

	public void setTooos(Boolean tooos) {
		this.tooos = tooos;
	}

	public Boolean getTorio() {
		return this.torio;
	}

	public void setTorio(Boolean torio) {
		this.torio = torio;
	}

	public String getUpstream() {
		return this.upstream;
	}

	public void setUpstream(String upstream) {
		this.upstream = upstream;
	}

}