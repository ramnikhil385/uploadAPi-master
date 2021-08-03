package com.doc360.upload.database.ui;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "r_doc_cls")
@Data
public class DocClassUIPreferences {
    @Id
    @Column(name = "r_doc_cls_id")
    Integer docClassId;
    @Column(name = "doc_cls_name")
    String docClassName;
    @Column(name = "preferences")
    String preferences;
}
