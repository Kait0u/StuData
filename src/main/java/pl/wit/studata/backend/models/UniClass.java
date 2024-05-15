package pl.wit.studata.backend.models;

// Klasa reprezentująca przedmiot

import java.util.List;

public class UniClass {

	// Zmienne
	private String className;
	private List<ClassCriterion> criteriaList;

	// Konstruktor
	public UniClass(String className, List<ClassCriterion> criteriaList) {
		this.className = className;
		this.criteriaList = criteriaList;
	}

	// Metoda dodająca kryterium oceniania
	public void addCriterion(ClassCriterion criterion) {
		criteriaList.add(criterion);
	}

	// gettery i settery
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<ClassCriterion> getCriteriaList() {
		return criteriaList;
	}

	public void setCriteriaList(List<ClassCriterion> criteriaList) {
		this.criteriaList = criteriaList;
	}

}
