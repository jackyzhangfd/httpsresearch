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
 
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
 
public class SSLClientAndServerAuthentigation2 {
    public static void main(String[] args) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
    	//KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream(new File("C:\\Jacky\\program\\keystores\\clientkeystore.p12")), "iloveyou".toCharArray());
        SSLContext sslcontext = SSLContexts.custom()
                
        		//方式1：忽略掉对服务器端证书的校验
                .loadTrustMaterial(new TrustStrategy() {
                    //@Override
                    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        return true;
                    }
                })
                
        		/*
                //方式2：加载服务端提供的truststore(如果服务器提供truststore的话就不用忽略对服务器端证书的校验了)
                .loadTrustMaterial(new File("C:\\Jacky\\program\\keystores\\clienttruststore.keystore"), "iloveyou".toCharArray(),
                        new TrustSelfSignedStrategy())
                */
                .loadKeyMaterial(keyStore, "iloveyou".toCharArray())
                
                .build();
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                sslcontext,
                new String[]{"TLSv1.2"},
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build();
 
        try {
            //HttpGet httpget = new HttpGet("https://bctmain.wdf.sap.corp/sap/bc/devdb/my_saved_search?sap-client=001&format=json");
            //HttpGet httpget = new HttpGet("https://ldcicix.wdf.sap.corp:44300/sap/opu/odata/sap/CBLD_PROJ_ANALYTIC_SRV/$metadata?sap-client=200&sap-language=EN");
        	HttpGet httpget = new HttpGet("https://solmancf1-approuter-pds.cfapps.sap.hana.ondemand.com/pds/api/v1/utc/loadedutcdates");
        	//httpget.addHeader("X-Uaa-Csrf", "UtnrzDMzP2SazJZR1bVLK8");
            System.out.println("Executing request " + httpget.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                System.out.println(response.getStatusLine());
                System.out.println(IOUtils.toString(entity.getContent()));
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }
}

