package ped.myscrum.display;

import info.androidhive.slidingmenu.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChartFragment extends Fragment{
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        return rootView;
    }

}
