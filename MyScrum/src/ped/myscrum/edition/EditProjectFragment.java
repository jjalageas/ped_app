package ped.myscrum.edition;

import info.androidhive.slidingmenu.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

public class EditProjectFragment extends Fragment{
	
	private CharSequence api_key;
	private String project_id;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_edit_project, container, false);
		
		api_key = getArguments().getString("api_key");
		project_id = getArguments().getString("project_id");
		
		final EditText title = (EditText) rootView.findViewById(R.id.title_edit);	
		final EditText description = (EditText) rootView.findViewById(R.id.edit_description);
		final EditText repo = (EditText) rootView.findViewById(R.id.edit_repo);
		final CheckBox public_box = (CheckBox) rootView.findViewById(R.id.public_);
		
		Button submit = (Button) rootView.findViewById(R.id.submit);
		Button back = (Button) rootView.findViewById(R.id.back);
		
		back.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStackImmediate();
			}

		});

		new ProjectInformationRetrieval(title, description, repo, public_box).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/project?api_key=" + api_key);
		
		submit.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				new PostProject(title, description, repo, public_box).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/edit?api_key=" + api_key);
				getFragmentManager().popBackStackImmediate();
			}

		});
		
		return rootView;
	
	}
	
	private class PostProject extends AsyncTask<String, String, String>{
		
		private EditText title;
		private EditText description;
		private EditText repo;
		private CheckBox public_box;
		
		public PostProject(EditText title, EditText description, EditText repo, CheckBox public_box){
			this.title = title;
			this.description = description;
			this.repo = repo;
			this.public_box = public_box;
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
				wr.writeBytes("project={\"title\":\"" + title.getText() + "\",\"description\":\"" + description.getText() + "\",\"repo\":\"" + repo.getText() + "\",\"public\":\"" + 
				public_box.isChecked() + "\"}");
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
	
	private class ProjectInformationRetrieval extends AsyncTask<String, String, String>{
		
		private EditText title;
		private EditText description;
		private EditText repo;
		private CheckBox public_box;
		
		public ProjectInformationRetrieval(EditText title, EditText description, EditText repo, CheckBox public_box){
			this.title = title;
			this.description = description;
			this.repo = repo;
			this.public_box = public_box;
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
		
		
		@SuppressWarnings("rawtypes")
		@Override
	    protected void onPostExecute(String result) {
	        
			super.onPostExecute(result);
	      
			JsonParserFactory factory=JsonParserFactory.getInstance();
	        JSONParser parser=factory.newJsonParser();
			final Map parsedData = parser.parseJson(result);
			
			title.setText((CharSequence) parsedData.get("title"));
			description.setText((CharSequence) parsedData.get("description"));
			repo.setText((CharSequence) parsedData.get("repo"));
			if((parsedData.get("public")).equals("true"))
				public_box.setChecked(true);
		
	    }
		
	}
	

}
