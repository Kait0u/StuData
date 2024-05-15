package pl.wit.studata.backend.models;

//Klasa reprezentująca studenta

public class UniStudent {

	// Zmienne
	private String firstName, lastName;
	private int studentId;

	// Konstruktor
	public UniStudent(String firstName, String lastName, int studentId) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.studentId = studentId;
	}

	// gettery i settery
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

}
