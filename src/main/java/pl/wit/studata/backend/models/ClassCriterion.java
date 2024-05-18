package pl.wit.studata.backend.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;

import pl.wit.studata.backend.fileio.Serializable;

//Klasa reprezentująca dane kryterium oceniania
public class ClassCriterion implements Serializable {

	// Zmienne
	private String criterionName;
	private int maxPoints;

	// Konstruktor
	public ClassCriterion(String criterionName, int maxPoints) {
		this.criterionName = criterionName;
		this.maxPoints = maxPoints;
	}

	/*
	 * Metoda sprawdzająca czy podana ocena mieści się w kryterium
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
		public void saveToFile(DataOutputStream dout) throws Exception{
			dout.writeUTF(getClass().getSimpleName());
			dout.writeUTF(criterionName);
			dout.writeInt(maxPoints);
		}
		
		public void saveMapElem(DataOutputStream dout) throws Exception{
			dout.writeUTF(criterionName);
		}
		
		public void loadFromFile(DataInputStream din) throws Exception{
			//din.readUTF();
			criterionName = din.readUTF();
			maxPoints = din.readInt();
		}
		
		public static ClassCriterion loadMapRef(DataInputStream din, List<ClassCriterion> l) throws Exception {
			String name = din.readUTF();
			for(ClassCriterion cc: l) {
				if(cc.getCriterionName() == name) return cc;
			}
			return null;
		}

}
