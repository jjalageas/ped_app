package ped.myscrum;

import ped.myscrum.database.DescriptionRetrieval;
import info.androidhive.slidingmenu.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DescriptionFragment extends Fragment{

	private CharSequence api_key;
	private int project_id;
	
	public DescriptionFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
		api_key = getArguments().getString("api_key");
		project_id = getArguments().getInt("project_id");
        View rootView = inflater.inflate(R.layout.fragment_project_description, container, false);
        
        TextView description = (TextView) rootView.findViewById(R.id.description);
        DescriptionRetrieval descrition_data = new DescriptionRetrieval(description);
        descrition_data.execute("http://10.0.2.2:3000/api/owner/projects/" + project_id + "/description?api_key=" + api_key);
        
        return rootView;
    }
	
}
