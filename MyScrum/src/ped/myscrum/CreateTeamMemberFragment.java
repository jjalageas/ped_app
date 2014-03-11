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
import android.widget.Button;
import android.widget.Spinner;

public class CreateTeamMemberFragment extends Fragment{
	
	private CharSequence api_key;
	private int project_id;
	
	public CreateTeamMemberFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_create_user, container, false);

        api_key = getArguments().getString("api_key");
		project_id = getArguments().getInt("project_id");
    	Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner1);
    	List<String> list = new ArrayList<String>();
    	Button add_user = (Button) rootView.findViewById(R.id.add_user);
    	
    	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_spinner_item, list);
    	new UsersRetrieval(list, spinner, dataAdapter).execute("http://10.0.2.2:3000/api/owner/projects/3/users/add?api_key=" + api_key);
    	

    	return rootView;
	}
    	
    	
    	
private class UsersRetrieval extends AsyncTask<String, String, String>{
		
		private List<String> listData;
		private Spinner spinner;
		private ArrayAdapter<String> dataAdapter;


		
		public UsersRetrieval(List<String> list, Spinner s, ArrayAdapter<String> adapter){
			listData = list;
			spinner = s;
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

			try{
				JSONArray data;
				data = new JSONArray(result);

				for(int i=0; i<data.length(); i++)
					listData.add(data.getJSONObject(i).getString("username"));

				dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(dataAdapter);

			}catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}

}
