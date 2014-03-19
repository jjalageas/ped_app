package ped.myscrum.edition;

import info.androidhive.slidingmenu.R;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditSettingsFragment extends Fragment{

	public EditSettingsFragment(){}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_edit_settings, container, false);
		final EditText username = (EditText) rootView.findViewById(R.id.username);
		final EditText firstname = (EditText) rootView.findViewById(R.id.first_name);
		final EditText lastname = (EditText) rootView.findViewById(R.id.last_name);
		final EditText email = (EditText) rootView.findViewById(R.id.email);
		
		username.setText(getArguments().getString("username"));
		firstname.setText(getArguments().getString("firstname"));
		lastname.setText(getArguments().getString("lastname"));
		email.setText(getArguments().getString("email"));
		
		final CharSequence api_key = "c324ae5f02c3e1811a82a1e0338ce06ccbe1f863";

		Button submit = (Button) rootView.findViewById(R.id.submit);

		
		submit.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				new PostSettings(firstname, lastname, username, email).execute("http://10.0.2.2:3000/api/owner/profile?api_key=" + api_key);
				getFragmentManager().popBackStackImmediate();
			}
			
		});
		
		
		

		return rootView;
	}
	
	
	private class PostSettings extends AsyncTask<String, String, String>{
		
		private EditText firstName;
		private EditText lastName;
		private EditText username;
		private EditText email;

		
		public PostSettings(EditText firstName, EditText lastName, EditText username, EditText email){
			this.firstName = firstName;
			this.lastName = lastName;
			this.username = username;
			this.email = email;
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
				wr.writeBytes("owner={\"email\":\"" + email.getText() + "\",\"username\":\"" + username.getText() + "\",\"first_name\":\"" + firstName.getText() + "\",\"last_name\":\"" + 
				lastName.getText() + "\"}");
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
