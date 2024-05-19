package pl.wit.studata.backend.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;
import java.util.Objects;

import pl.wit.studata.backend.fileio.Serializable;

/**
 * Klasa reprezentująca grupę
 * 
 * @author Karol Wojtyra
 * @author Aliaksei Harbuz
 */

public class UniGroup implements Serializable {

	/**
	 * Zmienne
	 */
	private String groupCode, specialization, description;

	/**
	 * Konstruktor
	 * 
	 * @param groupCode      Kod grupy
	 * @param specialization Specjalizacja
	 * @param description    Opis grupy
	 */
	public UniGroup(String groupCode, String specialization, String description) {
		this.groupCode = groupCode;
		this.specialization = specialization;
		this.description = description;
	}

	/**
	 * Metoda toString zwracająca reprezentację obiektu UniGroup w formie łańcucha
	 * znaków
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[").append(groupCode).append("] ").append(specialization);
		return sb.toString();
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

	// zapis do pliku
	@Override
    public boolean equals(Object o) {
		if(o == this)
			return true;
		if(!(o instanceof UniGroup))
			return false;
		
		UniGroup other = (UniGroup) o;
		return other.getGroupCode().equals(groupCode) && other.getSpecialization().equals(specialization) && other.getDescription().equals(description);
	}
	
	@Override
	public int hashCode() {
	    return Objects.hash(groupCode, specialization, description);
	}
	
	public void saveToFile(DataOutputStream dout) throws Exception {
		dout.writeUTF(getClass().getSimpleName());
		dout.writeUTF(groupCode);
		dout.writeUTF(specialization);
		dout.writeUTF(description);
	}

	public void saveMapElem(DataOutputStream dout) throws Exception {
		dout.writeUTF(groupCode);
	}

	public void loadFromFile(DataInputStream din) throws Exception {
		// din.readUTF();
		groupCode = din.readUTF();
		specialization = din.readUTF();
		description = din.readUTF();
	}

	public static UniGroup loadMapRef(DataInputStream din, List<UniGroup> l) throws Exception {
		String code = din.readUTF();
		for (UniGroup cc : l) {
			if (cc.getGroupCode().equals(code))
				return cc;
		}
		return null;
	}
}