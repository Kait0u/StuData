package pl.wit.studata;

import java.util.List;

import pl.wit.studata.backend.models.UniGroup;
import pl.wit.studata.backend.models.UniStudent;

/**
 * Klasa pełniąca rolę inizjalizatora aplikacji.
 * @author Jakub Jaworski
 */
public class Initializer {

	/**
	 * Konstruktor bezparametryczny.
	 */
	public Initializer() {
		// Nic póki co
	}
	
	public static void initialize() {
		Config.loadFromFile();
		InternalData.setupFromConfig();
	}

}
