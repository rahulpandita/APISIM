package edu.ncsu.csc.ase.apisim.configuration;

import java.io.File;

import org.apache.lucene.util.Version;

public class Configuration 
{
	public enum APITYPE{
		ANDROID,
		MIDP,
		CLDC,
		UNKNOWN
	}
	
	public enum OOTYPE{
		CLASS,
		INTERFACE,
		ENUM,
		UNKNOWN
	}
	
	public static final String PROJECT_PATH = "";
	
	public static final Version LUCENE_VERSION =  Version.LUCENE_4_9;

	public static final String API_LIBS_PATH = PROJECT_PATH + "libs"+ File.separator + "API" + File.separator;
	
	public static final String ROSETTA_RESULTS = PROJECT_PATH + "data"+ File.separator + "Rosetta_Results.xls";

	public static final String WORDNET_DICTIONARY = PROJECT_PATH + "WordNet-3.0" + File.separator + "dict" + File.separator;
	
	public static final String DICTIONARY = PROJECT_PATH + "data" + File.separator + "wn_s.pl"; 
	
	public static final String ANDROID_DUMP_PATH = PROJECT_PATH + "TEMPFILES" + File.separator + "android.api";
	
	public static final String CLDC_DUMP_PATH = PROJECT_PATH + "TEMPFILES" + File.separator + "cldc.api";
	
	public static final String MIDP_DUMP_PATH = PROJECT_PATH + "TEMPFILES" + File.separator + "midp.api";
	
	public static final String ANDROID_ALL_CLASS_URL = "http://developer.android.com/reference/classes.html";
	
	public static final String CLDC_ALL_CLASS_URL = "http://docs.oracle.com/javame/config/cldc/ref-impl/cldc1.1/jsr139/allclasses-frame.html";
	
	public static final String MIDP_ALL_CLASS_URL = "http://docs.oracle.com/javame/config/cldc/ref-impl/midp2.0/jsr118/allclasses-frame.html";
	
	public static final String API_IDX_FILE = PROJECT_PATH + "idx" + File.separator + "idx_all";
	
	public static final String API_IDX_FILE_SYNONYM = PROJECT_PATH + "idx" + File.separator + "idx_syn";
	
	public static final String API_IDX_FILE_SELECTIVE_SYNONYM = PROJECT_PATH + "idx" + File.separator + "idx_sel_syn";
	
	public static final String API_IDX_CLAZZ = PROJECT_PATH + "idx" + File.separator + "classidx";
	
	public static final String API_IDX_CLAZZ_SYN = PROJECT_PATH + "idx" + File.separator + "classidxSyn";
	
	//------------------------------ BEGIN LUCENE IDX FIELDS ----------------------------------------
	
	public static final String IDX_FIELD_API_NAME = "API_NAME";
	
	public static final String IDX_FIELD_CLASS_NAME = "CLS_NAME";
	
	public static final String IDX_FIELD_CLASS_BASE_NAME = "CLS_BASE_NAME";
	
	public static final String IDX_FIELD_CLASS_BASE_NAME_CCSPLIT = "CLS_BASE_NAME_CCSPLIT";
	
	public static final String IDX_FIELD_CLASS_NAME_PKG_SPLIT = "CLS_NAME_PKG_SPLIT";
	
	public static final String IDX_FIELD_METHOD_NAME = "MTD_NAME";
	
	public static final String IDX_FIELD_DESCRIPTION = "DESC";
	
	public static final String IDX_FIELD_METHOD_DESCRIPTION_CATCHALL = "DESC_CATCHALL";
	
	public static final String IDX_FIELD_METHOD_RETURN = "MTD_RET_TYPE";

	public static final String IDX_FIELD_METHOD_PARAMS = "MTD_PARAMS";
	
	public static final String IDX_FIELD_METHOD_EXCEPTIONS = "MTD_EXPTS";

	public static final String IDX_FIELD_METHOD_BASE_NAME = "MTD_BASE_NAME";
	
	public static final String IDX_FIELD_METHOD_BASE_NAME_CAMELCASE_SPLIT = "MTD_BASE_NAME_CCSPLIT";
	
	public static final String IDX_FIELD_ANNOTATED = "ANNOTATED";

	public static final String IDX_FIELD_MODIFIER = "MODIFIER";

	public static final String IDX_FIELD_OO_TYPE = "OO_TYPE";

	public static final String IDX_FIELD_IMPLEMENTS = "IMPLEMENTS";
	
	public static final String IDX_FIELD_EXTENDS = "EXTENDS";
	
	
	//------------------------------ END LUCENE IDX FIELDS ----------------------------------------
	

}
