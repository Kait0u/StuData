package pl.wit.studata;

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
	}

}
