package pl.wit.studata.backend.models;



// Klasa reprezentująca grupe

public class UniGroup {

	// Zmienne
	private String groupCode, specialization, description;

	// Konstruktor
	public UniGroup(String groupCode, String specialization, String description) {
		this.groupCode = groupCode;
		this.specialization = specialization;
		this.description = description;
	}

	// gettery i settery
	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}