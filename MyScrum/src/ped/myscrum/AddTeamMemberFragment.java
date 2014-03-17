package ped.myscrum;

import info.androidhive.slidingmenu.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import ped.myscrum.adapter.ExpandableListAdapter;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.Spinner;

public class AddTeamMemberFragment extends Fragment{
	
	private CharSequence api_key;
	private int project_id;
	private ExpandableListAdapter listAdapter;
	private ExpandableListView expListView;
	private List<String> listDataHeader;
	private HashMap<String, List<String>> listDataChild;
	
	public AddTeamMemberFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_add_user, container, false);

        api_key = getArguments().getString("api_key");
		project_id = getArguments().getInt("project_id");

		expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
		
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		
    	new UsersRetrieval(listDataHeader, listDataChild, expListView).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/users/add?api_key=" + api_key);
    	
    	expListView.setOnGroupClickListener(new OnGroupClickListener() {

    		@Override
    		public boolean onGroupClick(ExpandableListView parent, View v,
    				int groupPosition, long id) {
    			if(groupPosition == listDataHeader.size()-1){
    				getFragmentManager().popBackStackImmediate();
    			}

    			return false;
    		}
    	});

    	return rootView;
	}

    	
    	
private class UsersRetrieval extends AsyncTask<String, String, String>{
		

	private List<String> listDataHeader;
	private HashMap<String, List<String>> listDataChild;
	private ExpandableListAdapter listAdapter;
	private ExpandableListView expListView;

		
		public UsersRetrieval(List<String> listHeader, HashMap<String, List<String>> listChild, ExpandableListView listView){
			listDataHeader = listHeader;
			listDataChild = listChild;
			expListView = listView;

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
			
			for(int i=0; i<data.length(); i++){
				listDataHeader.add(data.getJSONObject(i).getString("username"));
			}
			listDataHeader.add("Back to Team");
			

			} catch (JSONException e) {
				e.printStackTrace();
			}


			listAdapter = new ExpandableListAdapter(AddTeamMemberFragment.this, listDataHeader, listDataChild);
			this.expListView.setAdapter(listAdapter);
			
			
		}
}

}
