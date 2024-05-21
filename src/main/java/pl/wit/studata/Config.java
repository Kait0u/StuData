/**
 * 
 */
package pl.wit.studata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Klasa konfiguracyjna aplikacji.
 * 
 * @author Jakub Jaworski
 */
public class Config {
	public static int THREADPOOL_SIZE = 4;
	public static String DB_PATH = "./database/data.studata";

	private static final String CFG_PATH = "./config.cfg";

	static {
		checkCreateFile();
	}

	/**
	 * Metoda wczytuje config z pliku konfiguracyjnego.
	 */
	public static void loadFromFile() {
		File file = new File(CFG_PATH);

		if (file.exists()) {
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				while (br.ready()) {
					String line = br.readLine();
					String[] splitLine = line.split("=");
					String varName = splitLine[0];
					String varVal = splitLine[1];

					switch (varName) {
					case "THREADPOOL_SIZE":
						THREADPOOL_SIZE = Integer.valueOf(varVal);
						break;
					case "DB_PATH":
						if (validateDbPath(varVal))
							DB_PATH = varVal;
						break;
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private static boolean validateDbPath(String varVal) {
		return !(new File(varVal).isDirectory()) && varVal.endsWith(".studata") && !varVal.startsWith(".studata");
	}

	private static void checkCreateFile() {
		File file = new File(CFG_PATH);

		if (!file.exists()) {
			try {
				file.createNewFile();
				saveToFile(file);
			} catch (IOException e) {

			}
		}
	}
	
	/**
	 * Zapisuje config do pliku
	 * @param file Obiekt wskazujący na plik.
	 */
	private static void saveToFile(File file) {
		if (file == null) return;
		
		try (PrintStream s = new PrintStream(file)) {
			// Dopisz pola do pliku
			s.println("THREADPOOL_SIZE=".concat(Integer.valueOf(THREADPOOL_SIZE).toString()));
			s.println("DB_PATH=".concat(DB_PATH));
		
		} catch (FileNotFoundException e) {}
	}
	
	/**
	 * Zapisuje config do pliku.
	 */
	public static void saveToFile() {
		File file = new File(CFG_PATH);
		saveToFile(file);
	}
}
