/**
 * 
 */
package pl.wit.studata;

/**
 * Klasa przechowująca dane wykorzystywane w różnych miejscach aplikacji.
 * @author Jakub Jaworski
 */
public class AppData {
	/**
	 * Tytuł aplikacji.
	 */
	public static final String APP_TITLE = "StuData";
	
	/**
	 * Minimalna szerokość okienka głównego.
	 */
	public static final int MIN_WIDTH = 1024;
	
	/**
	 * Minimalna wysokość okienka głównego.
	 */
	public static final int MIN_HEIGHT = 600;
	
	// Wartości bliższe działaniu wewnętrznym aplikacji -------------
	/**
	 * Wartość zastępcza, która zostanie wyświetlona użytkownikowi w przypadku, gdy pewna wartość jest nullem.
	 */
	public static final String NONE_TEXT = "<None>";
}
