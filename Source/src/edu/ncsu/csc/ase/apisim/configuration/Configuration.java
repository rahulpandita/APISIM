package edu.ncsu.csc.ase.apisim.configuration;

import java.io.File;

public class Configuration 
{
	public enum APITYPE{
		ANDROID,
		MIDP,
		CLDC
	}
	
	public static final String PROJECT_PATH = "C:\\Users\\rahulpandita\\Documents\\GitHub\\APISIM\\Source\\";
	
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
	
	public static final String IDX_FIELD_API_NAME = "APINAME";
	
	public static final String IDX_FIELD_CLASS_NAME = "CLASSNAME";
	
	public static final String IDX_FIELD_METHOD_NAME = "METHODNAME";
	
	public static final String IDX_FIELD_METHOD_DESCRIPTION = "MTDDESCRIPTION";
	
	public static final String IDX_FIELD_CLASS_NAME1 = "CLASSNAME1";
	
	public static final String IDX_FIELD_METHOD_DESCRIPTION1 = "MTDDESCRIPTION1";
	
	public static final String IDX_FIELD_METHOD_RETURN = "METHODRETURNTYPE";

	public static final String IDX_FIELD_METHOD_PARAMS = "METHODPARAMS";

	public static final String IDX_FIELD_METHOD_BASE_NAME = "METHODBASENAME";
	
	public static final String IDX_FIELD_METHOD_BASE_NAME_CAMELCASE_SPLIT = "MTDBASENAMECCSPLIT";
	
	
	

}
