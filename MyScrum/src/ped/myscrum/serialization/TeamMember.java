package ped.myscrum.serialization;

import java.io.Serializable;

public class TeamMember implements Serializable{

	private static final long serialVersionUID = 4907815571712247439L;

	private String firstname;
	private String lastname;
	private String email;
	private String username;
	
	public TeamMember(String un){
		username=un;
		firstname = "";
		email = "";
		lastname = "";
	}
	

	public String getUsername(){
		return username;
	}

	public void setUsername(String s){
		username=s;
	}


	public String getFirstname(){
		return firstname;
	}

	public String getLastname(){
		return lastname;
	}

	public String getEmail(){
		return email;
	}

	public void setFirstname(String s){
		firstname = s;
	}

	public void setLastname(String s){
		lastname = s;
	}

	public void setEmail(String s){
		email = s;
	}



}
