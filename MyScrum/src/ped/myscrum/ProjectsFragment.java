package ped.myscrum;

import info.androidhive.slidingmenu.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ped.myscrum.adapter.ExpandableListAdapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

public class ProjectsFragment extends Fragment {

	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;

	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_project, container, false);


		expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
		prepareListData();
		listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
		expListView.setAdapter(listAdapter);
		expListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return false;
			}
		});


		expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				;
			}
		});

		expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				;

			}
		});

		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Fragment fragment = null;
				switch (childPosition) {
					case 0:
						fragment = new TeamFragment();
						break;
					case 1:
						fragment = new BacklogFragment();
						break;
					case 2:
						fragment = new SprintsFragment();
						break;
					case 3:
						fragment = new TestsFragment();
						break;			
					default:
						break;
				}
				
				if (fragment != null) {
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();
				} else {
					// error in creating fragment
					Log.e("MainActivity", "Error in creating fragment");
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
		listDataHeader.add("Project 1");
		listDataHeader.add("Project 2");
		listDataHeader.add("Project 3");

		// Adding child data
		List<String> project1 = new ArrayList<String>();
		project1.add("Team");
		project1.add("Backlog");
		project1.add("Sprints");
		project1.add("Tests");

		List<String> project2 = new ArrayList<String>();
		project2.add("Team");
		project2.add("Backlog");
		project2.add("Sprints");
		project2.add("Tests");
		
		List<String> project3 = new ArrayList<String>();
		project3.add("Team");
		project3.add("Backlog");
		project3.add("Sprints");
		project3.add("Tests");


		listDataChild.put(listDataHeader.get(0), project1); // Header, Child data
		listDataChild.put(listDataHeader.get(1), project2);
		listDataChild.put(listDataHeader.get(2), project3);
	}
}
