package ped.myscrum;

import info.androidhive.slidingmenu.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
        Spinner duration = (Spinner) rootView.findViewById(R.id.duration);
        Spinner user_stories= (Spinner) rootView.findViewById(R.id.user_stories);
        
        for(int i=1; i<8; i++)
        	duration_list.add(String.valueOf(i));
        
        ArrayAdapter<String> dataAdapter_duration = new ArrayAdapter<String>(this.getActivity(),
        		android.R.layout.simple_spinner_item, duration_list);
        
        ArrayAdapter<String> dataAdapter_user_stories = new ArrayAdapter<String>(this.getActivity(),
        		android.R.layout.simple_spinner_item, user_stories_list);
    	
        dataAdapter_duration.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	duration.setAdapter(dataAdapter_duration);
	
    	new BacklogInformationRetrieval(user_stories_list, user_stories, dataAdapter_user_stories).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/user_stories?api_key=" + api_key);
        
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


}
