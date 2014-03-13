package ped.myscrum.serialization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Team implements Serializable{
	
	private static final long serialVersionUID = 7836154441443926181L;
	
	private List<TeamMember> team;
	
	public Team(){
		team = new ArrayList<TeamMember>();
	}
	
	public List<TeamMember> getTeam(){
		return team;
	}
	
	

}
