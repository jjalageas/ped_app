package ped.myscrum.serialization;

import java.io.Serializable;

public class ProjectDescription implements Serializable{
	
	private static final long serialVersionUID = -6399654966463720944L;
	private String project_description;
	
	public ProjectDescription(String des){
		project_description = des;
	}
	
	public String getDescritpion(){
		return project_description;
	}
	
	public void setDescription(String s){
		project_description = s;
	}
	
	

}
