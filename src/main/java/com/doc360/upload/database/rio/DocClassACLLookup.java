package com.doc360.upload.database.rio;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "doc_cls_acl_lookup")
@NoArgsConstructor
@Getter
@Setter
public class DocClassACLLookup implements Serializable {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "doc_cls_name")
    private String docClassName;

    @Column(name = "doc_cls_attr_name")
    private String docClassAttributeName;

    @Column(name = "doc_cls_attr_val_regex")
    private String docClassAttributeValueRegex;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "group_names")
    private String groupNames;

}

