package ped.myscrum.display;

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

import ped.myscrum.serialization.ProjectDescription;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class DescriptionFragment extends Fragment{

	private CharSequence api_key;
	private String project_id;
	private Button back;
	private ProjectDescription pd;
	
	public DescriptionFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {

		api_key = getArguments().getString("api_key");
		project_id = getArguments().getString("project_id");

		View rootView = inflater.inflate(R.layout.fragment_project_description, container, false);
	/*	back = (Button) rootView.findViewById(R.id.back);  

		back.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStackImmediate();
			}
		});*/

		WebView description = (WebView) rootView.findViewById(R.id.description);
		
		
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		
		if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
			DescriptionRetrieval descrition_data = new DescriptionRetrieval(description);
			descrition_data.execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/description?api_key=" + api_key);
		}
		
		else{
			try
			{
				pd = load_data(new File(this.getActivity().getFilesDir() + "description" + project_id + ".bin"));
				String result = pd.getDescritpion();
				description.loadData(result, "text/html", "utf-8");
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}

		}
	

		return rootView;
	}
	
	public void save_data() throws FileNotFoundException, IOException{
		File save = new File(this.getActivity().getFilesDir() + "description" + project_id + ".bin");
		save.createNewFile();
		System.out.println(save.getAbsolutePath());
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(save));
		oos.writeObject(pd);
		oos.flush();
		oos.close();
	}

	@SuppressWarnings("resource")
	public ProjectDescription load_data(File f) throws StreamCorruptedException, FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
		pd = (ProjectDescription) ois.readObject();
		return pd;
	}
	
	private class DescriptionRetrieval extends AsyncTask<String, String, String>{

		private WebView description;
		
		public DescriptionRetrieval(WebView description2){
			description = description2;
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
	        result = result.substring(2, result.length()-1);
	        String text = "<html><body>" + "<p align=\"justify\">" + result + "</p> " + "</body></html>";
	        result = text;
			description.loadData(result, "text/html", "utf-8");
			pd = new ProjectDescription(result);
			try {
				save_data();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
		
	}


}
