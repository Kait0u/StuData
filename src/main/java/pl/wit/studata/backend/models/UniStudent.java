package pl.wit.studata.backend.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;

import pl.wit.studata.backend.fileio.Serializable;

//Klasa reprezentująca studenta

public class UniStudent implements Serializable {

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
	
	// zapis do pliku
		public void saveToFile(DataOutputStream dout) throws Exception{
			dout.writeUTF(getClass().getSimpleName());
			dout.writeUTF(firstName);
			dout.writeUTF(lastName);
			dout.writeInt(studentId);
		}
		
		public void saveMapElem(DataOutputStream dout) throws Exception{
			dout.writeInt(studentId);
		}
		
		public void loadFromFile(DataInputStream din) throws Exception{
			//din.readUTF();
			firstName = din.readUTF();
			lastName = din.readUTF();
			studentId = din.readInt();
		}
		
		public static UniStudent loadMapRef(DataInputStream din, List<UniStudent> l) throws Exception{
			int id = din.readInt();
			for(UniStudent s: l) {
				if(s.getStudentId() == id) return s;
			}
			return null;
		}

}
