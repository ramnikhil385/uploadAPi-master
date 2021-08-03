package com.doc360.apibridge.utility;

public interface IConstants {
	// ELASTIC SEARCH
	String RIO_ALT_HOST_NUMBER_2 = "2";
	String SQL_PLUGIN_RESOURCE = "_sql";
	String SQL_PARAMETER_KEY = "sql";
	String HTTP_GET_METHOD = "GET";
	String HTTP_HEAD_METHOD = "HEAD";
	int HTTP_STATUS_CODE_SUCCESS = 200;
	String SEARCH_RESOURCE = "_search";
	String FORWARD_SLASH = "/";
	String ES_TYPE_DOCUMENT = "Document";
	String ES_QUERY_DELIMETER = "?";
	String ES_GLOBAL_DOC_ID_IDENTIFIER = "u_gbl_doc_id";
	String ES_REPO_NAME_IDENTIFIER = "repository_name";
	String ES_OBJECT_ID_IDENTIFIER = "r_object_id";
	String ES_CONTENT_SIZE_IDENTIFIER = "r_content_size";
	String ES_COMPOUND_DOC = "u_compound_doc";
	String ES_URL_PATH_IDENTIFIER = "urlpath";
	String ES_PAGE_COUNT_IDENTIFIER = "r_page_cnt";
	String ES_CONTENT_TYPE_IDENTIFIER = "a_content_type";
	String ES_ORIG_CREATION_DATE_IDENTIFIER = "u_orig_creation_date";
	String ES_SECURITY_RUNAS_USER_HEADER = "es-security-runas-user";
	String ES_DOC_TYPE_DEFAULT_SUFFIX = "_2";
	String ES_SCROLL_ID = "_scroll_id";
	String ES_MATCH_QUERY_CLAUSE = "matchQuery";
	String ES_PREFERENCE_PRIMARY_FIRST = "_primary_first";
	String ES_DOCVALUES_DEFAULT_FORMAT = "use_field_mapping";
	String ES_METADATA_ATTRIBUTE_PREFIX = "u_";
	String ES_BODY = "body";
	String ES_ATTRIBUTE_TYPE_TEXT = "Text";
	String ES_INDEX_NAME_MARM = "u_arm_rpt";
	String ES_MARM_RUN_DT_NAME = "u_run_dt";
	long ES_SCROLL_TIME_OUT_IN_MINUTES = 15L;
	String HTTPS_PROTOCOL = "https";

	// BEAN ID CONSTANTS
	String BEAN_ID_SSL_CONTEXT = "sslContext";
	String BEAN_ID_SU_MANAGER = "suManager";
	String BEAN_ID_ES_REST_CLIENT = "esRestClient";
	String BEAN_ID_BONEYARD_REST_CLIENT = "boneyardRestClient";
	String BEAN_ID_FARM_REST_CLIENT = "farmRestClient";
	String BEAN_ID_RIO_REST_CLIENT = "rioRestClient";
	String BEAN_ID_RIO_ALT_TWO_REST_CLIENT = "rioAltTwoRestClient";
	String BEAN_ID_BONEYARD_REST_HIGH_LEVEL_CLIENT = "boneyardRestHighLevelClient";
	String BEAN_ID_FARM_REST_HIGH_LEVEL_CLIENT = "farmRestHighLevelClient";
	String BEAN_ID_RIO_REST_HIGH_LEVEL_CLIENT = "rioRestHighLevelClient";
	String BEAN_ID_RIO_ALT_TWO_REST_HIGH_LEVEL_CLIENT = "rioAltTwoRestHighLevelClient";

	String ES_CONTENT_TYPE_PDF = "pdf";

	String COMPLETED_STATUS = "COMPLETE";

	String ERROR_STATUS = "ERROR";
	
	String FATAL_STATUS = "Fatal";
	
	String PROCESSING_STATUS = "PROCESSING";

	String RIO_BATCH_ERROR = "ERROR";
	
	String EXCEPTION_CODE_VALIDATION= "Vaidation Failed";

	String JSON_EXTENSION_CONSTANT = ".json";
	// security

	// spring security roles
	public static final String ROLE_USER = "ROLE_USER";
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String BOUNCY_CASTLE_PROVIDER_NAME = "BC";

	// Request type
	public static final String REQUEST_TYPE_HEAD = "HEAD";
	public static final String REQUEST_TYPE_GET = "GET";

	// logging constants
	public static final String MDC_USER_ID = "userID";
	public static final String MDC_USER_IP_ADDRESS = "userIPAddress";
	public static final String MDC_APPLICATION_IDENTIFIER = "applicationIdentifier";

	// Security constants
	public static final String CHARSET_UTF_8 = "UTF-8";
	public static final String ALGORITHM_SHA_256 = "SHA-256";

	public static final String JWT_TOKEN_ISSUER = "DOC360_API_CLIENT";
	public static final String JWT_CLAIM_APPID = "appId";
	public static final String JWT_CLAIM_USERNAME = "username";
	public static final String JWT_CLAIM_CRED_DIGEST = "passworddigest";
	public static final String JWT_CLAIM_NONCE = "nonce";
	public static final String JWT_CLAIM_CREATED = "created";
	public static final String JWT_CLAIM_ACTOR = "actor";
	public static final String JWT_CLAIM_DOMAIN = "domain";
	public static final String JWT_CLAIM_EDSS_USERNAME = "edssUserName";
	public static final String JWT_CLAIM_EDSS_USERCRED = "edssUserCred";
	public static final String REQUEST_ATTRIBUTE_DOC360_JWT_APPLICATION_CLAIMS = "DOC360_JWT_APPLICATION_CLAIMS";

	public static final String BEAN_ID_APPLICATION_SECURITY_CONFIG = "applicationSecurityConfig";
	public static final String BEAN_ID_JWT_VALIDATOR = "jwtValidator";
	public static final String EMPTY_STRING = "";
	public static final String HEADER_APPLICATION_IDENTIFIER = "ApplicationEntity-Identifier";
	public static final String CREATION_DATE_KEY = "u_orig_creation_date";
	public static final String PAGE_COUNT_KEY = "r_page_cnt";
	
	public static final String METADATA_FIELD = "Mandatory field ";
	
	public static final String DATEFORMAT = "yyyy-MM-dd";
	public static final String DF_MONTHH_DATE_YR = "MM/dd/yyyy";
	public static final String DF_MONTH_DATE_YR = "MMMM dd, yyyy";
	//F299998-US1796073
	public static final String DF_HYPHENATED_MONTH_DAY_YR = "MM-dd-yyyy";
	public static final String DF_HYPHENATED_YR_MONTH_DAY = "yyyy-MM-dd";

}
