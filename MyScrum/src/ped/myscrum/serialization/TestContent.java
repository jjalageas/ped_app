package ped.myscrum.serialization;

import java.io.Serializable;

public class TestContent implements Serializable{

	private static final long serialVersionUID = 4907815571712247439L;

	private String id;
	private String title;
	private String state;
	private String user_story_id;
	
	public TestContent(String id){
		this.id = id;
		title = "";
		state = "";
		user_story_id = "";
	}
	

	public String getId(){
		return id;
	}

	public void setId(String s){
		id=s;
	}


	public String getState(){
		return state;
	}

	public String getUserStoryId(){
		return user_story_id;
	}

	public String getTitle(){
		return title;
	}

	public void setState(String s){
		state = s;
	}

	public void setUserStoryId(String s){
		user_story_id = s;
	}

	public void setTitle(String s){
		title = s;
	}



}
