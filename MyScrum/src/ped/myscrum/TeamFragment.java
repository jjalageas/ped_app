package ped.myscrum;

import android.app.Fragment;
import info.androidhive.slidingmenu.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TeamFragment extends Fragment{

	ListView listView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_team, container, false);

		listView = (ListView) rootView.findViewById(R.id.list);
		String[] values = new String[] { "Team member #1", 
				"Team member #2",
				"Team member #3",
				"Team member #4", 
				"Team member #5", 
				"Team member #6", 
				"Team member #7", 
				"Add Team Member" 
		};

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_list_item_1, android.R.id.text1, values);

		listView.setAdapter(adapter); 
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				int itemPosition     = position;
				String  itemValue    = (String) listView.getItemAtPosition(position);

			}

		});
		return rootView; 
	}

}


	

