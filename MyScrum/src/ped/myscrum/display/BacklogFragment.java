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
import ped.myscrum.creation.CreateUserStoryFragment;
import ped.myscrum.edition.EditUserStoryFragment;
import ped.myscrum.gen.R;
import ped.myscrum.serialization.Backlog;
import ped.myscrum.serialization.BacklogContent;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
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

public class BacklogFragment extends Fragment {

	private ExpandableListAdapter listAdapter;
	private ExpandableListView expListView;
	private List<String> listDataHeader;
	private List<String> user_story_ids;
	private HashMap<String, List<String>> listDataChild;
	private CharSequence api_key;
	private String project_id;
	private Backlog backlog;

	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_backlog, container, false);

		expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
		api_key = getArguments().getString("api_key");
		project_id = getArguments().getString("project_id");
		
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		

		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		
		if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
			new BacklogInformationRetrieval(listDataHeader, listDataChild, expListView).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/user_stories?api_key=" + api_key);
		}
		else{
			try
			{
				backlog = load_data(new File(this.getActivity().getFilesDir() + "backlog_" + project_id +".bin"));
				int ctr = 0;
				
				listDataHeader = new ArrayList<String>();
				listDataChild = new HashMap<String, List<String>>();
				
				for(BacklogContent t: backlog.getBacklog()){
					
					List<String> project = new ArrayList<String>();
					
					listDataHeader.add(t.getId());
					project.add(t.getTitle());
					project.add(t.getDescription());
					project.add(t.getPriority());
					project.add(t.getDifficulty());
					project.add(t.getFinished());
					project.add(t.getValidated());
					project.add("Edit");
					listDataChild.put(listDataHeader.get(ctr), project);
					
					ctr++;
				}
				listDataHeader.add("Create New User Story");
				listDataHeader.add("Back to Projects");
				
				listAdapter = new ExpandableListAdapter(BacklogFragment.this, listDataHeader, listDataChild);
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
				else
					if(groupPosition == listDataHeader.size()-2){
						Fragment fragment = new CreateUserStoryFragment();
						Bundle args = new Bundle();
						args.putCharSequence("api_key", api_key);
						args.putInt("project_id", Integer.valueOf(project_id));
					    fragment.setArguments(args);
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
					case 6:
						fragment = new EditUserStoryFragment();
						Bundle backlog_args = new Bundle();
						backlog_args.putCharSequence("api_key", api_key);
						backlog_args.putString("project_id", project_id);
						backlog_args.putString("user_story_id", user_story_ids.get(groupPosition));
					    fragment.setArguments(backlog_args);
						break;
					default:
						break;
				}
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment).addToBackStack(String.valueOf(groupPosition)).commit();
				return false;
			}
		});
		return rootView;
	}

	public void save_data() throws FileNotFoundException, IOException{
		File save = new File(this.getActivity().getFilesDir() + "backlog_" + project_id +".bin");
		save.createNewFile();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(save));
		oos.writeObject(backlog);
		oos.flush();
		oos.close();
	}

	@SuppressWarnings("resource")
	public Backlog load_data(File f) throws StreamCorruptedException, FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
		backlog = (Backlog) ois.readObject();
		return backlog;
	}

	private class BacklogInformationRetrieval extends AsyncTask<String, String, String>{
		
		private List<String> listDataHeader;
		private HashMap<String, List<String>> listDataChild;
		private ExpandableListAdapter listAdapter;
		private ExpandableListView expListView;


		
		public BacklogInformationRetrieval(List<String> listHeader, HashMap<String, List<String>> listChild, ExpandableListView listView){
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
			backlog = new Backlog();
			user_story_ids = new ArrayList<String>();
	      
			try{
			JSONArray data;
			data = new JSONArray(result);
			
			for(int i=0; i<data.length(); i++){
				listDataHeader.add("User Story " + (i+1));
				backlog.getBacklog().add(new BacklogContent("User Story " + (i+1)));
				user_story_ids.add(data.getJSONObject(i).getString("id"));
			}
			listDataHeader.add("Create New User Story");
			listDataHeader.add("Back to Projects");
			
			for(int i=0; i< listDataHeader.size()-2; i++){
				
				List<String> project = new ArrayList<String>();
				
				project.add(data.getJSONObject(i).getString("title"));
				project.add(data.getJSONObject(i).getString("description"));
				project.add("Priority: " + data.getJSONObject(i).getString("priority"));
				project.add("Difficulty: " + data.getJSONObject(i).getString("difficulty"));
				
				if(data.getJSONObject(i).getString("finished").equals("null"))
					project.add("Finished: " + "No");
				else
					project.add("Finished: " + "Yes");
				
				if(data.getJSONObject(i).getString("valid").equals("null"))
					project.add("Validated: " + "No");
				else
					project.add("Validated: " + "Yes");
				project.add("Edit");
				listDataChild.put(listDataHeader.get(i), project);
				
				
				backlog.getBacklog().get(i).setTitle(data.getJSONObject(i).getString("title"));
				backlog.getBacklog().get(i).setDescription(data.getJSONObject(i).getString("description"));
				backlog.getBacklog().get(i).setPriority("Priority: " + data.getJSONObject(i).getString("priority"));
				backlog.getBacklog().get(i).setDifficulty("Difficulty: " + data.getJSONObject(i).getString("difficulty"));
				
				if(data.getJSONObject(i).getString("finished").equals("null"))
					backlog.getBacklog().get(i).setFinished("Finished: No");
				else
					backlog.getBacklog().get(i).setFinished("Finished: Yes");
				if(data.getJSONObject(i).getString("valid").equals("null"))
					backlog.getBacklog().get(i).setValidated("Validated: No");
				else
					backlog.getBacklog().get(i).setValidated("Validated: Yes");

			}
			} catch (JSONException e) {
				e.printStackTrace();
			}


			listAdapter = new ExpandableListAdapter(BacklogFragment.this, listDataHeader, listDataChild) {
				@Override
				public View getGroupView(int position, boolean b, View convertView, android.view.ViewGroup parent) {
					View result = super.getGroupView(position, false, convertView, parent);
					if(b == false){
						for(int i=0; i<listDataHeader.size(); i++)
							if(position == i){
								result.setBackgroundColor(Color.DKGRAY);
							} 
							else {
								if(position == (listDataHeader.size()-1) || position == (listDataHeader.size()-2))
										result.setBackgroundColor(Color.BLACK);
								else{
									if(position == i)
										result.setBackgroundColor(Color.DKGRAY);
								}
							}

					}
					if(position == (listDataHeader.size()-1) || position == (listDataHeader.size()-2))
						result.setBackgroundColor(Color.BLACK);
					return result;
				}
			};
			
			this.expListView.setAdapter(listAdapter);
			
			try {
				save_data();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
	    }
		
		
		

	}
}

	

