package com.doc360.upload.database.rio;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.net.URI;
import java.util.Date;
import java.util.Map;

@Slf4j
@Entity
@Table(name = "master_recon")
@Data
@ToString
public class MasterRecon {
    @Transient
    private URI batchSourceUri;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "batch_file")
    private String batchFile;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "received_time")
    private Date receivedTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "completed_time")
    private Date completedTime;

    @Column(name = "total_size")
    private Integer totalSize = 0;

    @Column(name = "total_content")
    private Integer totalContent = 0;

    @Column(name = "total_metadata")
    private Integer totalMetadata = 0;

    @Column(name = "file_server")
    private String fileServer = System.getenv("PARENT_HOSTNAME");

    @Column(name = "data_volume")
    private String dataVolume;

    @Column(name = "status")
    private String status;

    @Column(name = "data_group")
    private String dataGroup;

    @Column(name = "batch_source")
    private String batchSource;

    @Column(name = "exception_message")
    private String exceptionMessage;

    @Column(name = "exception_source")
    private String exceptionSource;

    @Column(name = "exception_file")
    private String exceptionFile;

    @Column(name = "error_log_id")
    private Integer errorLogId;

 

    
    public void setBatchSource(String batchSource) {
        this.batchSource = batchSource;
        try {
            this.batchSourceUri = URI.create(batchSource);
        } catch (Exception ignored) {
            log.warn("Invalid URI format for batchSource: {}\nCertain functionality will be unavailable.", batchSource);
        }
    }

    public URI getBatchSourceUri() {
        return this.batchSourceUri;
    }

}
