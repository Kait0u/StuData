/**
 * 
 */
package pl.wit.studata.gui;

import javax.swing.JOptionPane;

/**
 * Klasa o metodach statycznych służących do powoływana okienek dialogowych.
 * Jej cel to ograniczenie konstruowania tych okienek wewnątrz innych metod, poprzez zaoferowanie prekonfigurowanych możliwości. 
 */
public class MessageBoxes {
	/**
	 * Powołuje okienko dialogowe do celów informacyjnych.
	 * @param title Tytuł wiadomości.
	 * @param message Treść wiadomości.
	 */
	public static void showInfoBox(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Powołuje okienko dialogowe do celów informacji o błędzie.
	 * @param title Tytuł wiadomości.
	 * @param message Treść wiadomości.
	 */
	public static void showErrorBox(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Powołuje okienko dialogowe oczekujące potwierdzenia
	 * @param title Tytuł wiadomości.
	 * @param message Treść wiadomości.
	 * @return true, jeżeli użytkownik wybrał opcję YES/TAK, false w przeciwnym przypadku.
	 */
	public static boolean showConfirmationBox(String title, String message) {
		boolean result = false;
		result = (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE));
		return result;
	}
}
