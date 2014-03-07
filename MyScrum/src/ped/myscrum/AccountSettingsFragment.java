package ped.myscrum;

import java.io.IOException;

import ped.myscrum.database.OwnerInformationRetrieval;
import info.androidhive.slidingmenu.R;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AccountSettingsFragment extends Fragment{

	public AccountSettingsFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_account_settings, container, false);
        
        TextView username = (TextView) rootView.findViewById(R.id.username);
        TextView firstname = (TextView) rootView.findViewById(R.id.first_name);
        TextView lastname = (TextView) rootView.findViewById(R.id.last_name);
        TextView email = (TextView) rootView.findViewById(R.id.email);
        
        OwnerInformationRetrieval oir = new OwnerInformationRetrieval(username, firstname, lastname, email);
        oir.execute("http://10.0.2.2:3000/api/owner/3/profile");     
        
        return rootView;
    }
	
}
