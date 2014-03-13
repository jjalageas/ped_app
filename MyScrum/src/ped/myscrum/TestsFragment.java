package ped.myscrum;

import info.androidhive.slidingmenu.R;

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
import ped.myscrum.serialization.TestContent;
import ped.myscrum.serialization.Tests;
import android.app.Fragment;
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

public class TestsFragment extends Fragment {

	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;
	private CharSequence api_key;
	private int project_id;
	Tests tests;

	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_tests, container, false);

		expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
		api_key = getArguments().getString("api_key");
		project_id = getArguments().getInt("project_id");
		
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		
		if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
			new TestsInformationRetrieval(listDataHeader, listDataChild, expListView).execute("http://10.0.2.2:3000/api/owner/projects/3/tests?api_key=" + api_key);
		}
		
		else{
			System.out.println("/////////////////////////////////////////////////////");
			try
			{
				tests = load_data(new File(this.getActivity().getFilesDir() + "tests_" + project_id + ".bin"));
				System.out.println(tests.getTests().get(0).getId());
				int ctr = 0;
				
				listDataHeader = new ArrayList<String>();
				listDataChild = new HashMap<String, List<String>>();
				
				for(TestContent t: tests.getTests()){
					
					List<String> project = new ArrayList<String>();
					
					listDataHeader.add("Test #" + t.getId());
					project.add(t.getTitle());
					project.add(t.getUserStoryId());
					project.add(t.getState());
					listDataChild.put(listDataHeader.get(ctr), project);
					
					System.out.println(tests.getTests().get(ctr).getId());
					ctr++;
				}
				listDataHeader.add("Back to Projects");
				
				listAdapter = new ExpandableListAdapter(TestsFragment.this, listDataHeader, listDataChild);
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
					default:
						break;
				}
				return false;
			}
		});
		return rootView;
	}

	public void save_data() throws FileNotFoundException, IOException{
		File save = new File(this.getActivity().getFilesDir() + "tests_" + project_id + ".bin");
		save.createNewFile();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(save));
		oos.writeObject(tests);
		oos.flush();
		oos.close();
	}

	@SuppressWarnings("resource")
	public Tests load_data(File f) throws StreamCorruptedException, FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
		tests = (Tests) ois.readObject();
		return tests;
	}

	private class TestsInformationRetrieval extends AsyncTask<String, String, String>{
		
		private List<String> listDataHeader;
		private HashMap<String, List<String>> listDataChild;
		ExpandableListAdapter listAdapter;
		ExpandableListView expListView;


		
		public TestsInformationRetrieval(List<String> listHeader, HashMap<String, List<String>> listChild, ExpandableListView listView){
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
			tests = new Tests();
	      
			try{
			JSONArray data;
			data = new JSONArray(result);
			
			for(int i=0; i<data.length(); i++){
				listDataHeader.add("Test #" + data.getJSONObject(i).getString("id"));
				tests.getTests().add(new TestContent((String) data.getJSONObject(i).getString("id")));
			}
			listDataHeader.add("Back to Projects");
			
			for(int i=0; i< listDataHeader.size()-1; i++){
				
				List<String> project = new ArrayList<String>();
				project.add(data.getJSONObject(i).getString("title"));
				project.add("User Story: " + data.getJSONObject(i).getString("user_story_id"));
				project.add(data.getJSONObject(i).getString("state"));
				listDataChild.put(listDataHeader.get(i), project);
				
				tests.getTests().get(i).setTitle(data.getJSONObject(i).getString("title"));
				tests.getTests().get(i).setUserStoryId(data.getJSONObject(i).getString("user_story_id"));
				tests.getTests().get(i).setState(data.getJSONObject(i).getString("state"));
				
				
			}
			} catch (JSONException e) {
				e.printStackTrace();
			}


			listAdapter = new ExpandableListAdapter(TestsFragment.this, listDataHeader, listDataChild);
			this.expListView.setAdapter(listAdapter);
			
			try {
				save_data();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
	    }
		
		
		

	}
}

	

