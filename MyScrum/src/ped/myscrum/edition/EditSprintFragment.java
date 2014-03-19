package ped.myscrum.edition;

import info.androidhive.slidingmenu.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

public class EditSprintFragment extends Fragment{
	
	private CharSequence api_key;
	private String project_id;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_create_sprint, container, false);
		
		api_key = getArguments().getString("api_key");
		project_id = getArguments().getString("project_id");
		
		final DatePicker date = (DatePicker) rootView.findViewById(R.id.start_date);	
		final Spinner duration = (Spinner) rootView.findViewById(R.id.duration);
		final Spinner user_stories = (Spinner) rootView.findViewById(R.id.user_stories);

		
		Button submit = (Button) rootView.findViewById(R.id.submit);
		Button back = (Button) rootView.findViewById(R.id.back);
		
		back.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStackImmediate();
			}

		});

		new SprintInformationRetrieval(date, duration, user_stories).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/project?api_key=" + api_key);
		
		submit.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				new PostSprint(date, duration, user_stories).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/edit?api_key=" + api_key);
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
				wr.writeBytes("sprint={\"start_date\":\"" + string_date + "\",\"duration\":\"" + duration.getSelectedItem() +  "\",\"updated_at\":\"" + dateFormat.format(updated_at) + "\"}");
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
		Spinner user_stories;
		
		public SprintInformationRetrieval(DatePicker date, Spinner duration, Spinner user_stories){
			this.date = date;
			this.duration = duration;
			this.user_stories = user_stories;
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
			
			String date_string = (String) parsedData.get("start_date");
			String year = date_string.substring(0,3);
			String month = date_string.substring(5,6);
			String day = date_string.substring(8,9);
			
			date.updateDate(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day));
			duration.setSelection(Integer.valueOf((String) parsedData.get("duration")));
			//user_stories.setText((CharSequence) parsedData.get(""));

		
	    }
		
	}

}
