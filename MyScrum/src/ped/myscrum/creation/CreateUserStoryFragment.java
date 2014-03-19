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
import android.widget.EditText;

public class CreateUserStoryFragment extends Fragment{
	
	private CharSequence api_key;
	private int project_id;

	public CreateUserStoryFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_create_user_story, container, false);
        
        Button submit_user_story = (Button) rootView.findViewById(R.id.submit);
        Button back = (Button) rootView.findViewById(R.id.back);
        
        final EditText title = (EditText) rootView.findViewById(R.id.edit_title);
        final EditText description = (EditText) rootView.findViewById(R.id.edit_description);
		final EditText difficulty = (EditText) rootView.findViewById(R.id.edit_difficulty);
        final EditText priority = (EditText) rootView.findViewById(R.id.edit_priority);
        
        api_key = getArguments().getString("api_key");
        project_id = getArguments().getInt("project_id");
        
        submit_user_story.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				new PostUserStory(title, description, difficulty, priority).execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/user_stories/create?api_key=" + api_key);
				getFragmentManager().popBackStackImmediate();
			}
			
		});
        
        back.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStackImmediate();
			}
			
		});
        
        return rootView;
    }
	
	private class PostUserStory extends AsyncTask<String, String, String>{
		
		 private EditText title;
	     private EditText description;
	     private EditText difficulty;
	     private EditText priority;
			
			public PostUserStory(EditText title, EditText description, EditText difficulty, EditText priority){
				this.title = title;
				this.description = description;
				this.difficulty = difficulty;
				this.priority = priority;
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
					wr.writeBytes("user_story={\"title\":\"" + title.getText() + "\",\"description\":\"" + description.getText() + "\",\"difficulty\":\"" + difficulty.getText() + "\",\"priority\":\"" + priority.getText() + "\",\"project_id\":\"" + String.valueOf(project_id) +  "\"}");
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
