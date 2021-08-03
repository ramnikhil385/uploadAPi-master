package com.doc360.apibridge.utility;
//package com.optum.doc360.apibridge.utility;
//
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Base64;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.apache.commons.dbcp2.BasicDataSource;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//public class GetQueries {
//
//	private JdbcTemplate jdbcTemplate;
//	private JdbcTemplate batchJdbcTemplate;
//	private JdbcTemplate rioJdbcTemplate;
//
//	private Map<String, Map<String, String>> columnsMapping = new HashMap<>();
//	
//	String userName = "d3ui02_app";
//	String password = "HPBbbvpg";
//
//	private int docClsId;
//
//	private String docClsName;
//
//	private Set<String> setOfIds;
//
//	private BufferedWriter writer;
//
//	private String uiTables = "('r_doc_cls', 'r_doc_cls_attr', 'r_doc_cls_msg', 'r_srch_view_ui_attr', 'r_rslt_view_ui_attr', 'r_gbl_grps_doc_cls', 'r_gbl_grps', 'r_upload_attr')";
//	private String batchTables = "('r_batchtype_detail')";
//	private String rioTables = "('doc_cls_acl_lookup', 'doc_cls_metadata_lookup_v2')";
//
//	List<String> types = Arrays.asList("int", "smallint", "boolean");
//
//	public GetQueries(int id) throws IOException {
//		this.docClsId = id;
//		File file = new File("C:\\app_2\\scripts\\" + id + ".sql");
//		writer = new BufferedWriter(new FileWriter(file));
//	}
//
//	public static void main(String[] args) throws Exception {
//		// Send r_doc_cls_id from r_doc_cls table as parameter
//		GetQueries obj = new GetQueries(182);
//		obj.docClsName = "u_bccm_medica_led";
//		// obj.loadUiTables();
//		// obj.processUiTables();
//		// obj.loadBatchTables();
//		// obj.processBatchTables();
//		obj.process(obj.docClsName, obj.docClsId);
//		obj.writer.close();
//	}
//
//	private void process(String docClsName, int docClsId) throws Exception {
//		try {
//			// Ui tables
//			loadUiTemplate();
//			loadTables(jdbcTemplate, uiTables);
//			processUiTables();
//			// Batch processor tables
//			loadBatchDbTemplate();
//			loadTables(batchJdbcTemplate, batchTables);
//			processBatchTables();
//			// RIO tables
//			loadRioDbTemplate();
//			loadTables(rioJdbcTemplate, rioTables);
//			processRioTables();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void processRioTables() throws Exception {
//		List<Map<String, Object>> list = getBatchandRioTablesData(rioJdbcTemplate, "doc_cls_acl_lookup", "doc_cls_name",
//				"'" + docClsName + "'");
//		// Print r_doc_cls table
//		processSqlData(list, "doc_cls_acl_lookup");
//		list = getBatchandRioTablesData(rioJdbcTemplate, "doc_cls_metadata_lookup_v2", "doc_cls_name",
//				"'" + docClsName + "'");
//		// Print r_doc_cls table
//		processSqlData(list, "doc_cls_metadata_lookup_v2");
//	}
//
//	private void processBatchTables() throws Exception {
//		JdbcTemplate template = loadBatchDbTemplate();
//		List<Map<String, Object>> data = getBatchandRioTablesData(template, "r_batchtype_detail", "doc_cls_nm",
//				"'" + docClsName + "'");
//		// Print r_doc_cls table
//		processSqlData(data, "r_batchtype_detail");
//	}
//
//	private List<Map<String, Object>> getBatchandRioTablesData(JdbcTemplate template, String tableName, String column,
//			String val) {
//		String sql = "select * from " + tableName + " where " + column + " = " + val;
//		List<Map<String, Object>> list = template.queryForList(sql);
//		return list;
//	}
//
//	public void processUiTables() throws Exception {
//		setOfIds = new HashSet<>();
//		List<Map<String, Object>> data = getCommonTablesData("r_doc_cls", "r_doc_cls_id", docClsId);
//		// Print r_doc_cls table
//		processSqlData(data, "r_doc_cls");
//
//		List<Map<String, Object>> list = getCommonTablesData("r_doc_cls_attr", "r_doc_cls_id", docClsId);
//		// Print r_doc_cls_attr table
//		processSqlData(list, "r_doc_cls_attr");
//		list = getCommonTablesData("r_doc_cls_msg", "r_doc_cls_id", docClsId);
//		// Print r_doc_cls_msg table
//		processSqlData(list, "r_doc_cls_msg");
//
//		list = getCommonTablesData("r_gbl_grps_doc_cls", "r_doc_cls_id", docClsId);
//		// Get r_gbl_grp_id
//		Set<String> rGblGrpIds = getRGblGrpIds(list);
//		// Get data from table r_gbl_grps
//		List<Map<String, Object>> tempList = getSearchAndResultData("r_gbl_grps", "r_gbl_grp_id",
//				String.join(",", rGblGrpIds));
//		// Do not add any code in between this..!
//		processSqlData(tempList, "r_gbl_grps");
//		// Print r_doc_cls_msg table
//		processSqlData(list, "r_gbl_grps_doc_cls");
//
//		list = getSearchAndResultData("r_srch_view_ui_attr", "r_doc_cls_attr_id", String.join(",", setOfIds));
//		// Print r_doc_cls_msg table
//		processSqlData(list, "r_srch_view_ui_attr");
//		list = getSearchAndResultData("r_rslt_view_ui_attr", "r_doc_cls_attr_id", String.join(",", setOfIds));
//		// Print r_doc_cls_msg table
//		processSqlData(list, "r_rslt_view_ui_attr");
//
//		list = getSearchAndResultData("r_upload_attr", "r_doc_cls_attr_id", String.join(",", setOfIds));
//		// Print r_doc_cls_msg table
//		processSqlData(list, "r_upload_attr");
//	}
//
//	/**
//	 * Get tables columns and types
//	 */
//	/*
//	 * private void loadUiTables() { JdbcTemplate template = getUiTemplate();
//	 * List<Map<String, Object>> list = template.queryForList(
//	 * "select table_name, column_name, data_type from information_schema.columns where table_name in "
//	 * + uiTables); Map<String, String> newMap = new HashMap<>();
//	 * for(Map<String, Object> map : list) { String tableName =
//	 * String.valueOf(map.get("table_name"));
//	 * if(!columnsMapping.containsKey(tableName)) { newMap = new HashMap<>();
//	 * newMap.put(String.valueOf(map.get("column_name")),
//	 * String.valueOf(map.get("data_type"))); columnsMapping.put(tableName,
//	 * newMap); } else {
//	 * columnsMapping.get(tableName).put(String.valueOf(map.get("column_name")),
//	 * String.valueOf(map.get("data_type"))); } } }
//	 */
//
//	/**
//	 * @return
//	 */
//	private List<Map<String, Object>> getSearchAndResultData(String tableName, String colName, String listOfIds) {
//		String sql = "select * from " + tableName + " where  " + colName + " in (" + listOfIds + ")";
//		JdbcTemplate template = loadUiTemplate();
//		List<Map<String, Object>> list = template.queryForList(sql);
//		return list;
//	}
//
//	/**
//	 * Get data from tables r_doc_cls, r_doc_cls_attr, r_doc_cls_msg
//	 * 
//	 * @param tableName
//	 * @param column
//	 * @param val
//	 * @return
//	 */
//	public List<Map<String, Object>> getCommonTablesData(String tableName, String column, Object val) {
//		String sql = "select * from " + tableName + " where " + column + " = " + val;
//		JdbcTemplate template = loadUiTemplate();
//		List<Map<String, Object>> list = template.queryForList(sql);
//		return list;
//	}
//
//	/**
//	 * @param input
//	 * @param tableName
//	 * @throws IOException
//	 */
//	private void processSqlData(final List<Map<String, Object>> input, String tableName) throws IOException {
//		if (null != input && !input.isEmpty()) {
//
//		}
//		writer.write("-----TABLE - " + tableName.toUpperCase() + " ------\n");
//		for (Map<String, Object> map : input) {
//			StringBuilder builder = new StringBuilder();
//			Set<String> keys = map.keySet();
//			String columns = String.join(", ", keys);
//			builder.append("INSERT INTO " + tableName + " (" + columns + ") " + "VALUES " + "(");
//			StringBuilder temp = new StringBuilder();
//			for (String key : keys) {
//				if (map.get(key) != null) {
//					String colType = getTypeOfColumn(tableName, key);
//					if ("timestamp".equalsIgnoreCase(colType) || "Date".equalsIgnoreCase(colType)) {
//						temp.append("NOW()" + ", ");
//					} else if (types.contains(colType.toLowerCase())) {
//						if ("r_doc_cls_attr".equalsIgnoreCase(tableName) && "r_doc_cls_attr_id".equalsIgnoreCase(key)) {
//							setOfIds.add(String.valueOf(map.get(key)));
//						}
//						temp.append(map.get(key) + ", ");
//					} else {
//						temp.append("'" + map.get(key) + "'" + ", ");
//					}
//				} else {
//					temp.append(map.get(key) + ", ");
//				}
//			}
//			if (temp.toString().endsWith(", ")) {
//				temp.deleteCharAt(temp.toString().lastIndexOf(", "));
//			}
//			builder.append(temp + ")");
//			System.out.println(builder);
//			writer.write(builder.toString());
//			writer.write("\nGO\n");
//			System.out.println("GO");
//		}
//	}
//
//	/**
//	 * Get and return column type.
//	 * 
//	 * @param tableName
//	 * @param colName
//	 * @return
//	 */
//	private String getTypeOfColumn(String tableName, String colName) {
//		Map<String, String> columnTypes = columnsMapping.get(tableName);
//		if (null != columnTypes) {
//			return columnTypes.get(colName);
//		}
//		return tableName;
//	}
//
//	/**
//	 * @param list
//	 * @return
//	 */
//	private Set<String> getRGblGrpIds(List<Map<String, Object>> list) {
//		Set<String> set = new HashSet<>();
//		for (Map<String, Object> map : list) {
//			set.add(String.valueOf(map.get("r_gbl_grp_id")));
//		}
//		return set;
//	}
//
//	private JdbcTemplate loadUiTemplate() {
//		if (jdbcTemplate != null) {
//			return jdbcTemplate;
//		}
//		BasicDataSource dataSource = new BasicDataSource();
//		dataSource.setUsername(userName);
//		byte[] encPwd = Base64.getDecoder().decode("TWFsbGk0OTg=");
//		String pwd = new String(encPwd);
//		dataSource.setPassword(password);
//		dataSource.setMaxIdle(1);
//		dataSource.setMaxConnLifetimeMillis(50000);
//		dataSource.setUrl("jdbc:mysql://dbsrd3986:3306/d3ui02?currentSchema=doc360_ui&useSSL=true&serverTimezone=CST");
////		dataSource.setUrl("jdbc:mysql://dbvrs16762:3306,dbvrs16763:3306,dbvrs16764:3306/d3ui02?autoReconnect=true&failOverReadOnly=false&secondsBeforeRetryMaster=0&queriesBeforeRetryMaster=0&currentSchema=doc360_ui&useSSL=false&serverTimezone=CST");
//		jdbcTemplate = new JdbcTemplate();
//		jdbcTemplate.setDataSource(dataSource);
//		return jdbcTemplate;
//	}
//
//	/**
//	 * Batch processor DB template
//	 * 
//	 * @return
//	 */
//	private JdbcTemplate loadBatchDbTemplate() {
//		if (batchJdbcTemplate != null) {
//			return batchJdbcTemplate;
//		}
//		BasicDataSource dataSource = new BasicDataSource();
//		dataSource.setUsername("edmin00sc00");
//		dataSource.setPassword("nvz08qyAu");
//		dataSource.setMaxIdle(1);
//		dataSource.setMaxConnLifetimeMillis(50000);
//		dataSource.setUrl("jdbc:postgresql://apsrt4416.uhc.com:5432/dm_edmin00_ingest?currentSchema=doc360_recon");
//		batchJdbcTemplate = new JdbcTemplate();
//		batchJdbcTemplate.setDataSource(dataSource);
//		return batchJdbcTemplate;
//	}
//
//	private JdbcTemplate loadRioDbTemplate() {
//		if (rioJdbcTemplate != null) {
//			return rioJdbcTemplate;
//		}
//		BasicDataSource dataSource = new BasicDataSource();
//		dataSource.setUsername(userName);
//		byte[] encPwd = Base64.getDecoder().decode("TWFsbGk0OTg=");
//		String pwd = new String(encPwd);
//		dataSource.setPassword(password);
//		dataSource.setMaxIdle(1);
//		dataSource.setMaxConnLifetimeMillis(50000);
//		dataSource.setUrl("jdbc:mysql://dbsrd3986:3306/d3ui01?useSSL=true&serverTimezone=CST");
//		rioJdbcTemplate = new JdbcTemplate();
//		rioJdbcTemplate.setDataSource(dataSource);
//		return rioJdbcTemplate;
//	}
//
//	/*
//	 * private void loadBatchTables() { JdbcTemplate template =
//	 * getBatchDbTemplate(); List<Map<String, Object>> list =
//	 * template.queryForList(
//	 * "select table_name, column_name, data_type from information_schema.columns where table_name in "
//	 * + batchTables); Map<String, String> newMap = new HashMap<>();
//	 * for(Map<String, Object> map : list) { String tableName =
//	 * String.valueOf(map.get("table_name"));
//	 * if(!columnsMapping.containsKey(tableName)) { newMap = new HashMap<>();
//	 * newMap.put(String.valueOf(map.get("column_name")),
//	 * String.valueOf(map.get("data_type"))); columnsMapping.put(tableName,
//	 * newMap); } else {
//	 * columnsMapping.get(tableName).put(String.valueOf(map.get("column_name")),
//	 * String.valueOf(map.get("data_type"))); } } }
//	 */
//
//	private void loadTables(JdbcTemplate jdbcTemplate, String tables) {
//		List<Map<String, Object>> list = jdbcTemplate.queryForList(
//				"select table_name, column_name, data_type from information_schema.columns where table_name in "
//						+ tables);
//		Map<String, String> newMap = new HashMap<>();
//		for (Map<String, Object> map : list) {
//			String tableName = String.valueOf(map.get("table_name"));
//			if (!columnsMapping.containsKey(tableName)) {
//				newMap = new HashMap<>();
//				newMap.put(String.valueOf(map.get("column_name")), String.valueOf(map.get("data_type")));
//				columnsMapping.put(tableName, newMap);
//			} else {
//				columnsMapping.get(tableName).put(String.valueOf(map.get("column_name")),
//						String.valueOf(map.get("data_type")));
//			}
//		}
//	}
//}
//
