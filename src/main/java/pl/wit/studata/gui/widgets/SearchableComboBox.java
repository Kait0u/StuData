package pl.wit.studata.gui.widgets;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
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
	
    private JTextField editorComponent = null;
    private List<T> listItems = null; 

    /**
     * Konstruktor parametryczny przyjmujący pewną kolekcję elementów typu T. 
     * @param items Kolekcja obiektów.	
     */
    public SearchableComboBox(Collection<T> items) {
        super();
        listItems = new LinkedList<>();
        
        if (items != null)
	        for (T item: items) {
	        	addItem(item);
	        }
        
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
        for (T item: listItems) {
        	if (item == null) continue;
            if (item.toString().toLowerCase().contains(text.toLowerCase())) {
                filteredItems.add(item);
            }
        }
        setModel(new DefaultComboBoxModel<>(filteredItems));
        editorComponent.setText(text);
        setPopupVisible(true);
    }
    
    @Override
    public void addItem(T item) {
    	listItems.add(item);
    	super.addItem(item);
    }
    
    @Override
    public void removeItem(Object item) {
    	listItems.remove(item);
    	super.removeItem(item);
    }
    
    @Override
    public void removeAllItems() {
    	listItems.clear();
    	super.removeAllItems();
    	addItem(null);
    }

    @Override
    public void setSelectedItem(Object item) {
        super.setSelectedItem(item);
        editorComponent.setText(item == null ? "" : item.toString());
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.width = Math.max(size.width, 200);
        return size;
    }
    
    @Override
    public Dimension getMinimumSize() {
        Dimension size = super.getMinimumSize();
        size.width = Math.max(size.width, 200);
        return size;
    }
    
    @Override
    public T getSelectedItem() {
    	T result = null;
    	try {
    		result = (T) super.getSelectedItem();
    	} catch (Exception ex) {}
    	return result;
    }

}
