package edu.ncsu.csc.realsearch.apisim;

import java.util.List;
import java.util.Map;

import org.apache.lucene.search.Query;

import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.realsearch.apisim.cache.Cache;
import edu.ncsu.csc.realsearch.apisim.databeans.SearchResultBean;
import edu.ncsu.csc.realsearch.apisim.util.LuceneQueryHelper;

/**
 * Handles requests for the application home page.
 */
public class APISim{
	
	
	
	
	public Map<String, List<String>> display() {
		
		return Cache.getInstance().getPkgIdx();
	}
	
	
	public Map<String, String> editRow(String className) {
		return Cache.getInstance().getClassMtdIdx(className);
	}
	
	public List<SearchResultBean> query(String className, Integer mtdIdx) throws Exception {
		APIMtd mtd = Cache.getInstance().getClassMtdAtIdx(className, mtdIdx);
		Query query = LuceneQueryHelper.getQuery(mtd);
		return (LuceneQueryHelper.executeQuery(query));
	}	
}
