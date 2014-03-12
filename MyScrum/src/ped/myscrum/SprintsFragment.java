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
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class SprintsFragment extends Fragment {

	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;
	private CharSequence api_key;
	private int project_id;
	private List<Integer> sprint_ids;

	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_sprints, container, false);

		expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
		api_key = getArguments().getString("api_key");
		project_id = getArguments().getInt("project_id");
		
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		sprint_ids = new ArrayList<Integer>();
		
		new SprintsInformationRetrieval(listDataHeader, listDataChild, expListView).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/sprints?api_key=" + api_key);
		
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



		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Fragment fragment = null;
				switch (childPosition) {
					case 0:
						break;
					case 1:
						break;	
					case 2:
						fragment = new SprintsUserStoriesFragment();
						Bundle sprints_args = new Bundle();
						sprints_args.putCharSequence("api_key", api_key);
						sprints_args.putInt("project_id", project_id);
						sprints_args.putInt("sprint_id", sprint_ids.get(groupPosition));
					    fragment.setArguments(sprints_args);
						break;
					case 3:
						fragment = new ChartFragment();
						break;
					default:
						break;
				}
				if (fragment != null) {
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).addToBackStack(String.valueOf(childPosition)).commit();
				} else {
					Log.e("MainActivity", "Error in creating fragment");
				}
			
				return false;
			}
		});
		return rootView;
	}

	

	private class SprintsInformationRetrieval extends AsyncTask<String, String, String>{
		
		private List<String> listDataHeader;
		private HashMap<String, List<String>> listDataChild;
		ExpandableListAdapter listAdapter;
		ExpandableListView expListView;


		
		public SprintsInformationRetrieval(List<String> listHeader, HashMap<String, List<String>> listChild, ExpandableListView listView){
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
				listDataHeader.add("Sprint #" + (i+1));
				sprint_ids.add(Integer.valueOf(data.getJSONObject(i).getString("id").toString()));
			}
			listDataHeader.add("Back to Projects");
		
			
			for(int i=0; i< listDataHeader.size()-1; i++){
				List<String> project = new ArrayList<String>();
				project.add("Start Date: " + data.getJSONObject(i).getString("start_date"));
				project.add("Duration: " + data.getJSONObject(i).getString("duration"));
				project.add("User Stories");
				project.add("Charts");
				listDataChild.put(listDataHeader.get(i), project);
			}
			} catch (JSONException e) {
				e.printStackTrace();
			}


			listAdapter = new ExpandableListAdapter(SprintsFragment.this, listDataHeader, listDataChild);
			this.expListView.setAdapter(listAdapter);
			
	    }
		
		
		

	}
}

	