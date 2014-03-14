package ped.myscrum.serialization;

import java.io.Serializable;

public class JobContent implements Serializable{

	private static final long serialVersionUID = -6639532596869186404L;

	private String id;
	private String title;
	private String description;
	private String difficulty;
	private String finished;
	
	public JobContent(String id){
		this.id = id;
	}
	
	public String getId(){
		return id;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getDescription(){
		return description;
	}
	
	
	public String getDifficulty(){
		return difficulty;
	}
	
	public String getFinished(){
		return finished;
	}
	
	
	public void setId(String s){
		id = s;
	}
	
	public void setTitle(String s){
		title = s;
	}
	
	public void setDescription(String s){
		description = s;
	}
	
	
	public void setDifficulty(String s){
		difficulty = s;
	}
	
	public void setFinished(String s){
		finished = s;
	}
	
	
}
