package pl.wit.studata.backend.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;
import java.util.Objects;

import pl.wit.studata.backend.fileio.Serializable;

/**
 * Klasa reprezentująca kryterium oceniania
 * 
 * @author Karol Wojtyra
 * @author Aliaksei Harbuz
 */
public class ClassCriterion implements Serializable {

	/**
	 * Zmienne
	 */
	private String criterionName;
	private int maxPoints;

	/**
	 * Konstruktor
	 * 
	 * @param criterionName Nazwa kryterium
	 * @param maxPoints     Maksymalna ilość punktów do uzyskania
	 */
	public ClassCriterion(String criterionName, int maxPoints) {
		this.criterionName = criterionName;
		this.maxPoints = maxPoints;
	}

	/**
	 * Metoda toString zwracająca reprezentację obiektu ClassCriterion w formie
	 * łańcucha znaków
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(criterionName).append(" (").append(maxPoints).append(")");
		return sb.toString();
	}

	/**
	 * Metoda sprawdzająca czy podana ocena mieści się w kryterium
	 * 
	 * @param score Ilość punktów
	 * @return true gdy ocena jest poprawna
	 */
	public boolean validateScore(int score) {
		if (0 <= score && score <= maxPoints) {
			return true;
		} else
			return false;
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

	// zapis do pliku
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof ClassCriterion))
			return false;

		ClassCriterion other = (ClassCriterion) o;
		return other.getCriterionName().equals(criterionName) && other.getMaxPoints() == maxPoints;
	}

	@Override
	public int hashCode() {
		return Objects.hash(criterionName, maxPoints);
	}

	public void saveToFile(DataOutputStream dout) throws Exception {
		dout.writeUTF(getClass().getSimpleName());
		dout.writeUTF(criterionName);
		dout.writeInt(maxPoints);
	}

	public void saveMapElem(DataOutputStream dout) throws Exception {
		dout.writeUTF(criterionName);
	}

	public void loadFromFile(DataInputStream din) throws Exception {
		// din.readUTF();
		criterionName = din.readUTF();
		maxPoints = din.readInt();
	}

	public static ClassCriterion loadMapRef(DataInputStream din, List<ClassCriterion> l) throws Exception {
		String name = din.readUTF();
		for (ClassCriterion cc : l) {
			if (cc.getCriterionName().equals(name))
				return cc;
		}
		return null;
	}

}
