package ped.myscrum;

import info.androidhive.slidingmenu.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class CreateProjectFragment extends Fragment{

	public CreateProjectFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_create_project, container, false);
        Button create_project = (Button) rootView.findViewById(R.id.create_project);
        EditText project_name = (EditText) rootView.findViewById(R.id.edit_project_name);
        EditText project_repo = (EditText) rootView.findViewById(R.id.edit_project_repo);	
        
        return rootView;
    }
	
}
