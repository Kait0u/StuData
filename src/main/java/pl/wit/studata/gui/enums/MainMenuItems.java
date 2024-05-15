/**
 * 
 */
package pl.wit.studata.gui.enums;

/**
 * Klasa wyliczeniowa zbierająca w jednym miejscu opcje z menu "Menu"
 */
public enum MainMenuItems {
	SAVE("Save"),
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
