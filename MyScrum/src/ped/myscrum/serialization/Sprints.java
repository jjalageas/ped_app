package ped.myscrum.serialization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Sprints implements Serializable{


	private static final long serialVersionUID = 2523320242507446907L;
	private List<SprintContent> obj;

	public Sprints(){
		obj = new ArrayList<SprintContent>();
	}

	public List<SprintContent> getSprints(){
		return obj;
	}

}
