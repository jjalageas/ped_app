package ped.myscrum;

import info.androidhive.slidingmenu.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ped.myscrum.adapter.ExpandableListAdapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class TeamFragment extends Fragment {

	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;

	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_team, container, false);


		expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
		prepareListData();
		listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
		expListView.setAdapter(listAdapter);
		expListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				if(listDataHeader.get(groupPosition).equals("Add Team Member")){
					Fragment fragment = new CreateTeamMemberFragment();
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();
				}
				return false;
			}
		});



		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				switch (childPosition) {
					case 0:
						break;
					case 1:
						//TODO query to remove user
						break;		
					default:
						break;
				}
				return false;
			}
		});
		return rootView;
	}


	private void prepareListData() {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();

		// Adding child data
		listDataHeader.add("Team member #1");
		listDataHeader.add("Team member #2");
		listDataHeader.add("Team member #3");
		listDataHeader.add("Add Team Member");

		// Adding child data
		List<String> user1 = new ArrayList<String>();
		user1.add("Rank");
		user1.add("Remove User");


		List<String> user2 = new ArrayList<String>();
		user2.add("Rank");
		user2.add("Remove User");
		
		List<String> user3 = new ArrayList<String>();
		user3.add("Rank");
		user3.add("Remove User");


		listDataChild.put(listDataHeader.get(0),user1); // Header, Child data
		listDataChild.put(listDataHeader.get(1), user2);
		listDataChild.put(listDataHeader.get(2), user3);
	}
}

	

