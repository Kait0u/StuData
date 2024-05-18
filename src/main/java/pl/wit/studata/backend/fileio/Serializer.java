package pl.wit.studata.backend.fileio;

import java.io.DataInputStream;
import java.util.ArrayList;

import pl.wit.studata.backend.models.*;

/**
 * Klasa do odczytywania obiektów z pliku
 * @author Aliaksei Harbuz
 */
public class Serializer {
	public static Serializable loadObj(DataInputStream din) throws Exception{
		String name = din.readUTF();
		Serializable s = null;
		
		switch(name) {
		case "UniClass": s = new UniClass("", new ArrayList<ClassCriterion>()); break;
		case "UniStudent": s = new UniStudent("", "", 0); break;
		case "ClassCriterion": s = new ClassCriterion("", 0); break;
		case "UniGroup": s = new UniGroup("", "", ""); break;
		default: 
			throw new Exception("SERIALIZER: wrong type - " + name);
		}
		if(s != null) {
			s.loadFromFile(din);
		}
		
		return s;
	}
}
