/**
 * 
 */
package pl.wit.studata.gui.enums;

/**
 * Klasa wyliczeniowa wspierająca tworzenie zakładek poprzez m.in.: dostarczanie ich nazw.
 */
public enum MainTabs {
	STUDENTS("Students"),
	GROUPS("Groups"),
	CLASSES("Classes"),
	GRADING("Grading");
	
	/**
	 * Nazwa zakładki.
	 */
	private String tabName;
	
	/**
	 * Konstruktor tworzący pojedynczy wpis w klasie wyliczeniowej.
	 * @param tabName
	 */
	private MainTabs(String tabName) {
		this.tabName = tabName;
	}
	
	public String getTabName() {
		return tabName;
	}
}
