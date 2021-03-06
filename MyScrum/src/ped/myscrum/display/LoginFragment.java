
package ped.myscrum.display;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ped.myscrum.creation.CreateAccountFragment;
import ped.myscrum.gen.R;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.app.FragmentManager;

public class LoginFragment extends Fragment{

	private Button loginButton;
	private EditText apiKey;
	
	public LoginFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        
        apiKey = (EditText) rootView.findViewById(R.id.key);
        loginButton = (Button) rootView.findViewById(R.id.login_button);
        Button signup = (Button) rootView.findViewById(R.id.signup_button);
        
        signup .setOnClickListener(new OnClickListener() {			
        	@Override
        	public void onClick(View v) {
        		Fragment fragment = new CreateAccountFragment();
        		FragmentManager fragmentManager = getFragmentManager();
        		fragmentManager.beginTransaction()
        		.replace(R.id.frame_container, fragment).addToBackStack("pop").commit();
        	}
        });	
      
        loginButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				
				Editable key = apiKey.getText();
				String stringApiKey = key.toString();
				
				new Login(stringApiKey, apiKey).execute("http://10.0.2.2:3000/api/owner/profile/api?api_key=" + stringApiKey);
				
			}
		});
         
        return rootView;
    }
	
    
	private void startLoginActivity(String stringApiKey) {
    	Intent intent = new Intent(LoginFragment.this.getActivity(), MainActivityLoggedIn.class);
    	Bundle b = new Bundle();
    	b.putCharSequence("api_key", stringApiKey);
    	intent.putExtras(b);
    	startActivity(intent);
    }
	
	private class Login extends AsyncTask<String, String, String>{
		
		private String key;
		private EditText text;

		
		public Login(String s, EditText et){
			key = s;
			text = et;
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
			if(result.length()<key.length())
				text.setText("Invalid API Key");
			else{
				String key_tmp = result.substring(2, result.length()-1);
				if(key.equals(key_tmp))
					startLoginActivity(key);
			}

		}
	}
	
	
}
