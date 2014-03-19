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
import ped.myscrum.gen.R;
import ped.myscrum.serialization.Job;
import ped.myscrum.serialization.JobContent;
import android.app.Fragment;
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

public class JobsFragment extends Fragment {

	private ExpandableListAdapter listAdapter;
	private ExpandableListView expListView;
	private List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;
	private CharSequence api_key;
	private int project_id;
	private int sprint_id;
	private Job jobs;

	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_sprints, container, false);

		expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
		api_key = getArguments().getString("api_key");
		project_id = getArguments().getInt("project_id");
		sprint_id = getArguments().getInt("sprint_id");
		
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		
		if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
			new JobsRetrieval(listDataHeader, listDataChild, expListView).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/sprints/" + sprint_id + "/jobs?api_key=" + api_key);
		}
		else{
			try
			{
				jobs = load_data(new File(this.getActivity().getFilesDir() + "jobs_" + project_id + "_" + sprint_id + ".bin"));
				int ctr = 0;
				
				listDataHeader = new ArrayList<String>();
				listDataChild = new HashMap<String, List<String>>();
				
				for(JobContent t: jobs.getJobs()){
					
					List<String> project = new ArrayList<String>();
					
					listDataHeader.add(t.getId());
					project.add(t.getTitle());
					project.add(t.getDescription());
					project.add(t.getDifficulty());
					project.add(t.getFinished());
					listDataChild.put(listDataHeader.get(ctr), project);
					
					ctr++;
				}
				listDataHeader.add("Back to Projects");
				
				listAdapter = new ExpandableListAdapter(JobsFragment.this, listDataHeader, listDataChild);
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
				switch (childPosition) {
					case 0:
						break;
					case 1:
						break;	
					case 2:
						break;
					case 3:
						break;
					default:
						break;
				}
				return false;
			}
		});
		return rootView;
	}

	public void save_data() throws FileNotFoundException, IOException{
		File save = new File(this.getActivity().getFilesDir() + "jobs_" + project_id + "_" + sprint_id + ".bin");
		save.createNewFile();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(save));
		oos.writeObject(jobs);
		oos.flush();
		oos.close();
	}

	@SuppressWarnings("resource")
	public Job load_data(File f) throws StreamCorruptedException, FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
		jobs = (Job) ois.readObject();
		return jobs;
	}
	

	private class JobsRetrieval extends AsyncTask<String, String, String>{
		
		private List<String> listDataHeader;
		private HashMap<String, List<String>> listDataChild;
		private ExpandableListAdapter listAdapter;
		private ExpandableListView expListView;


		
		public JobsRetrieval(List<String> listHeader, HashMap<String, List<String>> listChild, ExpandableListView listView){
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
			jobs = new Job();
	      
			try{
			JSONArray data;
			data = new JSONArray(result);
			
			for(int i=0; i<data.length(); i++){
				listDataHeader.add("Job #" + data.getJSONObject(i).getString("id"));
				jobs.getJobs().add(new JobContent("Job #" + data.getJSONObject(i).getString("id")));
			}
			listDataHeader.add("Back to Sprints");
		
			
			for(int i=0; i< listDataHeader.size()-1; i++){
				List<String> project = new ArrayList<String>();
				project.add(data.getJSONObject(i).getString("title"));
				project.add(data.getJSONObject(i).getString("description"));
				project.add("Difficulty: " + data.getJSONObject(i).getString("difficulty"));
				if(data.getJSONObject(i).getString("status").equals("todo"))
					project.add("Status: To Do");
				else 
					if(data.getJSONObject(i).getString("status").equals("done"))
						project.add("Status: Done");
					else 
						project.add("Status: In Progress");
				listDataChild.put(listDataHeader.get(i), project);
				
				
				jobs.getJobs().get(i).setTitle(data.getJSONObject(i).getString("title"));
				jobs.getJobs().get(i).setDescription(data.getJSONObject(i).getString("description"));
				jobs.getJobs().get(i).setDifficulty("Difficulty: " + data.getJSONObject(i).getString("difficulty"));
				
				if(data.getJSONObject(i).getString("status").equals("todo"))
					jobs.getJobs().get(i).setFinished("Status: To Do");
				else 
					if(data.getJSONObject(i).getString("status").equals("done"))
						jobs.getJobs().get(i).setFinished("Status: Done");
					else 
						project.add("Status: In Progress");
				
			}
			} catch (JSONException e) {
				e.printStackTrace();
			}


			listAdapter = new ExpandableListAdapter(JobsFragment.this, listDataHeader, listDataChild);
			this.expListView.setAdapter(listAdapter);
			
			try {
				save_data();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
	    }
		
		
		

	}
}

	