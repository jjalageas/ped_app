package ped.myscrum;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.slidingmenu.R;
import android.app.Fragment;
import android.os.Bundle;
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
