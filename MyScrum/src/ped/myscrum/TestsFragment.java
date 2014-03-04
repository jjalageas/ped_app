package ped.myscrum;

import android.app.Fragment;
import info.androidhive.slidingmenu.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TestsFragment extends Fragment{

	public TestsFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_tests, container, false);
        
        return rootView;
    }
	
}
