package ped.myscrum;

import info.androidhive.slidingmenu.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import ped.myscrum.serialization.AccountSettings;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

public class AccountSettingsFragment extends Fragment{

	private CharSequence api_key;
	AccountSettings settings_data;
	
	public AccountSettingsFragment(){}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		api_key = getArguments().getString("api_key");
		View rootView = inflater.inflate(R.layout.fragment_account_settings, container, false);

		TextView username = (TextView) rootView.findViewById(R.id.username);
		TextView firstname = (TextView) rootView.findViewById(R.id.first_name);
		TextView lastname = (TextView) rootView.findViewById(R.id.last_name);
		TextView email = (TextView) rootView.findViewById(R.id.email);

		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

		if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
			OwnerInformationRetrieval oir = new OwnerInformationRetrieval(username, firstname, lastname, email);
			oir.execute("http://10.0.2.2:3000/api/owner/profile?api_key=" + api_key);    
		}

		else{
			try
			{
				settings_data = load_data(new File(this.getActivity().getFilesDir() + "account_settings.bin"));
				String un = settings_data.getUsername();
				String ln = settings_data.getLastname();
				String fn = settings_data.getFirstname();
				String em = settings_data.getEmail();
				username.setText(un);
				firstname.setText(fn);
				lastname.setText(ln);
				email.setText(em);
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}

		}

		return rootView;
	}


	public void save_data() throws FileNotFoundException, IOException{
		File save = new File(this.getActivity().getFilesDir() + "account_settings.bin");
		save.createNewFile();
		System.out.println(save.getAbsolutePath());
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(save));
		oos.writeObject(settings_data);
		oos.flush();
		oos.close();
	}

	@SuppressWarnings("resource")
	public AccountSettings load_data(File f) throws StreamCorruptedException, FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
		settings_data = (AccountSettings) ois.readObject();
		return settings_data;
	}
	
	
	private class OwnerInformationRetrieval extends AsyncTask<String, String, String>{
		
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
			settings_data = new AccountSettings((String) parsedData.get("username"), (String) parsedData.get("first_name"), (String) parsedData.get("last_name"), (String) parsedData.get("email"));
			try {
				save_data();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
		
	}
	
}
