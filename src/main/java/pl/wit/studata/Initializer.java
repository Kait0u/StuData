package pl.wit.studata;

/**
 * Klasa pełniąca rolę inizjalizatora aplikacji.
 * @author Jakub Jaworski
 */
public class Initializer {
	public static void initialize() {
		Config.loadFromFile();
		InternalData.setupFromConfig();
	}
}
