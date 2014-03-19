package ped.myscrum.creation;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.widget.DatePicker;
import android.widget.Spinner;

public class CreateSprintFragment extends Fragment{

	private String api_key;
	private int project_id;

	public CreateSprintFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_create_sprint, container, false);

		api_key = getArguments().getString("api_key");
		project_id = getArguments().getInt("project_id");

		List<String> duration_list = new ArrayList<String>();
		List<String> user_stories_list = new ArrayList<String>();
		final Spinner duration = (Spinner) rootView.findViewById(R.id.duration);
		final Spinner user_stories = (Spinner) rootView.findViewById(R.id.user_stories);
		final DatePicker date = (DatePicker) rootView.findViewById(R.id.start_date);
		Button submit = (Button) rootView.findViewById(R.id.submit);
		Button back = (Button) rootView.findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStackImmediate();
			}

		});

		for(int i=1; i<8; i++)
			duration_list.add(String.valueOf(i));

		ArrayAdapter<String> dataAdapter_duration = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_spinner_item, duration_list);

		ArrayAdapter<String> dataAdapter_user_stories = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_spinner_item, user_stories_list);

		dataAdapter_duration.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		duration.setAdapter(dataAdapter_duration);

		new BacklogInformationRetrieval(user_stories_list, user_stories, dataAdapter_user_stories).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/user_stories?api_key=" + api_key);

		submit.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				new PostSprint(date, duration, user_stories).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/create_sprint?api_key=" + api_key);
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

			JSONArray data;
			try {
				data = new JSONArray(result);
				for(int i=0; i<data.length(); i++){
					user_stories_list.add(data.getJSONObject(i).getString("title"));
				}
				dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				user_stories.setAdapter(dataAdapter);
			} catch (JSONException e) {
				e.printStackTrace();
			}




		}
	}

	private class PostSprint extends AsyncTask<String, String, String>{

		DatePicker start_date;
		Spinner duration;
		Spinner user_stories;

		public PostSprint(DatePicker start_date, Spinner duration, Spinner user_stories){
			this.start_date = start_date;
			this.duration = duration;
			this.user_stories = user_stories;

		}


		protected String doInBackground(String... url){

			String result = " ";
			String line;
			BufferedReader rd;
			HttpURLConnection conn;
			
			String string_date = String.valueOf(start_date.getYear()) + String.valueOf(start_date.getMonth()) + String.valueOf(start_date.getDayOfMonth());		
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date created_at = new Date();
			
			try {
				URL url_init;
				url_init = new URL(url[0]);



				conn = (HttpURLConnection) url_init.openConnection();
				conn.setRequestMethod("POST");

				conn.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
				wr.writeBytes("sprint={\"start_date\":\"" + string_date + "\",\"duration\":\"" + duration.getSelectedItem().toString() 
						+ "\",\"project_id\":\"" + project_id + "\",\"created_at\":\"" + dateFormat.format(created_at) + "\",\"updated_at\":\"" + dateFormat.format(created_at) + "\"}");
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
