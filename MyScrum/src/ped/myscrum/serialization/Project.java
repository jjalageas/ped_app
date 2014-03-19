package ped.myscrum.serialization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable{

	private static final long serialVersionUID = 17996955681309380L;
	
	private List<String> project;
	private List<String> ids;
	
	public Project(){
		project = new ArrayList<String>();
		ids = new ArrayList<String>();
	}
	
	public List<String> getProjects(){
		return project;
	}
	
	public List<String> getIds(){
		return ids;
	}
	
	public void setIds(List<String> l){
		ids = l;
	}
	
	

}
