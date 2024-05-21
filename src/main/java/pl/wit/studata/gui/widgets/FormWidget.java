/**
 * 
 */
package pl.wit.studata.gui.widgets;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 * Klasa opisująca widżet formularza, który może zajmować różne inne widżety sparowane z etykietą.
 */
public class FormWidget extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Menedżer układu
	 */
	private GridBagLayout gbLayout = null;
	
	/**
	 * Reguły menedżera układu
	 */
	private GridBagConstraints constr = null;
	
	/**
	 * Konstruktor bezparametryczny.
	 */
	public FormWidget() {
		gbLayout = new GridBagLayout();
		constr = new GridBagConstraints();
		
		setLayout(gbLayout);
		constr.insets = new Insets(5, 5, 5, 5);
	}
	
	/**
	 * Konstruktor parametryczny przyjmujący mapę z etykiety w komponent, który wyświetlony ma zostać w stylu formularzowym.
	 * @param labelsWidgets
	 */
	public FormWidget(Map<String, Component> labelsWidgets) {
		this();
		
		for (Map.Entry<String, Component> pair: labelsWidgets.entrySet())
			addField(pair.getKey(), pair.getValue());
	}
	
	/**
	 * Metoda służąca do dodania wiersza o danej etykiecie i danym widżecie. Należy określić, czy widżet ma wypełniać przestrzeń, czy nie.
	 * @param label
	 * @param widget
	 * @param componentFill
	 */
	public void addField(String label, Component widget, boolean componentFill) {
		constr.anchor = GridBagConstraints.EAST;
        constr.gridx = 0;
        ++constr.gridy;
        add(new JLabel(label), constr);

        constr.anchor = GridBagConstraints.WEST;
        constr.gridx = 1;
        
        int currFill = constr.fill;
        if (componentFill)
        	constr.fill = GridBagConstraints.BOTH;
        
        add(widget, constr);
        
        constr.fill = currFill;
	}
	
	/**
	 * Metoda służąca do dodania wiersza o danej etykiecie i danym widżecie, z wypełnieniem.
	 * @param label
	 * @param widget
	 */
	public void addField(String label, Component widget) {
		addField(label, widget, true);
	}
	
	/**
	 * Metoda służąca do dodania wiersza składającego się z pojedynczego widżetu wypełniającego całą przestrzeń wiersza.
	 * @param widget
	 */
	public void addWidget(Component widget) {
		++constr.gridy;
		constr.anchor = GridBagConstraints.CENTER;
        constr.gridx = 0;
        constr.gridwidth = GridBagConstraints.REMAINDER;
        constr.fill = GridBagConstraints.BOTH;
        add(widget, constr);
	}
	
	/**
	 * Metoda dodająca separator poziomy całą długość nowego wiersza.
	 */
	public void addSeparator() {
		JSeparator sep = new JSeparator();
		sep.setOrientation(JSeparator.HORIZONTAL);
		
		++constr.gridy;
		constr.anchor = GridBagConstraints.CENTER;
        constr.gridx = 0;
        constr.gridwidth = GridBagConstraints.REMAINDER;
        constr.fill = GridBagConstraints.HORIZONTAL;
        add(sep, constr);
	}

}
