package edu.ncsu.csc.ase.apisim.webcrawler.apiutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import edu.ncsu.csc.ase.apisim.topicModel.DataInstance;
import edu.ncsu.csc.ase.apisim.topicModel.InstanceCreator;

public class PackageSummaryCrawler {

	private static List<String> midpPkgUrlLst;
	
	private static List<String> androidPkgUrlLst;
	
	public static final String ANDROID_PKG_DMP_FILE = "android";
	
	public static final String MIDP_PKG_DMP_FILE = "midp";
	
	public static void main(String[] args)throws Exception {
		File file = new File("midp");
	    FileInputStream f = new FileInputStream(file);
	    ObjectInputStream s = new ObjectInputStream(f);
	    HashMap<String, String> fileObj2 = (HashMap<String, String>) s.readObject();
	    s.close();
	    String conString;
	    Element  d;
	    boolean flag= false;
	    StringBuffer buff;
	    for(String key:fileObj2.keySet())
	    {
	    	conString = fileObj2.get(key);
	    	d = Jsoup.parse(conString).body();
	    	
	    	buff = new StringBuffer();
	    	for(Element n:d.children())
	    	{
	    		if((n.hasAttr("name"))&&(n.attr("name").trim().equals("skip-navbar_top"))){
	    			
	    			flag=true;
	    		}
	    		if(flag)
	    		{
	    			if((n.hasAttr("name"))&&(n.attr("name").trim().equals("navbar_bottom"))){
		    			
		    			break;
		    		}
	    			buff.append(n.text());
	    			buff.append(" ");
	    		}
	    	}
	    	System.out.println(buff.toString());
	    	
	    }
	}

	private static void removeCodeBlocks(Element d) {
		Elements e = d.select("code");
		for(Element el:e)
		{
			if(el.text().length()>50)
				el.remove();
		}
	}

	private static boolean checkFlag(Node n) {
		if (n.nodeName().equals("#comment")) {
			 if(n.toString().trim().equals("<!-- ========= END OF TOP NAVBAR ======= -->"))
				 return true;
		 }
		return false;
	}
	
	private static boolean detectEnd(Node n) {
		if (n.nodeName().equals("#a.name")) {
			 if(n.toString().trim().equals("<!-- ========= START OF BOTTOM NAVBAR ======= -->"))
				 return true;
		 }
		return false;
	}

	/**
	 * @param n
	 */
	private static void tst(Node n) {
		
		if (n.nodeName().equals("#comment")) {
			 System.out.print(n.toString());
		 }
	}
	
	public static void downloadPackeages() throws IOException {
		HashMap<String, String> urlMap = getHTMLPages(midpPkgUrlLst());
		writePages(MIDP_PKG_DMP_FILE, urlMap);
        
		urlMap = getHTMLPages(androidPkgUrlLst());
		writePages(ANDROID_PKG_DMP_FILE, urlMap);
	}

	/**
	 * @param urlMap
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void writePages(String fileName, HashMap<String, String> urlMap)
			throws FileNotFoundException, IOException {
		File file = new File(fileName);
        FileOutputStream f = new FileOutputStream(file);
        ObjectOutputStream s = new ObjectOutputStream(f);
        s.writeObject(urlMap);
        s.close();
	}

	/**
	 * @param urlList
	 * @return
	 * @throws IOException
	 */
	private static HashMap<String, String> getHTMLPages(List<String> urlList) throws IOException {
		HashMap<String, String> urlMap = new HashMap<String, String>();
		for(String url: urlList)
			urlMap.put(getPkgNameMIDP(url), Jsoup.connect(url).get().toString());
		return urlMap;
	}
	
	
	
	
	public static InstanceList createInstanceList() throws Exception
	{
		List<Instance> insList = new ArrayList<Instance>();
		
		File file = new File("android");
	    FileInputStream f = new FileInputStream(file);
	    ObjectInputStream s = new ObjectInputStream(f);
	    HashMap<String, String> fileObj2 = (HashMap<String, String>) s.readObject();
	    s.close();
	    String conString;
	    Element  d;
	    for(String key:fileObj2.keySet())
	    {
	    	conString = fileObj2.get(key);
	    	d = Jsoup.parse(conString).getElementById("jd-content");
	    	insList.add(new DataInstance(d.text(), key));
	    }
	    
	    file = new File("midp");
	    f = new FileInputStream(file);
	    s = new ObjectInputStream(f);
	    fileObj2 = (HashMap<String, String>) s.readObject();
	    s.close();
	    
	    StringBuffer buff;
	    boolean flag = false;
	    for(String key:fileObj2.keySet())
	    {
	    	conString = fileObj2.get(key);
	    	d = Jsoup.parse(conString).body();
	    	
	    	buff = new StringBuffer();
	    	flag = false;
	    	for(Element n:d.children())
	    	{
	    		if((n.hasAttr("name"))&&(n.attr("name").trim().equals("skip-navbar_top"))){
	    			
	    			flag=true;
	    		}
	    		if(flag)
	    		{
	    			if((n.hasAttr("name"))&&(n.attr("name").trim().equals("navbar_bottom"))){
		    			
		    			break;
		    		}
	    			buff.append(n.text());
	    			buff.append(" ");
	    		}
	    	}
	    	insList.add(new DataInstance(buff.toString(), key));
	    	//System.out.println(buff.toString());
	    	
	    }
		return InstanceCreator.createInstanceList(insList);
	}
	
	private static String getPkgNameMIDP(String url) {
		url = url.replace("http://docs.oracle.com/javame/config/cldc/ref-impl/midp2.0/jsr118/", "");
		url =  url.replace("/package-summary.html", "");
		url = url.replaceAll("/", ".");
		return url;
	}
	
	private static String getPkgNameAndroid(String url) {
		url = url.replace("http://developer.android.com/reference/", "");
		url =  url.replace("/package-summary.html", "");
		url = url.replaceAll("/", ".");
		return url;
	}
	
	public static synchronized List<String> midpPkgUrlLst()
	{
		if(midpPkgUrlLst==null)
		{
			midpPkgUrlLst = new ArrayList<>();
			midpPkgUrlLst.add("http://docs.oracle.com/javame/config/cldc/ref-impl/midp2.0/jsr118/java/util/package-summary.html");
			midpPkgUrlLst.add("http://docs.oracle.com/javame/config/cldc/ref-impl/midp2.0/jsr118/javax/microedition/io/package-summary.html");
			midpPkgUrlLst.add("http://docs.oracle.com/javame/config/cldc/ref-impl/midp2.0/jsr118/javax/microedition/lcdui/package-summary.html");
			midpPkgUrlLst.add("http://docs.oracle.com/javame/config/cldc/ref-impl/midp2.0/jsr118/javax/microedition/lcdui/game/package-summary.html");
			midpPkgUrlLst.add("http://docs.oracle.com/javame/config/cldc/ref-impl/midp2.0/jsr118/javax/microedition/media/package-summary.html");
			midpPkgUrlLst.add("http://docs.oracle.com/javame/config/cldc/ref-impl/midp2.0/jsr118/javax/microedition/media/control/package-summary.html");
			midpPkgUrlLst.add("http://docs.oracle.com/javame/config/cldc/ref-impl/midp2.0/jsr118/javax/microedition/midlet/package-summary.html");
			midpPkgUrlLst.add("http://docs.oracle.com/javame/config/cldc/ref-impl/midp2.0/jsr118/javax/microedition/pki/package-summary.html");
			midpPkgUrlLst.add("http://docs.oracle.com/javame/config/cldc/ref-impl/midp2.0/jsr118/javax/microedition/rms/package-summary.html");
		}
		return midpPkgUrlLst;
	}
	
	public synchronized static List<String> androidPkgUrlLst()
	{
		if(androidPkgUrlLst== null)
		{
			androidPkgUrlLst = new ArrayList<>();
			androidPkgUrlLst.add("http://developer.android.com/reference/android/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/accessibilityservice/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/animation/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/app/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/app/admin/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/app/backup/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/appwidget/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/bluetooth/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/content/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/content/pm/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/content/res/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/database/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/drm/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/gesture/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/graphics/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/graphics/drawable/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/graphics/drawable/shapes/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/graphics/pdf/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/hardware/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/hardware/display/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/hardware/input/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/hardware/location/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/inputmethodservice/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/location/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/media/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/media/audiofx/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/media/effect/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/mtp/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/net/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/net/http/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/net/nsd/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/net/rtp/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/net/wifi/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/nfc/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/nfc/cardemulation/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/nfc/tech/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/opengl/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/os/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/os/storage/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/preference/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/print/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/print/pdf/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/printservice/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/provider/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/renderscript/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/sax/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/security/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/service/dreams/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/service/notification/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/speech/package-summary.html");
			//urlList.add("http://developer.android.com/reference/android/support/annotation/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v13/app/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v4/accessibilityservice/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v4/app/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v4/content/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v4/content/pm/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v4/database/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v4/graphics/drawable/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v4/hardware/display/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v4/net/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v4/os/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v4/print/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v4/text/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v4/util/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v4/view/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v4/view/accessibility/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v4/widget/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v7/app/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v7/appcompat/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v7/gridlayout/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v7/media/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v7/mediarouter/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v7/view/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v7/widget/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/support/v8/renderscript/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/telephony/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/telephony/cdma/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/telephony/gsm/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/test/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/test/mock/package-summary.html");
			//urlList.add("http://developer.android.com/reference/android/test/suitebuilder/annotation/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/text/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/text/format/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/text/method/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/text/style/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/text/util/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/transition/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/util/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/view/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/view/accessibility/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/view/animation/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/view/inputmethod/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/webkit/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/android/widget/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/dalvik/bytecode/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/dalvik/system/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/awt/font/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/beans/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/io/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/lang/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/lang/annotation/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/lang/reflect/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/math/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/net/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/nio/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/nio/channels/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/nio/channels/spi/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/nio/charset/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/nio/charset/spi/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/security/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/security/acl/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/security/cert/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/security/interfaces/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/security/spec/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/sql/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/text/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/util/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/util/concurrent/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/util/concurrent/atomic/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/util/concurrent/locks/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/util/jar/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/util/logging/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/util/prefs/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/util/regex/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/java/util/zip/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/javax/crypto/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/javax/crypto/interfaces/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/javax/crypto/spec/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/javax/microedition/khronos/egl/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/javax/microedition/khronos/opengles/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/javax/net/ssl/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/javax/security/auth/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/javax/security/auth/callback/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/javax/security/auth/login/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/javax/security/cert/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/javax/sql/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/javax/xml/datatype/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/javax/xml/namespace/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/javax/xml/parsers/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/javax/xml/transform/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/javax/xml/transform/dom/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/javax/xml/transform/sax/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/javax/xml/validation/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/junit/framework/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/junit/runner/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/auth/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/auth/params/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/client/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/client/methods/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/client/params/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/client/protocol/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/client/utils/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/conn/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/conn/params/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/conn/routing/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/conn/scheme/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/conn/ssl/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/conn/util/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/cookie/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/cookie/params/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/entity/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/impl/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/impl/auth/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/impl/client/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/impl/conn/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/impl/conn/tsccm/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/impl/cookie/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/impl/entity/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/impl/io/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/io/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/message/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/params/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/protocol/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/apache/http/util/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/json/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/w3c/dom/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/w3c/dom/ls/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/xml/sax/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/xml/sax/ext/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/xml/sax/helpers/package-summary.html");
			androidPkgUrlLst.add("http://developer.android.com/reference/org/xmlpull/v1/sax2/package-summary.html");
		}
		return androidPkgUrlLst;
	}	
}
