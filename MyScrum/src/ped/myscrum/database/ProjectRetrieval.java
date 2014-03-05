package ped.myscrum.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class ProjectRetrieval {
	InputStream is;
    String result = "";
    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	
	public void retieveProjects() throws ClientProtocolException, IOException{
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://10.0.2.2/projects.php");
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpResponse response = httpclient.execute(httppost); 
		HttpEntity entity = response.getEntity();
		is = entity.getContent();
	}

}
