package ped.myscrum.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.AsyncTask;
import android.widget.TextView;

public class DescriptionRetrieval extends AsyncTask<String, String, String>{

	private TextView description;
	
	public DescriptionRetrieval(TextView des){
		description = des;
	}
	
	@Override
	protected String doInBackground(String... url){
		String result = " ";
		URL url_init;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		try{
		url_init = new URL(url[0]);
		conn = (HttpURLConnection) url_init.openConnection();
		conn.setRequestMethod("GET");
		rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while ((line = rd.readLine()) != null) {
			result += line;
		}
		rd.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        System.out.println(result);
        result = result.substring(2, result.length()-1);
		description.setText(result);	
    }
	
}
