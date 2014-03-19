package ped.myscrum.creation;

import info.androidhive.slidingmenu.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class CreateAccountFragment extends Fragment {
	
	public CreateAccountFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
		final String api_key = "";
		
        View rootView = inflater.inflate(R.layout.fragment_create_account, container, false);
        Button sign_up = (Button) rootView.findViewById(R.id.signup_button);
        final EditText first_name = (EditText) rootView.findViewById(R.id.firstname);
        final EditText last_name = (EditText) rootView.findViewById(R.id.lastname);
        final EditText username = (EditText) rootView.findViewById(R.id.username);
        final EditText email = (EditText) rootView.findViewById(R.id.email);
        final EditText password = (EditText) rootView.findViewById(R.id.password);
        final EditText password_confirm = (EditText) rootView.findViewById(R.id.password_confirmation);
        
        sign_up.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				new PostSignup(first_name, last_name, username, password , password_confirm, email).execute("http://10.0.2.2:3000/api/signup?api_key=" + api_key);
				getFragmentManager().popBackStackImmediate();
			}
			
		});
        
        
        return rootView;
    }
	
	private class PostSignup extends AsyncTask<String, String, String>{
		
		final EditText first_name;
        final EditText last_name;
        final EditText username;
        final EditText email;
        final EditText password;
        final EditText password_confirm;
			
			public PostSignup(EditText first, EditText last, EditText user, EditText pass, EditText confirm, EditText mail){
				first_name = first;
		        last_name = last;
		        username = user;
		        password = pass;
		        password_confirm = confirm;
		        email = mail;
			}


			protected String doInBackground(String... url){

				String result = " ";
				String line;
				BufferedReader rd;
				HttpURLConnection conn;
				
				try {
					URL url_init;
					url_init = new URL(url[0]);



					conn = (HttpURLConnection) url_init.openConnection();
					conn.setRequestMethod("POST");

					conn.setDoOutput(true);
					DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
					wr.writeBytes("user={\"first_name\":\"" + first_name.getText() + "\",\"last_name\":\"" + last_name.getText() + "\",\"email\":\"" + 
							email.getText() + "\",\"username\":\"" + username.getText() + "\",\"password\":\"" + password.getText() + "\",\"password_confirmation\":\"" + password_confirm.getText() + "\"}");
					wr.flush();
					wr.close();


					rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					while ((line = rd.readLine()) != null) {
						result += line;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			}
			
			  protected void onPostExecute(String result) {
			  }
	}
	
}
