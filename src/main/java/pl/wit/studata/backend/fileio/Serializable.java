package pl.wit.studata.backend.fileio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
/**
 * Interfejs do zapisu i odczytu obiektów z plików
 * @author Aliaksei Harbuz
 * 
 */
public interface Serializable {
	
	public void saveToFile(DataOutputStream dout) throws Exception;
	public void saveMapElem(DataOutputStream dout) throws Exception;

	public void loadFromFile(DataInputStream din) throws Exception;
	// dodatkowo klasy mają metodę
	//public static *TYPE* loadMapRef(DataInputStream din, List<*TYPE*> l) throws Exception;
	

}
