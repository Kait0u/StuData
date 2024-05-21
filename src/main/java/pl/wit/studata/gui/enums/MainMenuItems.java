/**
 * 
 */
package pl.wit.studata.gui.enums;


/**
 * Klasa wyliczeniowa zbierająca w jednym miejscu opcje z menu "Menu"
 * 
 * @author Jakub Jaworski
 */
public enum MainMenuItems {
	SAVE("Save"),
	REFRESH("Refresh"),
	CONFIG("Config"),
	QUIT("Quit");
	
	private String optionName = null;
	private MainMenuItems(String optionName) {
		this.optionName = optionName;
	}
	
	public String getOptionName() {
		return this.optionName;
	}
}
