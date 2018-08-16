package www.autobusi.httpreqeust;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
 
public class SSLClientAndServerAuthentigation3 {
    public static void main(String[] args) throws Exception {
    	
    	//login via HTTPUnit
    	InputStream kis = new FileInputStream(new File("C:\\Jacky\\program\\keystores\\clientkeystore.p12"));
        WebClient client = new WebClient(BrowserVersion.EDGE);
        client.setJavaScriptTimeout(0);
        WebClientOptions wco = client.getOptions();
        wco.setSSLClientCertificate(kis, "iloveyou", "PKCS12");
    	wco.setUseInsecureSSL(true);
    	WebRequest req = new WebRequest(new URL("https://solmancf1-approuter-pds.cfapps.sap.hana.ondemand.com/pds"),HttpMethod.GET);
    	
    	HtmlPage page = client.getPage(req);
    	WebResponse loginResponse = page.getWebResponse();
    	kis.close();
    	
    	System.out.println(loginResponse.getContentAsString());
    	
    	//get the cookie which is needed for further requests to server
    	Set<Cookie> loginCookies = client.getCookies(new URL("https://solmancf1-approuter-pds.cfapps.sap.hana.ondemand.com"));
    	Iterator<Cookie> cookieIt = loginCookies.iterator();
    	String cookieStr = "";
    	while(cookieIt.hasNext()){
    		Cookie cookie = cookieIt.next();
    		if("".equals(cookieStr)){
    			cookieStr = cookie.toString();
    		}else{
    			cookieStr = cookieStr + ";" + cookie.toString();
    		}
    	}
    	
    	//do another call 
    	URL servUrl = new URL("https://solmancf1-approuter-pds.cfapps.sap.hana.ondemand.com/pds/api/v1/project/solman_cip");
    	req = new WebRequest(servUrl, HttpMethod.GET);
    	req.setAdditionalHeader("X-Csrf-Token","Fetch");
    	
    	req.setAdditionalHeader("Cookie", cookieStr);
    	UnexpectedPage result = client.getPage(req);
    	System.out.println(result.getWebResponse().getContentAsString());
    	
    }
}

