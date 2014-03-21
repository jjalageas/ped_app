package ped.myscrum.creation;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import ped.myscrum.gen.R;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class CreateJobFragment extends Fragment{

	private CharSequence api_key;
	private String project_id;
	private String sprint_id;
	private HashMap<String, String> user_story_ids;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_edit_job, container, false);
		
		api_key = getArguments().getString("api_key");
		project_id = getArguments().getString("project_id");
		sprint_id = getArguments().getString("sprint_id");
		
		List<String> status_list = new ArrayList<String>();
		status_list.add("To Do");
		status_list.add("In Progress");
		status_list.add("Done");
		
		List<String> user_stories_list = new ArrayList<String>();
		ArrayAdapter<String> dataAdapter_user_stories = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_spinner_item, user_stories_list);
		 
		final EditText title = (EditText) rootView.findViewById(R.id.edit_title);	
		final EditText description = (EditText) rootView.findViewById(R.id.edit_desc);
		final Spinner status = (Spinner) rootView.findViewById(R.id.spinner_status);
		final EditText difficulty = (EditText) rootView.findViewById(R.id.edit_difficulty);
		final Spinner user_stories = (Spinner) rootView.findViewById(R.id.spinner_us);
		
		Button submit = (Button) rootView.findViewById(R.id.submit);
		Button back = (Button) rootView.findViewById(R.id.back);
		
		back.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStackImmediate();
			}

		});
		
		new BacklogInformationRetrieval(user_stories_list, user_stories, dataAdapter_user_stories).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/sprints/" + sprint_id + "/user_stories?api_key=" + api_key);
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_spinner_item, status_list);
		
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		status.setAdapter(dataAdapter);
		
		submit.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				new PostJob(title, description, status, difficulty, user_stories).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/sprints/" + sprint_id +"/create_job?api_key=" + api_key);
				getFragmentManager().popBackStackImmediate();
			}

		});
		
		return rootView;
		
	}
	
	private class PostJob extends AsyncTask<String, String, String>{

		private EditText title;	
		private EditText description;
		private Spinner status;
		private EditText difficulty;
		private Spinner user_stories;

		public PostJob(EditText title, EditText description, Spinner status, EditText difficulty, Spinner user_stories){
			this.title = title;
			this.description = description;
			this.status = status;
			this.difficulty = difficulty;
			this.user_stories = user_stories;

		}


		protected String doInBackground(String... url){

			String result = " ";
			String line;
			BufferedReader rd;
			HttpURLConnection conn;

			String status_string = "";

			if(status.getSelectedItem().equals("To Do"))
				status_string = "todo";
			else
				if(status.getSelectedItem().equals("In Progress"))
					status_string = "in progress";
				else
					status_string ="done";
			
			try {
				URL url_init;
				url_init = new URL(url[0]);



				conn = (HttpURLConnection) url_init.openConnection();
				conn.setRequestMethod("POST");

				conn.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
				wr.writeBytes("job={\"title\":\"" + title.getText() + "\",\"description\":\"" + description.getText() 
						+ "\",\"status\":\"" + status_string + "\",\"difficulty\":\"" + difficulty.getText() 
						+ "\",\"user_story_id\":\"" + user_story_ids.get(user_stories.getSelectedItem()) 
						+ "\"}");
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
	
	private class BacklogInformationRetrieval extends AsyncTask<String, String, String>{


		private Spinner user_stories;
		private List<String> user_stories_list;
		private ArrayAdapter<String> dataAdapter;


		public BacklogInformationRetrieval(List<String> list, Spinner spinner, ArrayAdapter<String> adapter){
			user_stories_list = list;
			user_stories = spinner;
			dataAdapter = adapter;
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
			user_story_ids = new HashMap<String, String>();

			JSONArray data;
			try {
				data = new JSONArray(result);
				for(int i=0; i<data.length(); i++){
					user_stories_list.add(data.getJSONObject(i).getString("title"));
					user_story_ids.put(data.getJSONObject(i).getString("title"), data.getJSONObject(i).getString("id"));
				}
				dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				user_stories.setAdapter(dataAdapter);
			} catch (JSONException e) {
				e.printStackTrace();
			}




		}
	}

	
}
