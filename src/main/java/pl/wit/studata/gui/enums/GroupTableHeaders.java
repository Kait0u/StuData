/**
 * 
 */
package pl.wit.studata.gui.enums;

/**
 * Klasa wyliczeniowa zawierająca nagłówki kolumn w tabeli z grupami.
 */
public enum GroupTableHeaders {
	CODE("Code"),
	NAME("Name"),
	DESCRIPTION("Description");
	
	private String headerName = null;
	private GroupTableHeaders(String name) {
		headerName = name;
	}
	
	public String getHeaderName() {
		return headerName;
	}
}
