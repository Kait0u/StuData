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
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;

import pl.wit.studata.AppData;
import pl.wit.studata.InternalData;
import pl.wit.studata.backend.UniDB;
import pl.wit.studata.backend.models.UniGroup;
import pl.wit.studata.backend.models.UniStudent;
import pl.wit.studata.gui.MessageBoxes;
import pl.wit.studata.gui.enums.StudentTableHeaders;
import pl.wit.studata.gui.interfaces.IDatabasePusher;
import pl.wit.studata.gui.widgets.FormWidget;
import pl.wit.studata.gui.widgets.TableWidget;

/**
 * Klasa opisująca zakładkę "Student"
 * @author Jakub Jaworski
 */
public class StudentTab extends JPanel implements ActionListener, IDatabasePusher {

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
	 * Tabela na dane o studentach
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
	private Map<StudentTableHeaders, Component> formMap = null;
	
	/**
	 * Mapa pola formularza do komponentów formularzowych (formularz wyszukiwania
	 */
	private Map<StudentTableHeaders, Component> formQueryMap = null;
	
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
	 * Lista do przechowywania studentów pobranych z bazy danych w danym momencie.
	 */
	private List<UniStudent> students = null;
	
	/**
	 * Lista do przechowywania grup pobranych z bazy danych w danym momencie.
	 */
	private List<UniGroup> groups = null;
	
	/**
	 * Mapa do przechowywania przypisań studenta do danej grupy.
	 */
	private Map<UniStudent, UniGroup> groupAssignments = null;
	
	/**
	 * Zmienna do przechowywania, czy istnieją jakieś modyfikacje, które nie zostały ani zatwierdzone, ani odrzucone.
	 */
	private boolean unsavedChanges = false;
	
	// [Różne]
	
	// Zmienne wewnętrzne do 
	private static final String CREATE_FORM_STR = "Create";
	private static final String SEARCH_FORM_STR = "Search";
	
	/**
	 * Konstruktor bezparametryczny. 
	 */
	public StudentTab() {
		super();
		
		setLayout(new GridLayout(2, 1));
		
		// Panel górny (wyświetlanie)
		pnlTop = new JPanel();
		pnlTop.setBorder(BorderFactory.createTitledBorder("Data"));
		pnlTop.setLayout(new BorderLayout());
		
		String[] headers = Arrays.asList(StudentTableHeaders.values())
				.stream()
				.map(new Function<StudentTableHeaders, String>() {
					@Override
					public String apply(StudentTableHeaders h) {
						return h.getHeaderName();
					}
				}).toArray(String[]::new);
		
		tblData = new TableWidget(headers);
		
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
		for (StudentTableHeaders header: StudentTableHeaders.values()) {
			String label = header.getHeaderName().concat(": ");
			Component comp = null;
			
			switch (header) {
				case ID:
					comp = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
					break;
				case FNAME:
				case LNAME:
					comp = new JTextField(20);
					break;
				case GROUP:
					comp = new JComboBox<String>();
					
					@SuppressWarnings("unchecked") 
					JComboBox<String> combo = (JComboBox<String>) comp;
					if (groups != null) 
						for (UniGroup g: groups) {
							combo.addItem(g.getGroupCode());
						}
					combo.addItem(AppData.NONE_TEXT);
					combo.setSelectedIndex(combo.getItemCount() - 1);
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
		pnlQuery = new JPanel();
		FormWidget formQuery = new FormWidget();
		
		formQueryMap = new LinkedHashMap<>();
		for (StudentTableHeaders header: StudentTableHeaders.values()) {
			String label = header.getHeaderName().concat(": ");
			Component comp = null;
			
			switch (header) {
				case ID:
					comp = new JSpinner(new SpinnerNumberModel(-1, -1, Integer.MAX_VALUE, 1));
					break;
				case FNAME:
				case LNAME:
					comp = new JTextField(20);
					break;
				case GROUP:
					comp = new JComboBox<String>();
					
					@SuppressWarnings("unchecked") 
					JComboBox<String> combo = (JComboBox<String>) comp;
					if (groups != null) 
						for (UniGroup g: groups) {
							combo.addItem(g.getGroupCode());
						}
					combo.addItem(AppData.NONE_TEXT);
					combo.addItem(AppData.ANY_TEXT);
					combo.setSelectedIndex(combo.getItemCount() - 1);
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
		
		pnlQuery.add(formQuery);
		
		pnlBotCards.add(pnlQuery, SEARCH_FORM_STR);
		
		add(pnlBot);
		
		// Czynności po zbudowaniu interfejsu
		update();
	}
	
	/**
	 * Metoda, która ustawia struktury danych na nowe, po czym pobiera dane z obiektu bazy danych.
	 */
	private void pullDataFromDB() {
		students = new LinkedList<UniStudent>();
		groups = new LinkedList<UniGroup>();
		groupAssignments = new HashMap<UniStudent, UniGroup>();
		
		UniDB db = InternalData.DATABASE;
		
		List<UniStudent> dbStudentList = null;
		List<UniGroup> dbGroupList = null;
		Map<UniGroup, List<UniStudent>> dbGroupAssignments = null;
		
		// Pobierz listę studentów i mapę przypisań z bazy danych.
		synchronized (db) {
			dbStudentList = db.getStudentList();
			dbGroupList = db.getGroupList();
			dbGroupAssignments = db.getGroupStudentMap();
		}
		
		// Skopiuj listy z bazy do list lokalnych
		synchronized (dbStudentList) {
			students.addAll(dbStudentList);
		}
		synchronized (dbGroupList) {
			groups.addAll(dbGroupList);
		}
		
		// Zbuduj mapę przypisań
		synchronized (dbGroupAssignments) {
			for (UniStudent s: students) {
				UniGroup group = null;
				for (Map.Entry<UniGroup, List<UniStudent>> entry: dbGroupAssignments.entrySet()) {
					UniGroup g = entry.getKey();
					List<UniStudent> groupMembers = entry.getValue();
					if (groupMembers.contains(s)) {
						group = g;
						break;
					}
				}
				groupAssignments.put(s, group);
			}
		}
	}
	
	/**
	 * Metoda dodająca wiersz do tabeli na podstawie istniejącego Studenta i Grupy.
	 * @param s Student do dodania.
	 * @param g Grupa z którą skojarzony jest student.
	 */
	private void addTableRow(UniStudent s, UniGroup g) {
		if (s == null) return;
		
		Object[] rowData = new Object[] {
			s.getStudentId(),
			s.getFirstName(),
			s.getLastName(),
			g != null ? g.getGroupCode() : AppData.NONE_TEXT
		};
		
		tblData.addDataRow(rowData);
	}
	
	/**
	 * Aktualizuje wiersz w tabeli o zadanym indeksie informacjami o studencie i grupie.
	 * @param rowIdx
	 */
	private void updateTableRow(int rowIdx, UniStudent s, UniGroup g) {
		if (s == null) return;
		
		Object[] rowData = new Object[] {
				s.getStudentId(),
				s.getFirstName(),
				s.getLastName(),
				g != null ? g.getGroupCode() : AppData.NONE_TEXT
		};
		
		tblData.updateRow(rowIdx, rowData);
	}
	
	/**
	 * Metoda szukająca wiersza, który reprezentuje danego studenta, po jego ID.
	 * @param s Student.
	 * @return Indeks wiersza w tabeli, który reprezentuje studenta lub -1, jeżeli nic nie znaleziono.
	 */
	private int getRowIdx(UniStudent s) {
		if (s == null)
			return -1;
		
		int studentId = s.getStudentId();
		
		return tblData.findRowByCellValue(studentId, StudentTableHeaders.ID.ordinal());
	}
	
	/**
	 * Metoda, która na podstawie pobranych wcześniej danych czyści a następnie zapełnia ponownie tablicę.
	 */
	private void updateTable() {
		if (tblData == null) return;
		
		tblData.clear();
		
		if (students != null) {
			for (UniStudent s: students) {
				addTableRow(s, groupAssignments.get(s));
			}
		} 
			
	}
	
	/**
	 * Metoda, która uaktualnia spinner liczbowy do wyboru ID, proponując kolejne.
	 */
	private void updateIdSpinner() {
		JSpinner spnId = (JSpinner) formMap.get(StudentTableHeaders.ID);
		int maxId = students.stream().map((s) -> s.getStudentId()).max(Integer::compare).orElse(-1) + 1;
		spnId.setModel(new SpinnerNumberModel(maxId, 0, Integer.MAX_VALUE, 1));
	}
	
	/**
	 * Metoda, która uaktualnia opcje oferowane przez selektor grupy.
	 */
	private void updateGroupCombobox() {
		@SuppressWarnings("unchecked")
		JComboBox<String> cmbGroup = (JComboBox<String>) formMap.get(StudentTableHeaders.GROUP);
		cmbGroup.removeAllItems();
		
		if (groups != null) 
			for (UniGroup g: groups) {
				cmbGroup.addItem(g.getGroupCode());
			}
		cmbGroup.addItem(AppData.NONE_TEXT);
		cmbGroup.setSelectedIndex(cmbGroup.getItemCount() - 1);
	}
	
	/**
	 * Metoda, która na podstawie pobranych wcześniej danych odpowiednio resetuje a następnie uaktualnia widżety formularza.
	 */
	private void updateWidgets() {
		updateIdSpinner();
		updateGroupCombobox();
	}
	
	/**
	 * Metoda dokonująca pełnego odświeżenia zasobów
	 */
	public void update() {
		pullDataFromDB();
		updateTable();
		updateWidgets();
	}
	
	/**
	 * Metoda dodająca studenta, o ile student o takim ID już nie istnieje
	 * @param id ID studenta.
	 * @param firstName Imię studenta.
	 * @param lastName Nazwisko studenta.
	 * @param group Grupa do której należy student (może być null).
	 * @return true, jeżeli udało się dodać studenta, false jeśli nie.
	 */
	private boolean addStudent(int id, String firstName, String lastName, UniGroup group) {
		if (firstName == null || lastName == null) return false;
		
		UniStudent s = new UniStudent(firstName, lastName, id);
		
		students.add(s);
		
		if (group != null)
			groupAssignments.put(s, group);
		
		addTableRow(s, group);
		updateIdSpinner();
		
		return true;
	}
	
	/**
	 * Metoda usuwa studenta ze wszystkich struktur danych tej klasy.
	 * @param toDelete
	 * @return true jeżeli student został usunięty, false jeśli żadne usunięcie nie nastąpiło.
	 */
	private boolean deleteStudent(UniStudent toDelete) {
		if (toDelete == null)
			return false;
		
		groupAssignments.remove(toDelete);
		
		if (!students.contains(toDelete))
			return false;
		
		students.remove(toDelete);
		
		return true;
	}
	
	/**
	 * Metoda aktualizuje obiekt studenta oraz wszystkie jego wystąpienia w lokalnych strukturach danych.
	 * @param toUpdate Aktualizowany student 
	 * @param firstName Nowe imię
	 * @param lastName Nowe nazwisko
	 * @param group Nowa grupa
	 * @return true, jeżeli aktualizacja się dokonała, false jeśli nie
	 */
	private boolean updateStudent(UniStudent toUpdate, String firstName, String lastName, UniGroup group) {
		if (toUpdate == null)
			return false;
		
		toUpdate.setFirstName(firstName);
		toUpdate.setLastName(lastName);
		
		if (group != null)
			groupAssignments.put(toUpdate, group);
		else
			groupAssignments.remove(toUpdate);
		
		int rowIdx = getRowIdx(toUpdate);
		
		updateTableRow(rowIdx, toUpdate, group);
		
		return true;
	}
	
	/**
	 * Metoda zwracająca studenta o danym ID w lokalnych danych, o ile taki student istnieje.
	 * @param id ID studenta
	 * @return UniStudent (jeśli istnieje) lub null
	 */
	private UniStudent findById(int id) {
		return students.stream().filter((stud) -> (stud.getStudentId() == id)).findFirst().orElse(null);
	}
	
	private void filterTable() {
		JSpinner spnId = (JSpinner) formQueryMap.get(StudentTableHeaders.ID); 
		JTextField tfFirstName = (JTextField) formQueryMap.get(StudentTableHeaders.FNAME);
		JTextField tfLastName = (JTextField) formQueryMap.get(StudentTableHeaders.LNAME);
		JComboBox<String> cmbGroup = (JComboBox<String>) formQueryMap.get(StudentTableHeaders.GROUP);
		
		Integer idQuery = (Integer) spnId.getValue();
		idQuery = idQuery > -1 ? idQuery : null;
		
		String queryFName = tfFirstName.getText().trim();
		queryFName = queryFName.isEmpty() ? null : queryFName;
		
		String queryLName = tfLastName.getText().trim();
		queryLName = queryLName.isEmpty() ? null : queryLName;
		
		String queryGroup = (String) cmbGroup.getSelectedItem();
		queryGroup = queryGroup.equals(AppData.ANY_TEXT) ? null : queryGroup;
		
		updateTable();
		
		List<Integer> rowsToDelete = new ArrayList<Integer>(tblData.getRowCount());
		
		for (int rowIdx = 0; rowIdx < tblData.getRowCount(); ++rowIdx) {
			boolean criteriaMet = true;
			if (criteriaMet && idQuery != null) {
				Integer val = (Integer) tblData.getValueAt(rowIdx, StudentTableHeaders.ID.ordinal());
				criteriaMet &= val.equals(idQuery);
			}
			if (criteriaMet && queryFName != null) {
				String val = ((String) tblData.getValueAt(rowIdx, StudentTableHeaders.FNAME.ordinal())).toUpperCase();
				criteriaMet &= val.contains(queryFName.toUpperCase());
			}
			if (criteriaMet && queryLName != null) {
				String val = ((String) tblData.getValueAt(rowIdx, StudentTableHeaders.LNAME.ordinal())).toUpperCase();
				criteriaMet &= val.contains(queryLName.toUpperCase());
			}
			if (criteriaMet && queryGroup != null) {
				String val = ((String) tblData.getValueAt(rowIdx, StudentTableHeaders.GROUP.ordinal())).toUpperCase();
				criteriaMet &= val.equals(queryGroup);
			}
			
			// Oznacz do usunięcia jeśli koniunkcja niespełniona
			if (!criteriaMet) {
				rowsToDelete.add(rowIdx);
			}
		}
		
		tblData.deleteMultipleRows(rowsToDelete);
	}
	
	private void resetFilterCriteria() {
		JSpinner spnId = (JSpinner) formQueryMap.get(StudentTableHeaders.ID); 
		JTextField tfFirstName = (JTextField) formQueryMap.get(StudentTableHeaders.FNAME);
		JTextField tfLastName = (JTextField) formQueryMap.get(StudentTableHeaders.LNAME);
		JComboBox<String> cmbGroup = (JComboBox<String>) formQueryMap.get(StudentTableHeaders.GROUP);
		
		spnId.setValue(-1);
		tfFirstName.setText("");
		tfLastName.setText("");
		cmbGroup.setSelectedItem(AppData.ANY_TEXT);
		
		updateTable();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnSubmit) { // Można porównać za pomocą ==, ponieważ sprawdzamy tożsamość obiektu.
			
			Integer id = (Integer) ((JSpinner) formMap.get(StudentTableHeaders.ID)).getValue();
			String firstName = ((JTextField) formMap.get(StudentTableHeaders.FNAME)).getText().trim();
			String lastName = ((JTextField) formMap.get(StudentTableHeaders.LNAME)).getText().trim();
			
			@SuppressWarnings("unchecked")
			int groupIdx = ((JComboBox<String>) formMap.get(StudentTableHeaders.GROUP)).getSelectedIndex();
			UniGroup group = groupIdx >= groups.size() ? null : groups.get(groupIdx);
			
			UniStudent queryStudent = findById(id);
			if (queryStudent == null)
				unsavedChanges |= addStudent(id, firstName, lastName, group);
			else {
				// TODO Zapytaj się użytkownika, czy na pewno!
				if (MessageBoxes.showConfirmationBox("Are you sure?", "Are you sure that you want to update student ".concat(queryStudent.toString()).concat("?")))
					unsavedChanges |= updateStudent(queryStudent, firstName, lastName, group);
				
			}
		} else if (source == btnDeselect) {
			tblData.clearSelection();
			updateWidgets();
		} else if (source == btnDelete) {
			int selectedIdx = tblData.getSelectedRow();
			if (selectedIdx == -1) return;
			int idVal = (Integer) tblData.getValueAt(selectedIdx, StudentTableHeaders.ID.ordinal());
			
			UniStudent toDelete = findById(idVal);
			if (toDelete == null) return;
			
			if (MessageBoxes.showConfirmationBox("Are you sure?", "Are you sure that you want to DELETE student ".concat(toDelete.toString()).concat("?"))) {
				
			}
			unsavedChanges |= deleteStudent(toDelete);
			tblData.deleteRow(selectedIdx);
			tblData.clearSelection();
			updateIdSpinner();
		} else if (source == btnEdit) {
			int selectedIdx = tblData.getSelectedRow();
			if (selectedIdx == -1) return;
			int idVal = (Integer) tblData.getValueAt(selectedIdx, StudentTableHeaders.ID.ordinal());
			UniStudent toUpdate = findById(idVal);
			
			if (toUpdate != null) {
				Integer studentId = toUpdate.getStudentId();
				String firstName = toUpdate.getFirstName();
				String lastName = toUpdate.getLastName();
				UniGroup group = groupAssignments.getOrDefault(toUpdate, null);
				int groupIdx = (group != null && groups != null) ? groups.indexOf(group) : 0;
				
				JSpinner spnId = (JSpinner) formMap.get(StudentTableHeaders.ID); 
				JTextField tfFirstName = (JTextField) formMap.get(StudentTableHeaders.FNAME);
				JTextField tfLastName = (JTextField) formMap.get(StudentTableHeaders.LNAME);
				JComboBox<String> cmbGroup = (JComboBox<String>) formMap.get(StudentTableHeaders.GROUP);
				
				spnId.setValue(studentId);
				tfFirstName.setText(firstName);
				tfLastName.setText(lastName);
				cmbGroup.setSelectedIndex(groupIdx);
			}
		} else if (source == btnSearch) {
			filterTable();
		} else if (source == btnClearCriteria) {
			resetFilterCriteria();
			updateTable();
		}
		
	}

	@Override
	public void pushToDB() {
		List<UniStudent> studentsToDB = new ArrayList<UniStudent>(students);
		Map<UniStudent, UniGroup> groupAssignmentsToDB = new HashMap<UniStudent, UniGroup>(groupAssignments);
		
		UniDB db = InternalData.DATABASE;
		
		synchronized (db) {
			db.setStudentList(studentsToDB);
		}
		
		unsavedChanges = false;
	}


	// Getters & setters
	
	public boolean isUnsavedChanges() {
		return unsavedChanges;
	}
	
}
