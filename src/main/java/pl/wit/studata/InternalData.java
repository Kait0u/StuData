/**
 * 
 */
package pl.wit.studata;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.wit.studata.backend.UniDB;

/**
 * Klasa przechowująca wewnętrzne referencje do obiektów, od których wymaga się, by móc uzyskać do nich referencję z każdego miejsca w programie. 
 * @author Jakub Jaworski
 */
public class InternalData {

	/**
	 * Referencja do obiektu bazy danych.
	 */
	public static UniDB DATABASE = null;
	
	/**
	 * Referencja do egzekutora.
	 */
	public static ExecutorService EXECUTOR = null;
	
	/**
	 * Metoda ustalająca wartości statycznych pól klasy, korzystając jak najwięcej z obiektu Config.
	 */
	public static void setupFromConfig() {
		DATABASE = new UniDB();
		EXECUTOR = Executors.newFixedThreadPool(Config.THREADPOOL_SIZE);
	}

}
