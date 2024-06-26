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
	 * Metoda, która pobiera dane z bazy danych do swoich kolekcji lokalnych.
	 */
	public void pullFromDB();
	
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
	 * Metoda, która uaktualnia stan obiektu poprzez uaktualnienie danych ze źródła zewnętrznego i ewentualne odświeżenie zasobów, które tego mogą wymagać.
	 */
	public void update();
	
	/**
	 * Metoda, która uaktualnia stan obiektów oryginalnych do stanu ich kopii.
	 */
	public void merge();
}
