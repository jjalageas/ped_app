package ped.myscrum.serialization;

import java.io.Serializable;

public class SprintContent implements Serializable{
	
	private static final long serialVersionUID = 3629158452688506274L;
	
	private String id;
	private String start_date;
	private String duration;
	
	public SprintContent(String id){
		this.id = id;
	}
	
	public String getId(){
		return id;
	}
	
	public String getStartDate(){
		return start_date;
	}
	
	public String getDuration(){
		return duration;
	}
	
	public void setId(String s){
		id = s;
	}
	
	public void setStartDate(String s){
		start_date = s;
	}
	
	public void setDuration(String s){
		duration = s;
	}
}
