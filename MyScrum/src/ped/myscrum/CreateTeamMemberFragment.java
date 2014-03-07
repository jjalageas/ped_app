package ped.myscrum;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import info.androidhive.slidingmenu.R;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class CreateTeamMemberFragment extends Fragment{
	
	public CreateTeamMemberFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_create_user, container, false);

        //TODO retrieve users from database and add them dynamically
    	Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner1);
    	
 
    	String result = "";
    	InputStream is = null;
    	try{
    	        HttpClient httpclient = new DefaultHttpClient();
    	        HttpPost httppost = new HttpPost("http://10.0.2.2/ped_app/MyScrum/database_connection/connection.php");
    	        HttpResponse response = httpclient.execute(httppost);
    	        HttpEntity entity = response.getEntity();
    	        is = entity.getContent();
    	}catch(Exception e){
    	        Log.e("log_tag", "Error in http connection "+e.toString());
    	}

    	try{
    	        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
    	        StringBuilder sb = new StringBuilder();
    	        String line = null;
    	        while ((line = reader.readLine()) != null) {
    	                sb.append(line + "\n");
    	        }
    	        is.close();
    	 
    	        result=sb.toString();
    	}catch(Exception e){
    	        Log.e("log_tag", "Error converting result "+e.toString());
    	}
    	 
    	try{
		JSONArray jArray = new JSONArray(result);
		for(int i=0;i<jArray.length();i++){
			JSONObject json_data = jArray.getJSONObject(i);
			Log.i("log_tag","username: "+json_data.getString("username"));
		}
    	   
	}catch(JSONException e){
	        Log.e("log_tag", "Error parsing data "+e.toString());
	}
    	
    	
    	
    	
    	List<String> list = new ArrayList<String>();
    	list.add("jjalageas");
    	list.add("fcastand");
    	list.add("jport");
    	list.add("rherbert");
    	list.add("tdiallo");
    	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(),
    		android.R.layout.simple_spinner_item, list);
    	dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinner.setAdapter(dataAdapter);
        
        Button add_user = (Button) rootView.findViewById(R.id.add_user);
        
        
        return rootView;
    }

}
