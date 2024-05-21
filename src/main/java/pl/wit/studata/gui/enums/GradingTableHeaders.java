package pl.wit.studata.gui.enums;

/**
 * Klasa wyliczeniowa zawierająca nagłówki kolumn w tabeli z ocenami.
 * 
 * @author Jakub Jaworski
 */
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
