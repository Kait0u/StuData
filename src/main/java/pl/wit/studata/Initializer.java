package pl.wit.studata;

/**
 * Klasa pełniąca rolę inizjalizatora aplikacji.
 * @author Jakub Jaworski
 */
public class Initializer {
	/**
     * Metoda inicjalizuje aplikację.
     * Wczytuje konfigurację z pliku i ustawia dane wewnętrzne.
     */
	public static void initialize() {
		Config.loadFromFile();
		InternalData.setupFromConfig();
	}
}
