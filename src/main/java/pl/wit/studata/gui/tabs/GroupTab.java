/**
 * 
 */
package pl.wit.studata.gui.tabs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import pl.wit.studata.AppData;
import pl.wit.studata.InternalData;
import pl.wit.studata.backend.UniDB;
import pl.wit.studata.backend.models.UniGroup;
import pl.wit.studata.backend.models.UniStudent;
import pl.wit.studata.gui.dialogs.MessageBoxes;
import pl.wit.studata.gui.enums.GroupTableHeaders;
import pl.wit.studata.gui.enums.StudentTableHeaders;
import pl.wit.studata.gui.interfaces.IDatabaseInteractor;
import pl.wit.studata.gui.widgets.FormWidget;
import pl.wit.studata.gui.widgets.TableWidget;

/**
 * Klasa opisująca zakładkę "Groups"
 * @author Jakub Jaworski
 */
public class GroupTab extends JPanel implements IDatabaseInteractor, ActionListener {
private static final long serialVersionUID = 1L;
	
	/**
	 * Panel górny. 
	 * (Wyświetlanie danych)
	 */
	private JPanel pnlTop = null;
	
	/**
	 * Panel dolny.
	 * (Wprowadzanie danych i filtrowanie)
	 */
	private JPanel pnlBot = null;
	
	/**
	 * Podpanel panelu dolnego przechowujący jego karty.
	 */
	private JPanel pnlBotCards = null;
	
	/**
	 * Karta w panelu dolnym, skupiająca się na dodawaniu, aktualizacji i usuwaniu danych.
	 */
	private JPanel pnlCrUpDel = null;
	
	/**
	 * Karta w panelu dolnym, skupiająca się na wyszukiwaniu danych.
	 */
	private JPanel pnlQuery = null;

	/**
	 * Tabela na dane o grupach
	 */
	private TableWidget tblData = null;
	
	/**
	 * Widok z suwakiem, który zwierać będzie tabelę.
	 * @see tblData
	 */
	private JScrollPane scrlTblData = null;
	
	/**
	 * Mapa pola formularza do komponentów formularzowych (formularz tworzenia)
	 */
	private Map<GroupTableHeaders, Component> formMap = null;
	
	/**
	 * Mapa pola formularza do komponentów formularzowych (formularz wyszukiwania
	 */
	private Map<GroupTableHeaders, Component> formQueryMap = null;
	
	/**
	 * Przycisk, którego funkcją jest zatwierdzenie formularza
	 */
	private JButton btnSubmit = null;
	
	/**
	 * Przycisk, którego funkcją jest wczytanie zaznaczonego wiersza z tabeli do formularza.
	 */
	private JButton btnEdit = null;

	/**
	 * Przycisk, którego funkcją jest odznaczenie zaznaczonego wiersza w tabeli.
	 */
	private JButton btnDeselect = null;
	
	/**
	 * Przycisk, którego funkcją jest usunięcie zaznaczonego wiersza z tabeli oraz danych, które reprezentuje ten wiersz z kolekcji w których występuje.
	 */
	private JButton btnDelete = null;
	
	/**
	 * Przycisk, którego funkcją jest dokonanie wyszukiwania według podanych kryteriów.
	 */
	private JButton btnSearch = null;
	
	/**
	 * Przycisk, którego funkcją jest przywrócenie domyślnych wartości kryteriom.
	 */
	private JButton btnClearCriteria = null;
	
	// [Dane]
	
	/**
	 * Lista do przechowywania grup pobranych z bazy danych w danym momencie.
	 */
	private List<UniGroup> groups = null;
	
	/**
	 * Mapa pozwalająca na pilnowanie i porównywanie zmian, celem późniejszego wprowadzenia ich do bazy.
	 */
	private Map<UniGroup, UniGroup> copyToOriginal = null;
	
	/**
	 * Mapa pozwalająca na pilnowanie i porównywanie zmian, celem późniejszego wprowadzenia ich do bazy.
	 */
	private Map<UniGroup, UniGroup> originalToCopy = null;
	
	/**
	 * Zmienna do przechowywania, czy istnieją jakieś modyfikacje, które nie zostały ani zatwierdzone, ani odrzucone.
	 */
	private boolean unsavedChanges = false;
	
	// [Różne]
	
	// Zmienne wewnętrzne
	private static final String CREATE_FORM_STR = "Create";
	private static final String SEARCH_FORM_STR = "Search";
	
	
	/**
	 * Konstruktor bezparametryczny. 
	 */
	public GroupTab() {
		super();
		
		setLayout(new GridLayout(2, 1));
		
		// Panel górny (wyświetlanie)
		pnlTop = new JPanel();
		pnlTop.setBorder(BorderFactory.createTitledBorder("Data"));
		pnlTop.setLayout(new BorderLayout());
		
		String[] headers = Arrays.asList(GroupTableHeaders.values())
				.stream()
				.map(new Function<GroupTableHeaders, String>() {
					@Override
					public String apply(GroupTableHeaders h) {
						return h.getHeaderName();
					}
				}).toArray(String[]::new);
		
		tblData = new TableWidget(headers);
		tblData.setAutoCreateRowSorter(true);
		tblData.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		scrlTblData = new JScrollPane(tblData);
		
		pnlTop.add(scrlTblData, BorderLayout.CENTER);
		
		
		add(pnlTop);
		
		// ----------------------
		
		// Panel dolny (wprowadzanie danych i wyszukiwanie danych)
		pnlBot = new JPanel();
		pnlBot.setBorder(BorderFactory.createTitledBorder("Form"));
		pnlBot.setLayout(new BorderLayout());
		
		JComboBox<String> cmbForm = new JComboBox<String>(new String[] {CREATE_FORM_STR, SEARCH_FORM_STR});
		cmbForm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox source = (JComboBox) e.getSource();
				String cardName = (String) source.getSelectedItem();
				CardLayout cl = (CardLayout) pnlBotCards.getLayout();
				cl.show(pnlBotCards, cardName);
			}
		});
		
		pnlBotCards = new JPanel();
		pnlBotCards.setLayout(new CardLayout());
		
		pnlBot.add(cmbForm, BorderLayout.PAGE_START);
		pnlBot.add(pnlBotCards, BorderLayout.CENTER);
		
		pnlCrUpDel = new JPanel();
		pnlCrUpDel.setLayout(new GridBagLayout());
		GridBagConstraints cPnlCrRupDel = new GridBagConstraints();
		cPnlCrRupDel.insets = new Insets(0, 20, 0, 20);
		
		FormWidget formCreateUpdate = new FormWidget();
		formMap = new LinkedHashMap<>();
		for (GroupTableHeaders header: GroupTableHeaders.values()) {
			String label = header.getHeaderName().concat(": ");
			Component comp = null;
			
			switch (header) {
				case CODE:
				case NAME:
					comp = new JTextField(20);
					break;
				case DESCRIPTION:
					comp = new JScrollPane(new JTextArea()) {{
						Dimension pSize = getPreferredSize();
						pSize.height = 80;
						setPreferredSize(pSize);
					}};
					break;
			}
			
			if (comp != null) {
				formCreateUpdate.addField(label, comp);
				formMap.put(header, comp);
			}
		}
		
		btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(this);
		formCreateUpdate.addWidget(btnSubmit);
		
		pnlCrUpDel.add(formCreateUpdate, cPnlCrRupDel);
		
		// Update-delete frame
		JPanel pnlUpdateDelete = new JPanel();
		pnlUpdateDelete.setLayout(new GridLayout(3, 1));
		pnlUpdateDelete.setBorder(BorderFactory.createTitledBorder("Selection Options"));
		pnlUpdateDelete.setPreferredSize(new Dimension(150, 120));
		
		
		btnEdit = new JButton("Load & Edit");
		btnEdit.addActionListener(this);
		pnlUpdateDelete.add(btnEdit);
		
		btnDeselect = new JButton("Deselect");
		btnDeselect.addActionListener(this);
		pnlUpdateDelete.add(btnDeselect);
		
		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(this);
		btnDelete.setBackground(Color.RED);
		btnDelete.setForeground(Color.WHITE);
		pnlUpdateDelete.add(btnDelete);
		
		++cPnlCrRupDel.gridy;
		pnlCrUpDel.add(pnlUpdateDelete, cPnlCrRupDel);
		pnlBotCards.add(pnlCrUpDel, CREATE_FORM_STR);
		
		// Panel do poszukiwań
		pnlQuery = new JPanel(new GridLayout(1, 1));
		FormWidget formQuery = new FormWidget();
		
		formQueryMap = new LinkedHashMap<>();
		for (GroupTableHeaders header: GroupTableHeaders.values()) {
			String label = header.getHeaderName().concat(": ");
			Component comp = null;
			
			switch (header) {
				case CODE:
				case NAME:
					comp = new JTextField(20);
					break;
				case DESCRIPTION:
					comp = new JScrollPane(new JTextArea(3, 20));
					break;
			default:
				break;
		}
			
			if (comp != null) {
				formQuery.addField(label, comp);
				formQueryMap.put(header, comp);
			}
		}
		
		btnSearch = new JButton("Filter");
		btnSearch.addActionListener(this);
		
		btnClearCriteria = new JButton("Reset Criteria");
		btnClearCriteria.addActionListener(this);
		
		formQuery.addWidget(btnSearch);
		formQuery.addWidget(btnClearCriteria);
		
		Dimension pSize = formQuery.getPreferredSize();
		pSize.height = 200;
		pSize.width = 300;
		formQuery.setPreferredSize(pSize);
	
		pnlQuery.add(formQuery);
		
		pnlBotCards.add(pnlQuery, SEARCH_FORM_STR);
		
		add(pnlBot);
		
		// Czynności po zbudowaniu interfejsu
		update();
	}
	
	/**
	 * Metoda dodająca wiersz do tabeli na podstawie istniejącej Grupy.
	 * @param g Grupa do dodania.
	 */
	private void addTableRow(UniGroup g) {
		if (g == null) return;
		
		Object[] rowData = new Object[] {
			g.getGroupCode(),
			g.getSpecialization(),
			g.getDescription(),
		};
		
		tblData.addDataRow(rowData);
	}
	
	/**
	 * Aktualizuje wiersz w tabeli o zadanym indeksie informacjami o grupie.
	 * @param rowIdx Indeks wiersza.
	 * @param g Grupa.
	 */
	private void updateTableRow(int rowIdx, UniGroup g) {
		if (g == null) return;
		
		Object[] rowData = new Object[] {
			g.getGroupCode(),
			g.getSpecialization(),
			g.getDescription(),
		};
		
		tblData.updateRow(rowIdx, rowData);
	}
	
	/**
	 * Metoda szukająca wiersza, który reprezentuje daną grupę, po jej kodzie.
	 * @param g Grupa.
	 * @return Indeks wiersza w tabeli, który reprezentuje grupę lub -1, jeżeli nic nie znaleziono.
	 */
	private int getRowIdx(UniGroup g) {
		if (g == null)
			return -1;
		
		String groupCode = g.getGroupCode();
		
		return tblData.findRowByCellValue(groupCode, GroupTableHeaders.CODE.ordinal());
	}
	
	/**
	 * Metoda, która na podstawie pobranych wcześniej danych czyści a następnie zapełnia ponownie tablicę.
	 */
	private void updateTable() {
		if (tblData == null) return;
		
		tblData.clear();
		
		if (groups != null) {
			for (UniGroup g: groups) {
				addTableRow(g);
			}
		} 	
	}
	
	/**
	 * Metoda dodająca grupę.
	 * @param code Kod grupy.
	 * @param name Nazwa grupy.
	 * @param desc Opis grupy.
	 * @return true, jeżeli udało się dodać grupę, false jeśli nie.
	 */
	private boolean addGroup(String code, String name, String desc) {
		if (code == null || name == null) return false;
		
		UniGroup g = new UniGroup(code, name, desc);
		groups.add(g);
		addTableRow(g);
		return true;
	}
	
	/**
	 * Metoda usuwa grupę ze wszystkich struktur danych tej klasy.
	 * @param toDelete Grupa do usunięcia
	 * @return true jeżeli grupa została usunięta, false jeśli żadne usunięcie nie nastąpiło.
	 */
	private boolean deleteGroup(UniGroup toDelete) {
		if (toDelete == null)
			return false;
		
		
		if (!groups.contains(toDelete))
			return false;
		
		groups.remove(toDelete);
		return true;
	}
	
	/**
	 * Metoda aktualizuje obiekt grupy w lokalnych strukturach danych.
	 * @param toUpdate Aktualizowana grupa .
	 * @param name Nowa nazwa.
	 * @param desc Nowy opis.
	 * @return true, jeżeli aktualizacja się dokonała, false jeśli nie
	 */
	private boolean updateGroup(UniGroup toUpdate, String name, String desc) {
		if (toUpdate == null)
			return false;
		
		toUpdate.setSpecialization(name);
		toUpdate.setDescription(desc);
		
		int rowIdx = getRowIdx(toUpdate);
		
		updateTableRow(rowIdx, toUpdate);
		
		return true;
	}
	
	/**
	 * Metoda zwracająca grupę o danym kodzie w lokalnych danych, o ile takowa istnieje.
	 * @param code Kod grupy.
	 * @return UniGroup (jeśli istnieje) lub null
	 */
	private UniGroup findByCode(String code) {
		return groups.stream().filter((gr) -> (gr.getGroupCode().equals(code))).findFirst().orElse(null);
	}
	
	private void filterTable() {
		JTextField tfCode = (JTextField) formQueryMap.get(GroupTableHeaders.CODE);
		JTextField tfName = (JTextField) formQueryMap.get(GroupTableHeaders.NAME);
		JTextArea taDesc = (JTextArea) ((JViewport) ((JScrollPane) formQueryMap.get(GroupTableHeaders.DESCRIPTION)).getViewport()).getView();
		
		
		String queryCode = tfCode.getText().trim();
		queryCode = queryCode.isEmpty() ? null : queryCode;
		
		String queryName = tfName.getText().trim();
		queryName = queryName.isEmpty() ? null : queryName;
		
		String queryDesc = (String) taDesc.getText();
		queryDesc = queryDesc.isEmpty() ? null : queryDesc;
		
		updateTable();
		
		List<Integer> rowsToDelete = new ArrayList<Integer>(tblData.getRowCount());
		
		for (int rowIdx = 0; rowIdx < tblData.getRowCount(); ++rowIdx) {
			boolean criteriaMet = true;
			
			Object[] row = tblData.getRow(rowIdx);
			
			if (criteriaMet && queryCode != null) {
				String val = ((String) row[GroupTableHeaders.CODE.ordinal()]).toUpperCase();
				criteriaMet &= val.contains(queryCode.toUpperCase());
			}
			if (criteriaMet && queryName != null) {
				String val = ((String) row[GroupTableHeaders.NAME.ordinal()]).toUpperCase();
				criteriaMet &= val.contains(queryName.toUpperCase());
			}
			if (criteriaMet && queryDesc != null) {
				String val = ((String) row[GroupTableHeaders.DESCRIPTION.ordinal()]).toUpperCase();
				criteriaMet &= val.contains(queryDesc.toUpperCase());
			}
			
			// Oznacz do usunięcia jeśli koniunkcja niespełniona
			if (!criteriaMet) {
				rowsToDelete.add(rowIdx);
			}
		}
		
		tblData.deleteMultipleRows(rowsToDelete);
	}
	
	private void resetFilterCriteria() {
		JTextField tfCode = (JTextField) formQueryMap.get(GroupTableHeaders.CODE);
		JTextField tfName = (JTextField) formQueryMap.get(GroupTableHeaders.NAME);
		JTextArea taDesc = (JTextArea) ((JViewport) ((JScrollPane) formQueryMap.get(GroupTableHeaders.DESCRIPTION)).getViewport()).getView();
		
		tfCode.setText("");
		tfName.setText("");
		taDesc.setText("");
		
		updateTable();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		if (source == btnSubmit) { // Można porównać za pomocą ==, ponieważ sprawdzamy tożsamość obiektu.
			JTextField tfCode = (JTextField) formMap.get(GroupTableHeaders.CODE);
			JTextField tfName = (JTextField) formMap.get(GroupTableHeaders.NAME);
			JTextArea taDesc = (JTextArea) ((JViewport) ((JScrollPane) formMap.get(GroupTableHeaders.DESCRIPTION)).getViewport()).getView();
			
			String code = tfCode.getText();
			String name = tfName.getText();
			String desc = taDesc.getText();
			
			UniGroup queryGroup = findByCode(code);
			
			if (queryGroup == null)
				unsavedChanges |= addGroup(code, name, desc);
			else {
				if (MessageBoxes.showConfirmationBox("Are you sure?", "Are you sure that you want to update group ".concat(queryGroup.toString()).concat("?")))
					unsavedChanges |= updateGroup(queryGroup, name, desc);
			}
		} else if (source == btnDeselect) {
			tblData.clearSelection();
		} else if (source == btnDelete) {
			int selectedIdx = tblData.getSelectedRow();
			if (selectedIdx == -1) {
				MessageBoxes.showInfoBox("No selection!", "Please select a row first!");
				return;
			}
			
			String codeVal = (String) tblData.getValueAt(selectedIdx, GroupTableHeaders.CODE.ordinal());
			
			UniGroup toDelete = findByCode(codeVal);
			if (toDelete == null) return;
			
			if (MessageBoxes.showConfirmationBox("Are you sure?", "Are you sure that you want to DELETE group ".concat(toDelete.toString()).concat("?"))) {
				unsavedChanges |= deleteGroup(toDelete);
				tblData.deleteRow(selectedIdx);
				tblData.clearSelection();
			}
		} else if (source == btnEdit) {
			int selectedIdx = tblData.getSelectedRow();
			if (selectedIdx == -1) {
				MessageBoxes.showInfoBox("No selection!", "Please select a row first!");
				return;
			}
			
			String codeVal = (String) tblData.getValueAt(selectedIdx, GroupTableHeaders.CODE.ordinal());
			
			UniGroup toUpdate = findByCode(codeVal);
			
			if (toUpdate != null) {
				String code = toUpdate.getGroupCode();
				String name = toUpdate.getSpecialization();
				String desc = toUpdate.getDescription();
				
				JTextField tfCode = (JTextField) formMap.get(GroupTableHeaders.CODE);
				JTextField tfName = (JTextField) formMap.get(GroupTableHeaders.NAME);
				JTextArea taDesc = (JTextArea) ((JViewport) ((JScrollPane) formMap.get(GroupTableHeaders.DESCRIPTION)).getViewport()).getView();
				
				tfCode.setText(code);
				tfName.setText(name);
				taDesc.setText(desc);
			}
		} else if (source == btnSearch) {
			filterTable();
		} else if (source == btnClearCriteria) {
			resetFilterCriteria();
			updateTable();
		}

	}

	@Override
	public void pullFromDB() {
		copyToOriginal = new HashMap<>();
		originalToCopy = new HashMap<>();
		groups = new LinkedList<UniGroup>();
		
		UniDB db = InternalData.DATABASE;
		
		List<UniGroup> dbGroupList = null;
		
		// Pobierz listę grup
		synchronized (db) {
			dbGroupList = db.getGroupList();
		}
		
		// Skopiuj listę z bazy do lokalnych
		synchronized (dbGroupList) {
			for (UniGroup gOrig: dbGroupList) {
				UniGroup gCopy = gOrig.deepCopy();
				groups.add(gCopy);
				copyToOriginal.put(gCopy, gOrig);
				originalToCopy.put(gOrig, gCopy);
			}
		}
	}
	
	@Override
	public void merge() {
		List<UniGroup> tempGroups = new ArrayList<>();
		
		for (UniGroup gCopy: groups) {
			UniGroup gOrig = null;
			if (copyToOriginal.containsKey(gCopy)) {
				gOrig = copyToOriginal.get(gCopy);
				gOrig.setSpecialization(gCopy.getSpecialization());
				gOrig.setDescription(gCopy.getDescription());
			}
			
			if (gOrig == null)
				tempGroups.add(gCopy);
			else
				tempGroups.add(gOrig);
		}
		
		groups = tempGroups;
	}
	
	@Override
	public void pushToDB() {
		merge();
		List<UniGroup> groupsToDB = new ArrayList<UniGroup>(groups);
		
		Thread tGroups = new Thread(() -> {
			UniDB db = InternalData.DATABASE;
			synchronized(db) {
				db.updateGroups(groupsToDB);
			}
		});
		
		InternalData.EXECUTOR.execute(tGroups);
		
		try {
			tGroups.join();
			unsavedChanges = false;
			update();
		} catch (InterruptedException ex) {
			StringBuilder sb = new StringBuilder("Something went wrong saving changes to the database!");
			sb.append('\n').append(ex.getMessage());
			MessageBoxes.showErrorBox("Error!", sb.toString());
		}

	}

	@Override
	public boolean hasUnsavedChanges() {
		return unsavedChanges;
	}

	@Override
	public void update() {
		pullFromDB();
		updateTable();
	}

	@Override
	public void nullifyChanges() {
		unsavedChanges = false;
		update();
	}

}
