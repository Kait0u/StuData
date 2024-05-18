package pl.wit.studata.backend.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;

//Klasa reprezentująca przedmiot

import java.util.List;

import pl.wit.studata.backend.fileio.Serializable;

public class UniClass implements Serializable {

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

	// zapis do pliku
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
