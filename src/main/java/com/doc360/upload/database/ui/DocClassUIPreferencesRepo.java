package com.doc360.upload.database.ui;

import org.springframework.data.repository.CrudRepository;

public interface DocClassUIPreferencesRepo extends CrudRepository<DocClassUIPreferences, Integer> {
    DocClassUIPreferences findByDocClassName(String docClassName);
}
