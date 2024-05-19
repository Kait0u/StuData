package pl.wit.studata.backend.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;

//Klasa reprezentująca przedmiot

import java.util.List;
import java.util.Objects;

import pl.wit.studata.backend.fileio.Serializable;

/**
 * Klasa reprezentująca przedmiot
 * 
 * @author Karol Wojtyra
 * @author Aliaksei Harbuz
 */
public class UniClass implements Serializable {

	/**
	 * Zmienne
	 */
	private String className, code;

	private List<ClassCriterion> criteriaList;

	/*
	 * Metoda toString zwracająca reprezentację obiektu UniStudent w formie łańcucha
	 * znaków
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[").append(code).append("] ").append(className);
		return sb.toString();
	}

	/**
	 * Konstruktor
	 * 
	 * @param className    Nazwa przedmiotu
	 * @param code         Kod przedmiotu
	 * @param criteriaList Lista kryteriów
	 */
	public UniClass(String className, String code, List<ClassCriterion> criteriaList) {
		this.className = className;
		this.code = code;
		this.criteriaList = criteriaList;
	}

	/**
	 * Metoda dodająca kryterium do listy
	 * 
	 * @param criterion Kryterium
	 */
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	// zapis do pliku
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof UniClass))
			return false;

		UniClass other = (UniClass) o;
		return other.getClassName().equals(className) && other.getCriteriaList().equals(criteriaList);
	}

	@Override
	public int hashCode() {
		return Objects.hash(className, criteriaList);
	}

	public void saveToFile(DataOutputStream dout) throws Exception {
		dout.writeUTF(getClass().getSimpleName());
		dout.writeUTF(className);
		dout.writeInt(criteriaList.size());
		for (ClassCriterion cr : criteriaList) {
			cr.saveToFile(dout);
		}
	}

	public void saveMapElem(DataOutputStream dout) throws Exception {
		dout.writeUTF(className);
	}

	public void loadFromFile(DataInputStream din) throws Exception {
		// className = din.readUTF();
		int listLen = din.readInt();

		for (int i = 0; i < listLen; ++i) {
			String shortName = din.readUTF();
			ClassCriterion cr = new ClassCriterion("", 0);
			cr.loadFromFile(din);
			criteriaList.add(cr);
		}

	}

	public static UniClass loadMapRef(DataInputStream din, List<UniClass> l) throws Exception {
		String name = din.readUTF();
		for (UniClass cc : l) {
			if (cc.getClassName() == name)
				return cc;
		}
		return null;
	}

}
