package ped.myscrum.creation;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ped.myscrum.gen.R;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class CreateProjectFragment extends Fragment{
	
	private CharSequence api_key;

	public CreateProjectFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
		api_key = getArguments().getString("api_key");
        View rootView = inflater.inflate(R.layout.fragment_create_project, container, false);
        
        Button create_project = (Button) rootView.findViewById(R.id.create_project);
        
        final EditText project_name = (EditText) rootView.findViewById(R.id.edit_project_name);
        final EditText project_repo = (EditText) rootView.findViewById(R.id.edit_project_repo);
        final EditText project_description = (EditText) rootView.findViewById(R.id.description);
        final CheckBox public_box = (CheckBox) rootView.findViewById(R.id.public_);
        
        create_project.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				new PostProject(project_name, project_repo, project_description, public_box).execute("http://10.0.2.2:3000/api/owner/projects/create?api_key=" + api_key);
			}
			
		});

        
        return rootView;
    }
	
private class PostProject extends AsyncTask<String, String, String>{
		
	 private EditText project_name;
     private EditText project_repo;
     private EditText project_description;
     private CheckBox public_box;
		
		public PostProject(EditText name, EditText repo, EditText description, CheckBox box ){
			project_name = name;
			project_repo = repo;
			project_description = description;
			public_box = box;
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
				wr.writeBytes("project={\"title\":\"" + project_name.getText() + "\",\"repo\":\"" + project_repo.getText() + "\",\"description\":\"" + project_description.getText() 
						+ "\",\"public\":\"" + public_box.isChecked() + "\"}");
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
