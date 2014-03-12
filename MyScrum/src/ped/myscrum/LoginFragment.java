
package ped.myscrum;

import info.androidhive.slidingmenu.R;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends Fragment{

	private Button loginButton;
	private EditText apiKey;
	
	public LoginFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        
        apiKey = (EditText) rootView.findViewById(R.id.key);
        loginButton = (Button) rootView.findViewById(R.id.login_button);
        
        loginButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Editable key = apiKey.getText();
				String stringApiKey = key.toString();
				startLoginActivity(stringApiKey);
			}
		});
         
        return rootView;
    }
	
    
	private void startLoginActivity(String stringApiKey) {
    	Intent intent = new Intent(LoginFragment.this.getActivity(), MainActivityLoggedIn.class);
    	Bundle b = new Bundle();
    	b.putCharSequence("api_key", stringApiKey);
    	intent.putExtras(b);
    	startActivity(intent);
    }
	
	
}
