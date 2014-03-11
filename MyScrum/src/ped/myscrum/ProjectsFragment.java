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

public class ProjectsFragment extends Fragment {

	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;
	private CharSequence api_key;

	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_project, container, false);

		api_key = getArguments().getString("api_key");
		expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
		
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		
		new ProjectsInformationRetrieval(listDataHeader, listDataChild, expListView).execute("http://10.0.2.2:3000/api/owner/projects?api_key=" + api_key);
		
		
		expListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				if(listDataHeader.get(groupPosition).equals("Create New Project")){
					Fragment fragment = null;
					fragment = new CreateProjectFragment();
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();
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
					fragment = new DescriptionFragment();
					Bundle desc_args = new Bundle();
					desc_args.putCharSequence("api_key", api_key);
					desc_args.putInt("project_id", groupPosition + 1);
				    fragment.setArguments(desc_args);
					break;
				case 1:
					fragment = new TeamFragment();
					Bundle team_args = new Bundle();
					team_args.putCharSequence("api_key", api_key);
					team_args.putInt("project_id", groupPosition + 1);
				    fragment.setArguments(team_args);
					break;
				case 2:
					fragment = new BacklogFragment();
					Bundle backlog_args = new Bundle();
					backlog_args.putCharSequence("api_key", api_key);
					backlog_args.putInt("project_id", groupPosition + 1);
				    fragment.setArguments(backlog_args);
					break;
				case 3:
					fragment = new SprintsFragment();
					break;
				case 4:
					fragment = new TestsFragment();
					Bundle tests_args = new Bundle();
					tests_args.putCharSequence("api_key", api_key);
					tests_args.putInt("project_id", groupPosition + 1);
				    fragment.setArguments(tests_args);
					break;			
				default:
					break;
				}

				if (fragment != null) {
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
				} else {
					Log.e("MainActivity", "Error in creating fragment");
				}
				
				return false;
			}
		});
		return rootView;
	}

	
	private class ProjectsInformationRetrieval extends AsyncTask<String, String, String>{
		
		private List<String> listDataHeader;
		private HashMap<String, List<String>> listDataChild;
		ExpandableListAdapter listAdapter;
		ExpandableListView expListView;


		
		public ProjectsInformationRetrieval(List<String> listHeader, HashMap<String, List<String>> listChild, ExpandableListView listView){
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
			
			for(int i=0; i<data.length(); i++)
				listDataHeader.add(data.getJSONObject(i).getString("title"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			for(int i=0; i< listDataHeader.size(); i++){
				List<String> project = new ArrayList<String>();
				project.add("Description");
				project.add("Team");
				project.add("Backlog");
				project.add("Sprints");
				project.add("Tests");
				listDataChild.put(listDataHeader.get(i), project);
			}
			
			listAdapter = new ExpandableListAdapter(ProjectsFragment.this, listDataHeader, listDataChild);
			this.expListView.setAdapter(listAdapter);
			
	    }
		
		
		

	}
	
	
	
	
	
}






