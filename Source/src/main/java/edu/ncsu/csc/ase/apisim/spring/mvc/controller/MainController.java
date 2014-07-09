/*package edu.ncsu.csc.ase.apisim.spring.mvc.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.ncsu.csc.ase.apisim.configuration.Configuration;
import edu.ncsu.csc.ase.apisim.configuration.Configuration.APITYPE;
import edu.ncsu.csc.ase.apisim.dataStructure.APIMtd;
import edu.ncsu.csc.ase.apisim.dataStructure.APIType;
import edu.ncsu.csc.ase.apisim.lucene.analyzer.SynonymAnalyzer;
import edu.ncsu.csc.ase.apisim.lucene.searcher.APISearcher;
import edu.ncsu.csc.ase.apisim.spring.mvc.cache.APICache;
import edu.ncsu.csc.ase.apisim.spring.mvc.displayBeans.Result;

@Controller
public class MainController {

	@RequestMapping("/")
	public String hello() {
		return "hello";
	}

	@RequestMapping(value = "/searchAPI", method = RequestMethod.GET)
	public String searchAPI(@RequestParam("api") String api, @RequestParam("repo") String choice, Model model) {
		
		
		APICache cache = APICache.getInstance();
		APIType clazz = cache.getAPIClass(APITYPE.valueOf(api), choice);
		List<String> result = new ArrayList<>();
		for(APIMtd mtd: clazz.getMethod())
		{
			result.add(clazz.getPackage() + "." + clazz.getName() + " " + mtd.getName() + " " +  mtd.getDescription());
		}
		model.addAttribute("message", result);
		model.addAttribute("api", api);
		return "searchAPI";
	}
	
	@RequestMapping(value = "/exploreAPI", method = RequestMethod.GET)
	public String exploreAPI(@RequestParam("repo") String choice, Model model) {
		
		APICache cache = APICache.getInstance();
		List<APIType> classList;
		switch (choice)
		{
			case "EXPLOREANDROID":
				classList = cache.getClassList(APITYPE.ANDROID);
				model.addAttribute("api", APITYPE.ANDROID.name());
				break;
			case "EXPLORECLDC":
				classList = cache.getClassList(APITYPE.CLDC);
				model.addAttribute("api", APITYPE.CLDC.name());
				break;
			case "EXPLOREMIDP":
				classList = cache.getClassList(APITYPE.MIDP);
				model.addAttribute("api", APITYPE.MIDP.name());
				break;
			default:
				classList = new ArrayList<>();
				break;
		}
		
		List<String> result = new ArrayList<>();
		
		for(APIType clazz: classList)
			result.add(clazz.getPackage() + "." + clazz.getName());
		
		model.addAttribute("message", result);
		return "exploreAPI";
	}
	
	@RequestMapping(value = "/ajaxSimSearch", method = RequestMethod.GET)
    public @ResponseBody
    String simSearch(@RequestParam("query") String queryTerm, @RequestParam("repo") String repo) {
 
		
		List<Result> mtdList = new ArrayList<>();
		
		APISearcher searcher = new APISearcher();
		BooleanClause.Occur bc1 = BooleanClause.Occur.MUST;
		BooleanClause.Occur bc2 = BooleanClause.Occur.SHOULD;
		BooleanClause.Occur[] flags = { bc1, bc2 };
		
		queryTerm = MultiFieldQueryParser.escape(queryTerm);
		
		String[] query = {"android", queryTerm};
		String[] fields = { Configuration.IDX_FIELD_API_NAME, Configuration.IDX_FIELD_METHOD_DESCRIPTION };
		try 
		{
			Query q = MultiFieldQueryParser.parse(Version.LUCENE_47, query, fields, flags, new SynonymAnalyzer());
			mtdList = searcher.search(q);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		String result = decorate(mtdList);
		return result;
    }
	
	@RequestMapping(value = "/exploreOperation", method = RequestMethod.GET)
	public String exploreOperation(@RequestParam("search") String search, Model model) {
		List<Operation> operList = APICache.getInstance().getOperations();
		Operation oper = APICache.getInstance().getOperation(search);
		model.addAttribute("message", operList);
		model.addAttribute("operation", oper);
		return "exploreOperation";
	}
	
	@RequestMapping(value = "/displayResultsREST", method = RequestMethod.GET)
	public String displayResultsREST(@RequestParam("searchStr") String searchStr, @RequestParam("search") String search, Model model) throws IOException {
		
		
		if((search !=null) && (search.length()>0))
		{
			return "redirect:/exploreNLP?search="+search;
		}
		RESTAPISearcher searcher = new RESTAPISearcher();
		try 
		{
			Query q = new QueryParser(Version.LUCENE_44, "Description", new EnglishAnalyzer(Version.LUCENE_44)).parse(searchStr);
			model.addAttribute("message", searcher.search(q));
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		model.addAttribute("searchStr", searchStr);
		return "displayResultsREST";
	}
	
	@RequestMapping(value = "/exploreNLP", method = RequestMethod.GET)
	public String exploreNLP(@RequestParam("search") String search, Model model) throws Exception {
		List<NLPResult> str;
		List<CorefChain> corefList;
		if((search!=null)&&(search.trim().equalsIgnoreCase("")))
		{
			str = new ArrayList<NLPResult>();
			corefList = new ArrayList<CorefChain>();
		}
		else if((search!=null)&&(search.trim().equalsIgnoreCase("&&")))
		{
			SentenceCache.getInstace().updatePersistance();
			str = new ArrayList<NLPResult>();
			corefList = new ArrayList<CorefChain>();
			search = "Cache Updated!!";
		}
		else
		{
			NLPParser parser = NLPParser.getInstance();
			str = parser.getDisplayList(search);
			corefList = parser.getCoRefDependencies(search);
		}
		model.addAttribute("message", str);
		model.addAttribute("coref", corefList);
		model.addAttribute("searchstr", search);
		return "exploreNLP";
	}

	private String decorate(List<Result> mtdList) {
		StringBuffer buff = new StringBuffer();
		buff.append("<ul>\n");
		for(Result res: mtdList)
		{
			buff.append("\t<li>\n");
			buff.append("<font color=\"green\">");
			buff.append("\t\t<b>");
			buff.append(res.getClassName());
			buff.append("</b>  ");
			buff.append("<i>");
			buff.append(res.getMethodName());
			buff.append("</i>  ");
			buff.append(res.getDescription());
			buff.append("</font>");
			buff.append("\t</li>\n");
		}
		buff.append("</ul>\n");
		return buff.toString();
	}
	
	
	
}
*/