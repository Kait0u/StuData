package pl.wit.studata.gui.enums;

/**
 * Klasa wyliczeniowa zawierająca nagłówki kolumn w tabeli z przedmiotami.
 * 
 * @author Jakub Jaworski
 */
public enum ClassTableHeaders {
	CODE("Code"),
	NAME("Name"),
	CRITERIA("Criteria");
	
	private String headerName = null;
	private ClassTableHeaders(String name) {
		headerName = name;
	}
	
	public String getHeaderName() {
		return headerName;
	}
}
