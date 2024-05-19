/**
 * 
 */
package pl.wit.studata.gui.interfaces;

/**
 * Interfejs ustalający metody potrzebne do wysłania obecnego stanu danych danej np. zakładki celem wysłania jej do bazy danych.
 * @author Jakub Jaworski
 */
public interface IDatabaseInteractor {
	/**
	 * Metoda, która wysyła obecny stan obiektu do bazdy danych celem wprowadzenia w niej zmian trwałych.
	 */
	public void pushToDB();
	
	/**
	 * Metoda, które zwraca informację o tym, czy dany obiekt ma jakieś niezapisane zmiany.
	 * @return true, jeśli istnieją zmiany do zapisania; false w przeciwnym przypadku.
	 */
	public boolean hasUnsavedChanges();
	
	/**
	 * Metoda, która wysyła do obiektu informację o tym, że zmiany mają zostać odrzucone.
	 */
	public void nullifyChanges();
	
	/**
	 * Metoda, która uaktualnia stan obiektu poprzez uaktualnienie danych ze źródła zewnętrznego.
	 */
	public void update();
}
