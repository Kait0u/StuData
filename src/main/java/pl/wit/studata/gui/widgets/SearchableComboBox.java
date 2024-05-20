package pl.wit.studata.gui.widgets;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxEditor;

/**
 * Klasa opisująca widżet JComboBox poszerzony o możliwość wyszukiwania.
 * @param <T> Typ danych
 * @author Jakub Jaworski
 */
public class SearchableComboBox<T> extends JComboBox<T> {
    private static final long serialVersionUID = 1L;
	
    private JTextField editorComponent;

    /**
     * Konstruktor parametryczny przyjmujący pewną kolekcję elementów typu T. 
     * @param items Kolekcja obiektów.	
     */
    public SearchableComboBox(Collection<T> items) {
        super(new Vector<T>(items));
        
        setEditable(true);
        setEditor(new BasicComboBoxEditor());
        editorComponent = (JTextField) getEditor().getEditorComponent();
        editorComponent.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = editorComponent.getText();
                filterList(text);
            }
        });
    }

    private void filterList(String text) {
        Vector<T> filteredItems = new Vector<>();
        for (int i = 0; i < getItemCount(); i++) {
            T item = getItemAt(i);
            if (item.toString().toLowerCase().contains(text.toLowerCase())) {
                filteredItems.add(item);
            }
        }
        setModel(new DefaultComboBoxModel<>(filteredItems));
        editorComponent.setText(text);
        setPopupVisible(true);
    }

    @Override
    public void setSelectedItem(Object item) {
        super.setSelectedItem(item);
        editorComponent.setText(item == null ? "" : item.toString());
    }

}
