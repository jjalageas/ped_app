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

public class CreateTestFragment extends Fragment{
	
	private CharSequence api_key;
	private int project_id;
	private HashMap<String, String> user_story_ids;
	private HashMap<String, String> user_ids;

	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_create_test, container, false);
        
        Button back = (Button) rootView.findViewById(R.id.back);
        Button submit = (Button) rootView.findViewById(R.id.submit);
        
		api_key = getArguments().getString("api_key");
		project_id = getArguments().getInt("project_id");
        
        final EditText title = (EditText) rootView.findViewById(R.id.edit_title);
        final EditText input = (EditText) rootView.findViewById(R.id.input_edit);
        final EditText test_case = (EditText) rootView.findViewById(R.id.test_case_edit);
        final EditText expected_result = (EditText) rootView.findViewById(R.id.expected_result_edit);
        
        final Spinner state = (Spinner) rootView.findViewById(R.id.state_spinner);
        final Spinner testers = (Spinner) rootView.findViewById(R.id.test_spinner);
        final Spinner user_story = (Spinner) rootView.findViewById(R.id.user_story_spinner);
        
        //Retrieve user stories
        List<String> user_stories_list = new ArrayList<String>();
        ArrayAdapter<String> dataAdapter_user_stories = new ArrayAdapter<String>(this.getActivity(),
        		android.R.layout.simple_spinner_item, user_stories_list);
        
        new BacklogInformationRetrieval(user_stories_list, user_story, dataAdapter_user_stories).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/user_stories?api_key=" + api_key);
        
        //Retrieve testers
        List<String> testers_list = new ArrayList<String>();
        ArrayAdapter<String> dataAdapter_testers = new ArrayAdapter<String>(this.getActivity(),
        		android.R.layout.simple_spinner_item, testers_list);
        
        new TeamInformationRetrieval(testers_list, testers, dataAdapter_testers).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/users?api_key=" + api_key);
        
        //State
        List<String> state_list = new ArrayList<String>();
        ArrayAdapter<String> dataAdapter_state = new ArrayAdapter<String>(this.getActivity(),
        		android.R.layout.simple_spinner_item, state_list);
        state_list.add("Not Tested");
        state_list.add("Failed");
        state_list.add("Success");
        dataAdapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		state.setAdapter(dataAdapter_state);
		
		back.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStackImmediate();
			}
			
		});
		
		submit.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				new PostTest(title, input, test_case, expected_result, user_story, testers, state).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/tests?api_key=" + api_key);
				getFragmentManager().popBackStackImmediate();
			}
			
		});
        
        return rootView;
        
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

private class TeamInformationRetrieval extends AsyncTask<String, String, String>{
	

	private Spinner testers;
	private List<String> testers_list;
	private ArrayAdapter<String> dataAdapter;

	
	public TeamInformationRetrieval(List<String> list, Spinner spinner, ArrayAdapter<String> adapter){
		testers_list = list;
		testers = spinner;
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
		user_ids = new HashMap<String, String>();

		JSONArray data;
		try {
			data = new JSONArray(result);
			for(int i=0; i<data.length(); i++){
				testers_list.add(data.getJSONObject(i).getString("username"));
				user_ids.put(data.getJSONObject(i).getString("username"), data.getJSONObject(i).getString("id"));
			}
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			testers.setAdapter(dataAdapter);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	


	}
}

private class PostTest extends AsyncTask<String, String, String>{
	
	private EditText title;
	private EditText input;
	private EditText test_case;
	private EditText expected_result;
	private Spinner user_stories;
	private Spinner testers;
	private Spinner state;

	
	public PostTest(EditText title, EditText input, EditText test_case, EditText expected_result, Spinner user_stories, Spinner testers, Spinner state){
		this.title = title;
		this.input = input;
		this.test_case = test_case;
		this.expected_result = expected_result;
		this.user_stories = user_stories;
		this.testers = testers;
		this.state = state;
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
			wr.writeBytes("test={\"title\":\"" + title.getText() + "\",\"input\":\"" + input.getText() + "\",\"test_case\":\"" + test_case.getText() + "\",\"expected\":\"" + 
			expected_result.getText() + "\",\"user_story_id\":\"" + user_story_ids.get(user_stories.getSelectedItem().toString()) + "\",\"state\":\"" + state.getSelectedItem().toString() + "\",\"owner_id\":\"" + user_ids.get(testers.getSelectedItem().toString()) + "\"}");
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
