package com.example.model;

public class FtlMailTemplateConstant {

	// Define for Operatortrategy
	public static final String NEW_OPERATOR_NOTIFY = "NewOperatorNotify.ftl";
	
	// Define for NotifyService
	public static final String ETL_SUCCESS_NOTIFY = "ETL-SuccessNotify.ftl";
	public static final String ETL_FAIL_NOTIFY = "ETL-SrcErrorNotify.ftl";
	public static final String ETL_SRC_NO_UPDATE_NOTIFY = "ETL-SrcNotUpdateNotify.ftl";
	public static final String IMPORT_FAIL_NOTIFY = "ETL-ImportFailedNotify.ftl";
	public static final String ETL_SUMMARY_NOTIFY = "ETL-SummaryNotify.ftl";

	// Ftl attribute key
	public static final String NEW_OPERATOR_LIST_MAP_KEY = "newOperatorList";
	public static final String ETL_SUMMARY_LIST_MAP_KEY = "etlSummaryList";
	
	private FtlMailTemplateConstant() {
	}
	
}
