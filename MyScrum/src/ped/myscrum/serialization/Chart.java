package ped.myscrum.serialization;

import java.io.Serializable;

public class Chart implements Serializable {

	private static final long serialVersionUID = -5498670130943592475L;
	
	public String difficulty;
	public String start_date;
	public SerialBitmap pie_chart;
	public SerialBitmap burndown_chart;
	
	

}
