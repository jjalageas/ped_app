package ped.myscrum;

import info.androidhive.slidingmenu.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SignupFragment extends Fragment {
	
	public SignupFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        Button sign_up = (Button) rootView.findViewById(R.id.signup_button);
        EditText first_name = (EditText) rootView.findViewById(R.id.firstname);
        EditText last_name = (EditText) rootView.findViewById(R.id.lastname);
        EditText username = (EditText) rootView.findViewById(R.id.username);
        EditText password = (EditText) rootView.findViewById(R.id.password);
        return rootView;
    }
}
