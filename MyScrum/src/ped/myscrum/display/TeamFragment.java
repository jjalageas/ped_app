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
import ped.myscrum.serialization.Team;
import ped.myscrum.serialization.TeamMember;
import ped.myscrum.edition.EditTeamFragment;
import ped.myscrum.gen.R;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class TeamFragment extends Fragment {

	private ExpandableListAdapter listAdapter;
	private ExpandableListView expListView;
	private List<String> listDataHeader;
	private HashMap<String, List<String>> listDataChild;
	private CharSequence api_key;
	private String project_id;
	private Team team;
	private List<String> user_ids;
	private List<String> usernames;

	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_team, container, false);

		expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
		api_key = getArguments().getString("api_key");
		project_id = getArguments().getString("project_id");
		
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		
		if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
			new TeamInformationRetrieval(listDataHeader, listDataChild, expListView).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/users?api_key=" + api_key);
		}
		else{
			try
			{
				team = load_data(new File(this.getActivity().getFilesDir() + "team_" + project_id +".bin"));
				int ctr = 0;
				
				listDataHeader = new ArrayList<String>();
				listDataChild = new HashMap<String, List<String>>();
				user_ids = new ArrayList<String>();
				
				for(TeamMember t: team.getTeam()){
					
					List<String> project = new ArrayList<String>();
					
					listDataHeader.add(t.getUsername());
					project.add(t.getEmail());
					project.add(t.getLastname());
					project.add(t.getFirstname());
					listDataChild.put(listDataHeader.get(ctr), project);
					user_ids.add(t.getUsername());
					
					ctr++;
				}
				listDataHeader.add("Add User");
				listDataHeader.add("Back to Projects");
				
				listAdapter = new ExpandableListAdapter(TeamFragment.this, listDataHeader, listDataChild);
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
						fragment = new EditTeamFragment();
						Bundle team_args = new Bundle();
						
						team_args.putCharSequence("api_key", api_key);
						team_args.putInt("project_id", Integer.valueOf(project_id));
					    
						int ctr = 1;
						for(int i=0; i<user_ids.size(); i++){
							team_args.putCharSequence("user" + String.valueOf(i+1), usernames.get(i));
							team_args.putCharSequence("ids" + String.valueOf(i+1), user_ids.get(i));
							++ctr;
						}
						team_args.putInt("nb_users", ctr);
						System.out.println(ctr);
						System.out.println(user_ids.size());
						
						fragment.setArguments(team_args);
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
				switch (childPosition) {
					case 0:
						break;
					case 1:
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
		File save = new File(this.getActivity().getFilesDir() + "team_" + project_id +".bin");
		save.createNewFile();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(save));
		oos.writeObject(team);
		oos.flush();
		oos.close();
	}

	@SuppressWarnings("resource")
	public Team load_data(File f) throws StreamCorruptedException, FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
		team = (Team) ois.readObject();
		return team;
	}
	

	private class TeamInformationRetrieval extends AsyncTask<String, String, String>{
		
		private List<String> listDataHeader;
		private HashMap<String, List<String>> listDataChild;
		private ExpandableListAdapter listAdapter;
		private ExpandableListView expListView;


		
		public TeamInformationRetrieval(List<String> listHeader, HashMap<String, List<String>> listChild, ExpandableListView listView){
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
			team = new Team();
			user_ids = new ArrayList<String>();
			usernames = new ArrayList<String>();
			
			try{
			JSONArray data;
			data = new JSONArray(result);
			
			for(int i=0; i<data.length(); i++){
				listDataHeader.add(data.getJSONObject(i).getString("username"));
				usernames.add(data.getJSONObject(i).getString("username"));
				user_ids.add(data.getJSONObject(i).getString("id"));
				team.getTeam().add(new TeamMember((String) data.getJSONObject(i).getString("username")));
			}
			listDataHeader.add("Add User");
			listDataHeader.add("Back to Projects");
			
			for(int i=0; i< listDataHeader.size()-2; i++){
				
				List<String> project = new ArrayList<String>();
				project.add(data.getJSONObject(i).getString("email"));
				project.add(data.getJSONObject(i).getString("last_name"));
				project.add(data.getJSONObject(i).getString("first_name"));
				listDataChild.put(listDataHeader.get(i), project);
				
				team.getTeam().get(i).setEmail(data.getJSONObject(i).getString("email"));
				team.getTeam().get(i).setFirstname(data.getJSONObject(i).getString("first_name"));
				team.getTeam().get(i).setLastname(data.getJSONObject(i).getString("last_name"));
			}
			} catch (JSONException e) {
				e.printStackTrace();
			}


			listAdapter = new ExpandableListAdapter(TeamFragment.this, listDataHeader, listDataChild) {
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

	

