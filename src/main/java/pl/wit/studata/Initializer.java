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
		// TODO Zacznij od wczytania configa.
		
		InternalData.setupFromConfig();
		
		debugDBSetup();
	}
	
	private static void debugDBSetup() {
		InternalData.DATABASE.setStudentList(List.of(new UniStudent("Bob", "Dudu", 0)));
		InternalData.DATABASE.setGroupList(List.of(new UniGroup("IO1", "Inżynieria Oprogramowania", "EEEO")));
	}

}
