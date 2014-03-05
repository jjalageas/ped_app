package ped.myscrum;

import info.androidhive.slidingmenu.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ped.myscrum.adapter.ExpandableListAdapter;
import android.app.Fragment;
import android.os.Bundle;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

public class BacklogFragment extends Fragment {

	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;

	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_backlog, container, false);

		expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
		prepareListData();
		listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
		expListView.setAdapter(listAdapter);
		expListView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				if(listDataHeader.get(groupPosition).equals("Create User Story")){
					Fragment fragment = new CreateUserStoryFragment();
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();
				}
				return false;
			}
		});
		
		return rootView;
	}

	private void prepareListData() {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();

		listDataHeader.add("User Story #1");
		listDataHeader.add("Create User Story");

		List<String> userStory1 = new ArrayList<String>();
		userStory1.add("Title");
		userStory1.add("Description");
		userStory1.add("Priority");
		userStory1.add("Difficulty");
		userStory1.add("Finished");
		userStory1.add("Validated");
		
		listDataChild.put(listDataHeader.get(0), userStory1);

	}
}

	

