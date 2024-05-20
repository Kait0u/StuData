package pl.wit.studata.backend.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;
import java.util.Objects;

import pl.wit.studata.backend.fileio.Serializable;

/**
 * Klasa reprezentująca studenta
 * 
 * @author Karol Wojtyra
 * @author Aliaksei Harbuz
 */

public class UniStudent implements Serializable {

	/**
	 * Zmienne
	 */
	private String firstName, lastName;
	private int studentId;

	/**
	 * Konstruktor
	 * 
	 * @param firstName Imie
	 * @param lastName  Nazwisko
	 * @param studentId Numer indeksu
	 */
	public UniStudent(String firstName, String lastName, int studentId) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.studentId = studentId;
	}

	/**
	 * Metoda toString zwracająca reprezentację obiektu UniStudent w formie łańcucha
	 * znaków
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[").append(studentId).append("] ").append(firstName).append(" ")
				.append(lastName);
		return sb.toString();
	}
	
	/**
	 * Tworzy kopię głęboką obiektu.
	 * @return Kopia głęboka.
	 */
	public UniStudent deepCopy() {
		return new UniStudent(new String(this.firstName), new String(this.lastName), studentId);
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

	// zapis do pliku
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof UniStudent))
			return false;

		UniStudent other = (UniStudent) o;
		return other.getFirstName().equals(firstName) && other.getLastName().equals(lastName)
				&& other.getStudentId() == studentId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstName, lastName, studentId);
	}

	public void saveToFile(DataOutputStream dout) throws Exception {
		dout.writeUTF(getClass().getSimpleName());
		dout.writeUTF(firstName);
		dout.writeUTF(lastName);
		dout.writeInt(studentId);
	}

	public void saveMapElem(DataOutputStream dout) throws Exception {
		dout.writeInt(studentId);
	}

	public void loadFromFile(DataInputStream din) throws Exception {
		// din.readUTF();
		firstName = din.readUTF();
		lastName = din.readUTF();
		studentId = din.readInt();
	}

	/**
	 * Metoda pobierająca wartość pola kluczowego ze strumienia i wyszukująca element z listy o takiej samej wartośći pola kluczowego. 
	 * @param din Strumień wejściowy.
	 * @param l Lista studentów.
	 * @return Student.
	 * @throws Exception
	 */
	public static UniStudent loadMapRef(DataInputStream din, List<UniStudent> l) throws Exception {
		int id = din.readInt();
		for (UniStudent s : l) {
			if (s.getStudentId() == id)
				return s;
		}
		return null;
	}

}
