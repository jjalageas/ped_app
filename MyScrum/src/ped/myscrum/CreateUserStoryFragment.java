package ped.myscrum;

import info.androidhive.slidingmenu.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class CreateUserStoryFragment extends Fragment{

	public CreateUserStoryFragment(){}
	
	@SuppressWarnings("unused")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_create_user_story, container, false);
        Button submit_user_story = (Button) rootView.findViewById(R.id.submit);
        EditText title = (EditText) rootView.findViewById(R.id.edit_title);
        EditText description = (EditText) rootView.findViewById(R.id.edit_description);
		EditText difficulty = (EditText) rootView.findViewById(R.id.edit_difficulty);
        EditText priority = (EditText) rootView.findViewById(R.id.edit_priority);
        return rootView;
    }
	
}
