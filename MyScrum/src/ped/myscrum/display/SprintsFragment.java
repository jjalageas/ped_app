package ped.myscrum.display;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import ped.myscrum.adapter.ExpandableListAdapter;
import ped.myscrum.serialization.SprintContent;
import ped.myscrum.serialization.Sprints;
import ped.myscrum.creation.CreateSprintFragment;
import ped.myscrum.edition.EditJobFragment;
import ped.myscrum.gen.R;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class SprintsFragment extends Fragment {

	private ExpandableListAdapter listAdapter;
	private ExpandableListView expListView;
	private List<String> listDataHeader;
	private HashMap<String, List<String>> listDataChild;
	private CharSequence api_key;
	private String project_id;
	private List<Integer> sprint_ids;
	private Sprints sprints;

	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View rootView = inflater.inflate(R.layout.fragment_sprints, container, false);

		expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
		api_key = getArguments().getString("api_key");
		project_id = getArguments().getString("project_id");
		
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		sprint_ids = new ArrayList<Integer>();
		
	
		
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		
		if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
			new SprintsInformationRetrieval(listDataHeader, listDataChild, expListView).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/sprints?api_key=" + api_key);
		}
		else{
			try
			{
				sprints = load_data(new File(this.getActivity().getFilesDir() + "sprints_" + project_id +".bin"));
				int ctr = 0;
				
				listDataHeader = new ArrayList<String>();
				listDataChild = new HashMap<String, List<String>>();
				sprint_ids = new ArrayList<Integer>();
				
				for(SprintContent t: sprints.getSprints()){
					
					List<String> project = new ArrayList<String>();
					
					listDataHeader.add(t.getId());
					project.add(t.getStartDate());
					project.add(t.getDuration());
					project.add("User Stories");
					project.add("Jobs");
					project.add("Charts");
					project.add("Edit Sprint");
					listDataChild.put(listDataHeader.get(ctr), project);
					
					sprint_ids.add(t.getIdNum());
					
					ctr++;
				}
				listDataHeader.add("Create New Sprint");
				listDataHeader.add("Back to Projects");
				
				listAdapter = new ExpandableListAdapter(SprintsFragment.this, listDataHeader, listDataChild);
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
				Fragment fragment;
				if(groupPosition == listDataHeader.size()-1){
					getFragmentManager().popBackStackImmediate();
				}
				else
					if(groupPosition == listDataHeader.size()-2){
						fragment = new CreateSprintFragment();
						Bundle sprints_args = new Bundle();
						sprints_args.putCharSequence("api_key", api_key);
						sprints_args.putInt("project_id", Integer.valueOf(project_id));
						fragment.setArguments(sprints_args);
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).addToBackStack(String.valueOf(groupPosition)).commit();
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
						sprints_args.putInt("project_id", Integer.valueOf(project_id));
						sprints_args.putInt("sprint_id", sprint_ids.get(groupPosition));
					    fragment.setArguments(sprints_args);
						break;
					case 3:
						fragment = new JobsFragment();
						Bundle jobs_args = new Bundle();
						jobs_args.putCharSequence("api_key", api_key);
						jobs_args.putInt("project_id", Integer.valueOf(project_id));
						jobs_args.putInt("sprint_id", sprint_ids.get(groupPosition));
					    fragment.setArguments(jobs_args);
						break;
					case 4:
						fragment = new ChartFragment();
						break;
					case 5:
						fragment = new EditJobFragment();
						Bundle job_args = new Bundle();
						job_args.putCharSequence("api_key", api_key);
						job_args.putInt("project_id", Integer.valueOf(project_id));
						job_args.putInt("sprint_id", sprint_ids.get(groupPosition));
					    fragment.setArguments(job_args);
						break;
					default:
						break;
				}
				if (fragment != null) {
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).addToBackStack(String.valueOf(childPosition)).commit();
				}
			
				return false;
			}
		});
		return rootView;
	}

	public void save_data() throws FileNotFoundException, IOException{
		File save = new File(this.getActivity().getFilesDir() + "sprints_" + project_id + ".bin");
		save.createNewFile();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(save));
		oos.writeObject(sprints);
		oos.flush();
		oos.close();
	}

	@SuppressWarnings("resource")
	public Sprints load_data(File f) throws StreamCorruptedException, FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
		sprints = (Sprints) ois.readObject();
		return sprints;
	}

	private class SprintsInformationRetrieval extends AsyncTask<String, String, String>{
		
		private List<String> listDataHeader;
		private HashMap<String, List<String>> listDataChild;
		private ExpandableListAdapter listAdapter;
		private ExpandableListView expListView;


		
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
			sprints = new Sprints();
	      
			try{
			JSONArray data;
			data = new JSONArray(result);
			
			for(int i=0; i<data.length(); i++){
				listDataHeader.add("Sprint " + (i+1));
				sprint_ids.add(Integer.valueOf(data.getJSONObject(i).getString("id").toString()));
				sprints.getSprints().add(new SprintContent("Sprint " + String.valueOf(i+1)));
			}
			listDataHeader.add("Create New Sprint");
			listDataHeader.add("Back to Projects");
		
			
			for(int i=0; i< listDataHeader.size()-2; i++){
				List<String> project = new ArrayList<String>();
				project.add("Start Date: " + data.getJSONObject(i).getString("start_date").substring(0, 10));
				project.add("Duration: " + data.getJSONObject(i).getString("duration"));
				project.add("User Stories");
				project.add("Jobs");
				project.add("Charts");
				project.add("Edit Sprint");
				listDataChild.put(listDataHeader.get(i), project);
				
				sprints.getSprints().get(i).setStartDate("Start Date: " + data.getJSONObject(i).getString("start_date").substring(0, 10));
				sprints.getSprints().get(i).setDuration("Duration: " + data.getJSONObject(i).getString("duration"));
				sprints.getSprints().get(i).setIdNum(Integer.valueOf(data.getJSONObject(i).getString("id").toString()));
			}
			} catch (JSONException e) {
				e.printStackTrace();
			}


			listAdapter = new ExpandableListAdapter(SprintsFragment.this, listDataHeader, listDataChild);
			this.expListView.setAdapter(listAdapter);
			
			try {
				save_data();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
	    }
		
		
		

	}
}

	