package ped.myscrum.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import android.os.AsyncTask;
import android.widget.TextView;

import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

public class OwnerInformationRetrieval extends AsyncTask<String, String, String>{
	
	private TextView firstName;
	private TextView lastName;
	private TextView username;
	private TextView email;

	
	public OwnerInformationRetrieval(TextView firstName, TextView lastName, TextView username, TextView email){
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.email = email;
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
        JsonParserFactory factory=JsonParserFactory.getInstance();
        JSONParser parser=factory.newJsonParser();
		Map parsedData = parser.parseJson(result);
		firstName.setText("First Name:     " + (CharSequence) parsedData.get("first_name"));
		lastName.setText("Last Name:     " + (CharSequence) parsedData.get("last_name"));
		username.setText("Username:      " +(CharSequence) parsedData.get("username"));
		email.setText("Email:               " + (CharSequence) parsedData.get("email"));
    }
	
}
