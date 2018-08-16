package www.autobusi.httpreqeust;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
public class SSLClientAndServerAuthentigation1 {
	private final Logger logger = LoggerFactory.getLogger(SSLClientAndServerAuthentigation1.class);
	private InputStream trustFileInputStream = null;
	private InputStream keyFileInputStream = null;
	private String trustPasswd = null;
	private String keyPasswd = null;
	
	public static void main( String[] args )
    {
        try {
			InputStream keyStoreFile   = new FileInputStream(new File("C:\\Jacky\\program\\keystores\\clientkeystore.p12"));
			InputStream trustStoreFile = new FileInputStream(new File("C:\\Jacky\\program\\keystores\\clienttruststore.keystore"));
			SSLClientAndServerAuthentigation1 requestor = new SSLClientAndServerAuthentigation1(trustStoreFile,keyStoreFile, "iloveyou","iloveyou");
			requestor.httpRequest("https://bctmain.wdf.sap.corp/sap/bc/devdb/my_saved_search?sap-client=001&format=json", "GET", null);
			//requestor.httpRequest("https://ldcicix.wdf.sap.corp:44300/sap/opu/odata/sap/CBLD_PROJ_ANALYTIC_SRV/$metadata?sap-client=200&sap-language=EN", "GET", null);
			//requestor.httpRequest("https://solmancf1-approuter-pds.cfapps.sap.hana.ondemand.com/pds/api/v1/utc/loadedutcdates", "GET", null);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }
	
	public SSLClientAndServerAuthentigation1(InputStream tfi, InputStream kfi, String trustStorePwd, String keyStorePwd){
		this.trustFileInputStream = tfi;
		this.keyFileInputStream = kfi;
		this.trustPasswd = trustStorePwd;
		this.keyPasswd = keyStorePwd;
	}
	
	public String httpRequest(String requestUrl, String requestMethod, String outputStr) {
		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			KeyStore trustKeyStore = KeyStore.getInstance("JKS");
			trustKeyStore.load(trustFileInputStream, trustPasswd.toCharArray());
			tmf.init(trustKeyStore);
			trustFileInputStream.close();

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			KeyStore ks = KeyStore.getInstance("PKCS12");//KeyStore.getInstance("PKCS12");
			ks.load(keyFileInputStream, keyPasswd.toCharArray());
			kmf.init(ks, keyPasswd.toCharArray());
			keyFileInputStream.close();
			
			SecureRandom rand = new SecureRandom();
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), rand);

			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);

			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod(requestMethod);

			if ("GET".equalsIgnoreCase(requestMethod))
				httpUrlConn.connect();

			// 当有数据需要提交时
			if (null != outputStr) {
				OutputStream outputStream = httpUrlConn.getOutputStream();
				// 注意编码格式，防止中文乱码
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}

			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
			System.out.println("返回的数据：" + buffer.toString());
		} catch (ConnectException ce) {
			//ce.printStackTrace();
			logger.error("server connection timed out.");
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("https request error:{}", e);
		}
		return buffer.toString();
	}
}
