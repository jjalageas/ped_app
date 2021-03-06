package ped.myscrum.serialization;

import java.io.Serializable;

public class SprintContent implements Serializable{
	
	private static final long serialVersionUID = 3629158452688506274L;
	
	private String id;
	private String start_date;
	private String duration;
	private int id_num;
	
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
	
	public int getIdNum(){
		return id_num;
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
	
	public void setIdNum(int i){
		id_num = i;
	}
	
}
