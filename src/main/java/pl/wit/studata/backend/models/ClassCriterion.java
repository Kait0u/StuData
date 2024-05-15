package pl.wit.studata.backend.models;



// Klasa reprezentująca dane kryterium oceniania
public class ClassCriterion {

	// Zmienne
	private String criterionName;
	private int maxPoints;

	// Konstruktor
	public ClassCriterion(String criterionName, int maxPoints) {
		this.criterionName = criterionName;
		this.maxPoints = maxPoints;
	}

	// gettery i settery
	public String getCriterionName() {
		return criterionName;
	}

	public void setCriterionName(String criterionName) {
		this.criterionName = criterionName;
	}

	public int getMaxPoints() {
		return maxPoints;
	}

	public void setMaxPoints(int maxPoints) {
		this.maxPoints = maxPoints;
	}

}
