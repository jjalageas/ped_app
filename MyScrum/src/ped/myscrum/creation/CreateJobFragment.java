package ped.myscrum.creation;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ped.myscrum.gen.R;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class CreateJobFragment extends Fragment{

	private CharSequence api_key;
	private String project_id;
	private String sprint_id;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_edit_job, container, false);
		
		api_key = getArguments().getString("api_key");
		project_id = getArguments().getString("project_id");
		sprint_id = getArguments().getString("sprint_id");
		
		List<String> status_list = new ArrayList<String>();
		status_list.add("To Do");
		status_list.add("In Progress");
		status_list.add("Done");
		
		final EditText title = (EditText) rootView.findViewById(R.id.edit_title);	
		final EditText description = (EditText) rootView.findViewById(R.id.edit_desc);
		final Spinner status = (Spinner) rootView.findViewById(R.id.spinner_status);
		final EditText difficulty = (EditText) rootView.findViewById(R.id.edit_difficulty);
		
		Button submit = (Button) rootView.findViewById(R.id.submit);
		Button back = (Button) rootView.findViewById(R.id.back);
		
		back.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStackImmediate();
			}

		});
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_spinner_item, status_list);
		
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		status.setAdapter(dataAdapter);
		
		submit.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				new PostJob(title, description, status, difficulty).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/sprints/" + sprint_id +"/create_job?api_key=" + api_key);
			}

		});
		
		return rootView;
		
	}
	
	private class PostJob extends AsyncTask<String, String, String>{

		EditText title;	
		final EditText description;
		final Spinner status;
		final EditText difficulty;

		public PostJob(EditText title, EditText description, Spinner status, EditText difficulty){
			this.title = title;
			this.description = description;
			this.status = status;
			this.difficulty = difficulty;

		}


		protected String doInBackground(String... url){

			String result = " ";
			String line;
			BufferedReader rd;
			HttpURLConnection conn;
			
			
			
			try {
				URL url_init;
				url_init = new URL(url[0]);



				conn = (HttpURLConnection) url_init.openConnection();
				conn.setRequestMethod("POST");

				conn.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
				wr.writeBytes("job={\"title\":\"" + title.getText() + "\",\"description\":\"" + description.getText() 
						+ "\",\"status\":\"" + status.getSelectedItem() + "\",\"difficulty\":\"" + difficulty.getText() + "\"}");
				wr.flush();
				wr.close();


				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while ((line = rd.readLine()) != null) {
					result += line;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		protected void onPostExecute(String result) {
		}
		
	}

	
}
