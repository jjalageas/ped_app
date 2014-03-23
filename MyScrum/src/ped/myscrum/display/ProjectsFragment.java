package ped.myscrum.display;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import ped.myscrum.adapter.ExpandableListAdapter;
import ped.myscrum.creation.CreateProjectFragment;
import ped.myscrum.edition.EditProjectFragment;
import ped.myscrum.gen.R;
import ped.myscrum.serialization.Project;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

	private ExpandableListAdapter listAdapter;
	private ExpandableListView expListView;
	private List<String> listDataHeader;
	private HashMap<String, List<String>> listDataChild;
	private CharSequence api_key;
	private Project projects;
	private List<String> project_ids;

	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_project, container, false);

		api_key = getArguments().getString("api_key");
		expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
		
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		project_ids = new ArrayList<String>();
		

		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		
		if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
			new ProjectsInformationRetrieval(listDataHeader, listDataChild, expListView).execute("http://10.0.2.2:3000/api/" +
					"owner/projects?api_key=" + api_key);
		}
		else{
			try
			{
				projects = load_data(new File(this.getActivity().getFilesDir() + "projects.bin"));
				project_ids = projects.getIds();
				int ctr = 0;
				
				for(int i=0; i<projects.getProjects().size(); i++){
					
					List<String> project = new ArrayList<String>();
					
					listDataHeader.add(projects.getProjects().get(i));
					project.add("Description");
					project.add("Team");
					project.add("Backlog");
					project.add("Sprints");
					project.add("Tests");
					project.add("Edit");
					listDataChild.put(listDataHeader.get(ctr), project);
					
					ctr++;
				}

				
				listAdapter = new ExpandableListAdapter(ProjectsFragment.this, listDataHeader, listDataChild);
				this.expListView.setAdapter(listAdapter);
					

			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		
		expListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				if(listDataHeader.get(groupPosition).equals("Create New Project")){
					Fragment fragment = null;
					fragment = new CreateProjectFragment();
					Bundle pro_args = new Bundle();
					pro_args.putCharSequence("api_key", api_key);
				    fragment.setArguments(pro_args);
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
					desc_args.putString("project_id", project_ids.get(groupPosition));
				    fragment.setArguments(desc_args);
					break;
				case 1:
					fragment = new TeamFragment();
					Bundle team_args = new Bundle();
					team_args.putCharSequence("api_key", api_key);
					team_args.putString("project_id", project_ids.get(groupPosition));
				    fragment.setArguments(team_args);
					break;
				case 2:
					fragment = new BacklogFragment();
					Bundle backlog_args = new Bundle();
					backlog_args.putCharSequence("api_key", api_key);
					backlog_args.putString("project_id", project_ids.get(groupPosition));
				    fragment.setArguments(backlog_args);
					break;
				case 3:
					fragment = new SprintsFragment();
					Bundle sprints_args = new Bundle();
					sprints_args.putCharSequence("api_key", api_key);
					sprints_args.putString("project_id", project_ids.get(groupPosition));
				    fragment.setArguments(sprints_args);
					break;
				case 4:
					fragment = new TestsFragment();
					Bundle tests_args = new Bundle();
					tests_args.putCharSequence("api_key", api_key);
					tests_args.putString("project_id", project_ids.get(groupPosition));
				    fragment.setArguments(tests_args);
					break;			
				case 5:
					fragment = new EditProjectFragment();
					Bundle edit_args = new Bundle();
					edit_args.putCharSequence("api_key", api_key);
					edit_args.putString("project_id", project_ids.get(groupPosition));
				    fragment.setArguments(edit_args);
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

	
	public void save_data(){
		File save = new File(this.getActivity().getFilesDir() + "projects.bin");
		try {
			save.createNewFile();
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(save));
			oos.writeObject(projects);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	public Project load_data(File f){
		try {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
		projects = (Project) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return projects;
	}
	
	private class ProjectsInformationRetrieval extends AsyncTask<String, String, String>{
		
		private List<String> listDataHeader;
		private HashMap<String, List<String>> listDataChild;
		private ExpandableListAdapter listAdapter;
		private ExpandableListView expListView;


		
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
			projects = new Project();
	      
			try{
			JSONArray data;
			data = new JSONArray(result);
			
			for(int i=0; i<data.length(); i++){
				listDataHeader.add(data.getJSONObject(i).getString("title"));
				projects.getProjects().add(data.getJSONObject(i).getString("title"));
				project_ids.add(data.getJSONObject(i).getString("id"));
			}
			listDataHeader.add("Create New Project");
			projects.setIds(project_ids);

			for(int i=0; i< listDataHeader.size()-1; i++){
				List<String> project = new ArrayList<String>();
				project.add("Description");
				project.add("Team");
				project.add("Backlog");
				project.add("Sprints");
				project.add("Tests");
				project.add("Edit");
				listDataChild.put(listDataHeader.get(i), project);
				
				
			}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			listAdapter = new ExpandableListAdapter(ProjectsFragment.this, listDataHeader, listDataChild);
			this.expListView.setAdapter(listAdapter);
			
			try {
				save_data();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
	    }
		
		
		

	}
	
	
	
	
	
}






