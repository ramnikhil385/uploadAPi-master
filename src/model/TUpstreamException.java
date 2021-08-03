package model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the t_upstream_exception database table.
 * 
 */
@Entity
@Table(name="t_upstream_exception")
@NamedQuery(name="TUpstreamException.findAll", query="SELECT t FROM TUpstreamException t")
public class TUpstreamException implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="gbl_doc_id")
	private String gblDocId;

	@Column(name="xcpt_cd")
	private String xcptCd;

	@Column(name="xcpt_datetime")
	private Timestamp xcptDatetime;

	@Column(name="xcpt_id")
	private Integer xcptId;

	@Column(name="xcpt_status")
	private String xcptStatus;

	@Column(name="xcpt_type")
	private String xcptType;

	//bi-directional many-to-one association to TUpstreamMaster
	@ManyToOne
	@JoinColumn(name="parent_id")
	private TUpstreamMaster TUpstreamMaster;

	public TUpstreamException() {
	}

	public String getGblDocId() {
		return this.gblDocId;
	}

	public void setGblDocId(String gblDocId) {
		this.gblDocId = gblDocId;
	}

	public String getXcptCd() {
		return this.xcptCd;
	}

	public void setXcptCd(String xcptCd) {
		this.xcptCd = xcptCd;
	}

	public Timestamp getXcptDatetime() {
		return this.xcptDatetime;
	}

	public void setXcptDatetime(Timestamp xcptDatetime) {
		this.xcptDatetime = xcptDatetime;
	}

	public Integer getXcptId() {
		return this.xcptId;
	}

	public void setXcptId(Integer xcptId) {
		this.xcptId = xcptId;
	}

	public String getXcptStatus() {
		return this.xcptStatus;
	}

	public void setXcptStatus(String xcptStatus) {
		this.xcptStatus = xcptStatus;
	}

	public String getXcptType() {
		return this.xcptType;
	}

	public void setXcptType(String xcptType) {
		this.xcptType = xcptType;
	}

	public TUpstreamMaster getTUpstreamMaster() {
		return this.TUpstreamMaster;
	}

	public void setTUpstreamMaster(TUpstreamMaster TUpstreamMaster) {
		this.TUpstreamMaster = TUpstreamMaster;
	}

}