package com.doc360.upload.database.rio;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doc360.apibridge.utility.ApplicationMapper;
import com.doc360.upload.database.pre_processor.RBatchtypeDetail;
import com.doc360.upload.database.pre_processor.RbatchDetailRepo;
import com.doc360.upload.database.pre_processor.TUpstreamException;
import com.doc360.upload.database.pre_processor.TUpstreamExceptionRepo;
import com.doc360.upload.database.pre_processor.TUpstreamMaster;
import com.doc360.upload.database.pre_processor.TUpstreamMasterRepo;
import com.doc360.upload.database.ui.ApplicationRepo;
import com.doc360.upload.database.ui.DocClassUIPreferences;
import com.doc360.upload.database.ui.DocClassUIPreferencesRepo;
import com.doc360.uploadApi.security.Application;
import com.google.gson.Gson;

@Service
public class UploadDBService {
@Autowired	
private DocClassACLLookupRepo docClassLckup;
@Autowired
private MasterReconRepo masterRecRepo;
@Autowired
private ApplicationRepo applicationRepo;
@Autowired
private RbatchDetailRepo rBatchRepo;
@Autowired
private ApplicationMapper applicationMapper;
@Autowired
private TUpstreamMasterRepo tUpstreamMasterRepo;
@Autowired
private TUpstreamExceptionRepo tUpstreamExceptionRepo;
@Autowired
private DocClassUIPreferencesRepo docClassUIPreferencesRepo;

public List<DocClassACLLookup> getGlobalGroupDocClass(String docClass) {
	return docClassLckup.findByDocClassName(docClass);
}

public MasterRecon insertMasterRecon(MasterRecon masterRecon) {
	return masterRecRepo.save(masterRecon);
}

public Application findByApplicationId(String appId) throws IOException {
	return applicationMapper.map(applicationRepo.findByAppId(appId));

}

public RBatchtypeDetail findRbatchTypeForDocClass(String docClassName) {
	return rBatchRepo.findBydocClsNm(docClassName);
}

public TUpstreamMaster insertTUpstreamMaster(TUpstreamMaster upstreamMaster) {
	return tUpstreamMasterRepo.save(upstreamMaster);
}

public TUpstreamException insertTUpstreamException(TUpstreamException upstreamException) {
	return tUpstreamExceptionRepo.save(upstreamException);
}

public int getTUpstreamPrimaryKey() {
	return tUpstreamMasterRepo.findMaxParentId()+1;
}

public Map<String, String> getDocClassUIPreferences(String docClass) {
    try {
        DocClassUIPreferences docClassUIPreferences = this.docClassUIPreferencesRepo.findByDocClassName(docClass);
        return new Gson().fromJson(docClassUIPreferences.getPreferences(), HashMap.class);
    } catch (NullPointerException e) {
        return null;
    }
}
}
