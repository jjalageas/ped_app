package ped.myscrum.serialization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tests  implements Serializable{
	
	private static final long serialVersionUID = -2553462670306945698L;
	
	private List<TestContent> tests;
	
	public Tests(){
		tests = new ArrayList<TestContent>();
	}
	
	public List<TestContent> getTests(){
		return tests;
	}

}
