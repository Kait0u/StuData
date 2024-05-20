package pl.wit.studata.backend.fileio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
/**
 * Interfejs do zapisu i odczytu obiektów z plików
 * @author Aliaksei Harbuz
 * 
 */
public interface Serializable {
	
	/**
	 * Zapis obiektu do strumienia.
	 * @param dout Strumień wyjściowy
	 * @throws Exception
	 */
	public void saveToFile(DataOutputStream dout) throws Exception;
	
	/**
	 * Zapis pola kluczowego do strumienia
	 * @param dout Strumień wyjściowy
	 * @throws Exception
	 */
	public void saveMapElem(DataOutputStream dout) throws Exception;

	/**
	 * Wypełnienie pól obiektu ze strumienia.
	 * @param din Strumień wejściowy.
	 * @throws Exception
	 */
	public void loadFromFile(DataInputStream din) throws Exception;
	
	// dodatkowo klasy mają metodę
	//public static *TYPE* loadMapRef(DataInputStream din, List<*TYPE*> l) throws Exception;
	

}
