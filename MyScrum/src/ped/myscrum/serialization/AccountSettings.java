package ped.myscrum.serialization;

import java.io.Serializable;

public class AccountSettings implements Serializable{
	
	private static final long serialVersionUID = -220897358777066028L;
	
	private String username;
	private String firstname;
	private String lastname;
	private String email;

	public AccountSettings(String un, String fn, String ln, String em){
		username=un;
		firstname=fn;
		lastname=ln;
		email=em;
	}

	public String getUsername(){
		return username;
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

	public void setUsername(String s){
		username = s;
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
