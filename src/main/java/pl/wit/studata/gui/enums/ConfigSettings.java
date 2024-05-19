/**
 * 
 */
package pl.wit.studata.gui.enums;

/**
 * Klasa wyliczeniowa zawierająca nazwy pól w formularzu konfiguracyjnym.
 */
public enum ConfigSettings {
	THREADPOOL_SIZE("Thread Pool Size"),
	DB_PATH("Path to Database (Folder)");
	
	private String settingName = null;
	
	private ConfigSettings(String settingName) {
		this.settingName = settingName;
	}
	
	public String getSettingName() {
		return settingName;
	}
}
