/**
 * 
 */
package pl.wit.studata;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;

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
	 * Lokalizacja (ścieżka) bazy danych.
	 */
	public static String DATABASE_PATH = null;
	
	/**
	 * Odniesienie do pliku bazy danych.
	 */
	
	public static File DATABASE_FILE = null;
	
	/**
	 * Referencja do egzekutora.
	 */
	public static ExecutorService EXECUTOR = null;
	
	/**
	 * Lista otwartych okienek.
	 */
	public static List<JFrame> WINLIST = null;
	
	/**
	 * Metoda inicjalizująca pola, które nie wymagają informacji zewnętrznych do inicjalizacji.
	 */
	private static void generalSetup() {
		WINLIST = new LinkedList<JFrame>();
	}
	
	/**
	 * Metoda ustalająca wartości statycznych pól klasy, korzystając jak najwięcej z obiektu Config.
	 */
	public static void setupFromConfig() {
		generalSetup();
		
		EXECUTOR = Executors.newFixedThreadPool(Config.THREADPOOL_SIZE);
		
		// Postaw bazę danych.
		DATABASE = new UniDB();
		
		DATABASE_PATH = Config.DB_PATH;
		DATABASE_FILE = new File(DATABASE_PATH);
		DATABASE_FILE.getParentFile().mkdirs();
		if (!DATABASE_FILE.exists()) {
			try {
				DATABASE_FILE.createNewFile();
			} catch (IOException e) {
			}
		}
		
		try {
			DATABASE.loadFromFile(DATABASE_FILE.getAbsolutePath());
		} catch (Exception e) {
			
		}
		try {
			
		} catch (Exception e) {
		}
	}

	/**
	 * Dodaje obiekt okienka do listy otwartych okienek.
	 * @param window
	 */
	public static void registerWindow(JFrame window) {
		if (window != null)
			WINLIST.add(window);
	}
	
	/**
	 * Usuwa wszystkie okienka.
	 */
	public static void destroyAllWindows() {
		Collections.reverse(WINLIST);
		
		for (JFrame window: WINLIST) {
			window.dispose();
		}
		
		WINLIST.clear();
	}
	
	public static void destroyWindow(JFrame window) {
		if (window != null && WINLIST.contains(window)) {
			WINLIST.remove(window);
			window.dispose();
		}
	}
	
	
}
