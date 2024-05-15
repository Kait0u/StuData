package pl.wit.studata.gui.enums;

/**
 * Klasa wyliczeniowa zawierająca nagłówki kolumn w tabeli ze studentami.
 * @author Jakub Jaworski
 */
public enum StudentTableHeaders {
	ID("ID"),
	FNAME("First Name"),
	LNAME("Last Name"),
	GROUP("Group");
	
	
	private String headerName = null;
	private StudentTableHeaders(String name) {
		headerName = name;
	}
	
	public String getHeaderName() {
		return headerName;
	}
}
