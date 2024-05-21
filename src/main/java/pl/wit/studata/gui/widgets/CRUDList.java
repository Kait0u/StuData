package pl.wit.studata.gui.widgets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

/**
 * Klasa reprezentująca widżet składający się z listy oraz przycisków dodawania, edycji i usuwania zeń elementów.
 */
public class CRUDList extends JPanel {
    private static final long serialVersionUID = 1L;
	
    private DefaultListModel<Object> listModel;
    private JList<Object> list;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    /**
     * Konstruktor bezparametryczny.
     */
    public CRUDList() {
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(list);
        add(scrollPane, BorderLayout.CENTER);

        // Create the panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3, 0, 0));

        addButton = new JButton("ADD");
        editButton = new JButton("EDIT");
        deleteButton = new JButton("REMOVE");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultButtonActions();
    }

    /**
     * Pobierz zawartość listy jako obiekt typu List.
     * @return Zawartość listy jako obiekt typu List
     */
    public List<Object> getListItems() {
        List<Object> items = new ArrayList<>();
        for (int i = 0; i < listModel.size(); i++) {
            items.add(listModel.getElementAt(i));
        }
        return items;
    }

    /**
     * Usuń wszystkie elementy z listy.
     */
    public void clearList() {
        listModel.clear();
    }

    /**
     * Dodaj nowy wpis do listy.
     * @param item Byt do dodania.
     */
    public void addItem(Object item) {
        listModel.addElement(item);
    }
    
    /**
     * Pobiera indeks obiektu reprezentowanego przez obecnie zaznaczony wpis.
     * @return Indeks zaznaczonego obiektu lub -1, jeśli nic nie jest zaznaczone.
     */
    public int getSelectedItemIdx() {
    	int selectedIndex = list.getSelectedIndex();
    	return selectedIndex;
    }
    
    /**
     * Pobiera obiekt reprezentowany przez obecnie zaznaczony wpis.
     * @return Zaznaczony obiekt lub null, jeśli nic nie jest zaznaczone.
     */
    public Object getSelectedItem() {
    	Object result = null;
    	int selectedIndex = getSelectedItemIdx();
    	if (selectedIndex != -1) {
    		result = listModel.getElementAt(selectedIndex);
    	}
    	return result;
    }
   

    /**
     * Domyślna metoda do edytowania wpisu (wpis będzie typu String)
     */
    public void editSelectedItem() {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex != -1) {
            String newValue = JOptionPane.showInputDialog(this, "Edit Item", listModel.getElementAt(selectedIndex));
            if (newValue != null) {
                listModel.setElementAt(newValue, selectedIndex);
            }
        }
    }
    
    public void updateSelectedItem(Object newValue) {
    	int selectedIndex = list.getSelectedIndex();
    	if (newValue != null && selectedIndex >= 0) {
    		listModel.setElementAt(newValue, selectedIndex);
    	}
    }

    /**
     * Metoda do usuwania zaznaczonego wpisu.
     */
    public void deleteSelectedItem() {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex != -1) {
            listModel.removeElementAt(selectedIndex);
        }
    }

    /**
     * Metoda ustawiająca domyślne czynności dla przycisków.
     */
    private void setDefaultButtonActions() {
        addButton.addActionListener(e -> addItem("New Item"));
        editButton.addActionListener(e -> editSelectedItem());
        deleteButton.addActionListener(e -> deleteSelectedItem());
    }

    /**
     * Metoda ustawiająca czynność przyciskowi dodawania.
     * @param action Czynność.
     */
    public void setAddButtonAction(ActionListener action) {
        for (ActionListener al : addButton.getActionListeners()) {
            addButton.removeActionListener(al);
        }
        addButton.addActionListener(action);
    }

    /**
     * Metoda ustawiająca czynność przyciskowi edycji.
     * @param action Czynność.
     */
    public void setEditButtonAction(ActionListener action) {
        for (ActionListener al : editButton.getActionListeners()) {
            editButton.removeActionListener(al);
        }
        editButton.addActionListener(action);
    }

    /**
     * Metoda ustawiająca czynność przyciskowi usuwania.
     * @param action Czynność.
     */
    public void setDeleteButtonAction(ActionListener action) {
        for (ActionListener al : deleteButton.getActionListeners()) {
            deleteButton.removeActionListener(al);
        }
        deleteButton.addActionListener(action);
    }
}
