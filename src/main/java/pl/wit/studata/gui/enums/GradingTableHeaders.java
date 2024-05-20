package pl.wit.studata.gui.enums;

public enum GradingTableHeaders {
	STUDENT("Student"),
	CLASS("Class"),
	CRITERION("Criterion"),
	SCORE("Score");
	
	private String headerName = null;
	private GradingTableHeaders(String name) {
		headerName = name;
	}
	
	public String getHeaderName() {
		return headerName;
	}
}
