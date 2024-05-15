package pl.wit.studata;

import javax.swing.SwingUtilities;

import pl.wit.studata.gui.MainWindow;


/**
 * Klasa zawierająca punkt wejściowy do programu.
 */
public class Main {
	
	/**
	 * Punkt wejściowy programu
	 * @param args
	 */
	public static void main(String[] args) {
		Initializer.initialize();
		
		SwingUtilities.invokeLater(new Runnable() {
	         @Override
	         public void run() {
	            new MainWindow();
	         }
	      });
	}
}
