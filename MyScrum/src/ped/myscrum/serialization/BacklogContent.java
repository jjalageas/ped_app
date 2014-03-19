package ped.myscrum.serialization;

import java.io.Serializable;

public class BacklogContent implements Serializable{
	
	private static final long serialVersionUID = 6532381678622264922L;
	
	private String id;
	private String title;
	private String description;
	private String priority;
	private String difficulty;
	private String finished;
	private String validated;
	
	public BacklogContent(String id){
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
	
	public String getPriority(){
		return priority;
	}
	
	public String getDifficulty(){
		return difficulty;
	}
	
	public String getFinished(){
		return finished;
	}
	
	public String getValidated(){
		return validated;
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
	
	public void setPriority(String s){
		priority = s;
	}
	
	public void setDifficulty(String s){
		difficulty = s;
	}
	
	public void setFinished(String s){
		finished = s;
	}
	
	public void setValidated(String s){
		validated = s;
	}
	
	

}
