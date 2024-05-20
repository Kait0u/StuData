package pl.wit.studata.gui.enums;

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
