/**
 * 
 */
package pl.wit.studata.gui.interfaces;

/**
 * Interfejs ustalający metody potrzebne do wysłania obecnego stanu danych danej np. zakładki celem wysłania jej do bazy danych.
 * @author Jakub Jaworski
 */
public interface IDatabasePusher {
	/**
	 * Metoda, która wysyła obecny stan obiektu do bazdy danych celem wprowadzenia w niej zmian trwałych.
	 */
	public void pushToDB();
}
