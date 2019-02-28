package www.autobusi.httpreqeust;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

public class LoadEconomicKeys {

	public static void main(String[] args) {
		LoadEconomicKeys loader = new LoadEconomicKeys();
		loader.readSubNodes("zb","hgyd");
		loader.readSubNodes("zb","hgjd");
		loader.readSubNodes("zb","hgnd");
	}
	
	private void readSubNodes(String id,String dbcode){
		HttpClient httpClient = new DefaultHttpClient();

		HttpPost http = new HttpPost("http://data.stats.gov.cn/easyquery.htm");
		http.setHeader("Content-Type", "application/x-www-form-urlencoded");
		StringEntity entity = new StringEntity("id="+id+"&dbcode="+dbcode+"&wdcode=zb&m=getTree", Charset.forName("UTF-8"));
        entity.setContentEncoding("UTF-8");
        http.setEntity(entity);

		String info = "";
		
        try {
			HttpResponse response = httpClient.execute(http);

			//System.out.println(response.getStatusLine().getStatusCode());
			if(response.getStatusLine().getStatusCode() != 200){
				System.out.println("Http returnning code isn't 200 when reading id "+id);
				return;
			}
			
			BufferedReader reader  = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8")); 
			String line = null;
		    while((line = reader.readLine()) != null)
		    {
		       //System.out.println(line);
		    	info = info + line + "\r\n";
		    }
			//System.out.println(info);
		} catch (ClientProtocolException e) {
			System.out.println("Exception when reading id "+id);
			return;
		} catch (IOException e) {
			System.out.println("Exception when reading id "+id);
			return;
		}
        
        JSONArray json = new JSONArray(info);
        for( int i = 0; i < json.length(); i++){
        	JSONObject node = json.getJSONObject(i);
        	System.out.println(node.toString());
        	if(node.getBoolean("isParent")){
        		this.readSubNodes(node.getString("id"),dbcode);
        	}
        }
	}

}
