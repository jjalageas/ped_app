package ped.myscrum;

import info.androidhive.slidingmenu.R;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class LoginFragment extends Fragment{

	private Button loginButton;
	
	public LoginFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        
        loginButton = (Button) rootView.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				startLoginActivity();
			}
		});
         
        return rootView;
    }
	
    
	private void startLoginActivity() {
    	Intent intent = new Intent(LoginFragment.this.getActivity(), MainActivityLoggedIn.class);
    	startActivity(intent);
    }
	
	
}
