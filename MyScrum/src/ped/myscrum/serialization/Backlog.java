package ped.myscrum.serialization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Backlog implements Serializable{

	private static final long serialVersionUID = -7962670726688593640L;
	
	private List<BacklogContent> obj;
	
	public Backlog(){
		obj = new ArrayList<BacklogContent>();
	}
	
	public List<BacklogContent> getBacklog(){
		return obj;
	}

	
}
