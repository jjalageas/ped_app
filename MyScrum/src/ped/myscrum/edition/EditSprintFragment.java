package ped.myscrum.edition;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import ped.myscrum.gen.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

public class EditSprintFragment extends Fragment{
	
	private CharSequence api_key;
	private String project_id;
	private String sprint_id;
	private HashMap<String, String> user_story_ids;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_create_sprint, container, false);
		
		api_key = getArguments().getString("api_key");
		project_id = String.valueOf(getArguments().getInt("project_id"));
		sprint_id = String.valueOf(getArguments().getInt("sprint_id"));
		
		List<String> duration_list = new ArrayList<String>();
		List<String> user_stories_list = new ArrayList<String>();
		
		final DatePicker date = (DatePicker) rootView.findViewById(R.id.start_date);	
		final Spinner duration = (Spinner) rootView.findViewById(R.id.duration);
		final Spinner user_stories = (Spinner) rootView.findViewById(R.id.user_stories);

		for(int i=1; i<8; i++)
			duration_list.add(String.valueOf(i));
		ArrayAdapter<String> dataAdapter_duration = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_spinner_item, duration_list);
		dataAdapter_duration.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		duration.setAdapter(dataAdapter_duration);
		
		ArrayAdapter<String> dataAdapter_user_stories = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_spinner_item, user_stories_list);
		
		new BacklogInformationRetrieval(user_stories_list, user_stories, dataAdapter_user_stories).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/user_stories?api_key=" + api_key);
		
		Button submit = (Button) rootView.findViewById(R.id.submit);
		Button back = (Button) rootView.findViewById(R.id.back);
		
		back.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStackImmediate();
			}

		});

		new SprintInformationRetrieval(date, duration, user_stories).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/sprints/" + sprint_id + "/show?api_key=" + api_key);
		
		submit.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				new PostSprint(date, duration, user_stories).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/sprints/" + sprint_id + "/edit?api_key=" + api_key);
				getFragmentManager().popBackStackImmediate();
			}

		});
		
		return rootView;
	
	}
	
	private class PostSprint extends AsyncTask<String, String, String>{
		
		DatePicker date;	
		Spinner duration;
		Spinner user_stories;

		
		public PostSprint(DatePicker date, Spinner duration, Spinner user_stories){
			this.date = date;
			this.duration = duration;
			this.user_stories = user_stories;
		}

		@SuppressLint("SimpleDateFormat")
		protected String doInBackground(String... url){

			String result = " ";
			String line;
			BufferedReader rd;
			HttpURLConnection conn;
			
			String string_date = String.valueOf(date.getYear()) + String.valueOf(date.getMonth()) + String.valueOf(date.getDayOfMonth());		
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date updated_at = new Date();
			
			try {
				URL url_init;
				url_init = new URL(url[0]);



				conn = (HttpURLConnection) url_init.openConnection();
				conn.setRequestMethod("POST");

				conn.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
				
				wr.writeBytes("Object={\"sprint\": {\"updated_at\": \"" + dateFormat.format(updated_at) + "\",\"duration\": "
		        		+ "\"" + duration.getSelectedItem().toString() +  "\",\"project_id\": \"" + project_id + "\",\"start_date\": \""+ string_date +"\"}, \"user_stories\": "
		        		+ "{\"sprint_id\": \""+ sprint_id +"\",\"user_story_id\": \"" + user_story_ids.get(user_stories.getSelectedItem()) + "\"}} ");
				
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
	
	private class SprintInformationRetrieval extends AsyncTask<String, String, String>{
		
		DatePicker date;	
		Spinner duration;
		
		public SprintInformationRetrieval(DatePicker date, Spinner duration, Spinner user_stories){
			this.date = date;
			this.duration = duration;
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
			user_story_ids = new HashMap<String, String>();
	      
			JsonParserFactory factory=JsonParserFactory.getInstance();
	        JSONParser parser=factory.newJsonParser();
			final Map parsedData = parser.parseJson(result);
			
			String date_string = (String) parsedData.get("start_date");
			String year = date_string.substring(0,4);
			String month = date_string.substring(5,7);
			String day = date_string.substring(8,10);
			
			System.out.println(year + month + day);
			
			date.updateDate(Integer.valueOf(year), Integer.valueOf(month) - 1, Integer.valueOf(day));
			duration.setSelection(Integer.valueOf((String) parsedData.get("duration")) - 1);
			//user_stories.setText((CharSequence) parsedData.get(""));

		
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
