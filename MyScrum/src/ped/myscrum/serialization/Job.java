package ped.myscrum.serialization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Job implements Serializable{

	private static final long serialVersionUID = 5953969078724626018L;
	
	private List<JobContent> obj;
	
	public Job(){
		obj = new ArrayList<JobContent>();
	}
	
	public List<JobContent> getJobs(){
		return obj;
	}

}
