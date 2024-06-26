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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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
import pl.wit.studata.gui.dialogs.MessageBoxes;
import pl.wit.studata.gui.enums.StudentTableHeaders;
import pl.wit.studata.gui.interfaces.IDatabaseInteractor;
import pl.wit.studata.gui.widgets.FormWidget;
import pl.wit.studata.gui.widgets.TableWidget;

/**
 * Klasa opisująca zakładkę "Student"
 * @author Jakub Jaworski
 */
public class StudentTab extends JPanel implements ActionListener, IDatabaseInteractor {

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
	 * Mapa pozwalająca na pilnowanie i porównywanie zmian, celem późniejszego wprowadzenia ich do bazy.
	 */
	private Map<UniStudent, UniStudent> copyToOriginal = null;
	
	/**
	 * Mapa pozwalająca na pilnowanie i porównywanie zmian, celem późniejszego wprowadzenia ich do bazy.
	 */
	private Map<UniStudent, UniStudent> originalToCopy = null;
	
	/**
	 * Lista do przechowywania grup pobranych z bazy danych w danym momencie.
	 */
	private List<UniGroup> groups = null;
	
	/**
	 * Mapa do przechowywania przypisań studenta do danej grupy.
	 */
	private Map<UniStudent, UniGroup> groupAssignments = null;
	
	/**
	 * Mapa do przechowywania i modyfikacji przypisań studenta do danej grupy.
	 */
	private Map<UniStudent, UniGroup> localGroupAssignments = null;
	
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
				@SuppressWarnings("unchecked")
				JComboBox<String> source = (JComboBox<String>) e.getSource();
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
		pnlQuery = new JPanel(new GridLayout(1, 1));
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
	 * Metoda dodająca wiersz do tabeli na podstawie istniejącego Studenta.
	 * @param s Student do dodania.
	 */
	private void addTableRow(UniStudent s) {
		if (s == null) return;
		
		UniGroup g = localGroupAssignments.getOrDefault(s, null);
		
		Object[] rowData = new Object[] {
			s.getStudentId(),
			s.getFirstName(),
			s.getLastName(),
			g != null ? g.toString() : AppData.NONE_TEXT
		};
		
		tblData.addDataRow(rowData);
	}
	
	/**
	 * Aktualizuje wiersz w tabeli o zadanym indeksie informacjami o studencie.
	 * @param rowIdx Indeks wiersza.
	 * @param s Student.
	 */
	private void updateTableRow(int rowIdx, UniStudent s) {
		if (s == null) return;
		
		UniGroup g = localGroupAssignments.getOrDefault(s, null);
				
		Object[] rowData = new Object[] {
				s.getStudentId(),
				s.getFirstName(),
				s.getLastName(),
				g != null ? g.toString() : AppData.NONE_TEXT
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
				addTableRow(s);
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
		@SuppressWarnings("unchecked")
		JComboBox<String> cmbGroupQuery = (JComboBox<String>) formQueryMap.get(StudentTableHeaders.GROUP);
		
		cmbGroup.removeAllItems();
		cmbGroupQuery.removeAllItems();
		
		if (groups != null) 
			for (UniGroup g: groups) {
				cmbGroup.addItem(g.toString());
				cmbGroupQuery.addItem(g.toString());
			}
		
		cmbGroup.addItem(AppData.NONE_TEXT);
		cmbGroupQuery.addItem(AppData.NONE_TEXT);
		cmbGroupQuery.addItem(AppData.ANY_TEXT);
		
		cmbGroup.setSelectedIndex(cmbGroup.getItemCount() - 1);
		cmbGroupQuery.setSelectedIndex(cmbGroupQuery.getItemCount() - 1);
	}
	
	/**
	 * Metoda, która na podstawie pobranych wcześniej danych odpowiednio resetuje a następnie uaktualnia widżety formularza.
	 */
	private void updateWidgets() {
		updateIdSpinner();
		updateGroupCombobox();
	}
	
	@Override
	public void update() {
		pullFromDB();
		updateTable();
		updateWidgets();
	}
	
	/**
	 * Metoda dodająca studenta.
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
			localGroupAssignments.put(s, group);
		
		addTableRow(s);
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
		
		localGroupAssignments.remove(toDelete);
		
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
			localGroupAssignments.put(toUpdate, group);
		else
			localGroupAssignments.remove(toUpdate);
		
		int rowIdx = getRowIdx(toUpdate);
		
		updateTableRow(rowIdx, toUpdate);
		
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
		@SuppressWarnings("unchecked")
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
			Object[] row = tblData.getRow(rowIdx);
			
			if (criteriaMet && idQuery != null) {
				Integer val = (Integer) row[StudentTableHeaders.ID.ordinal()];
				criteriaMet &= val.equals(idQuery);
			}
			if (criteriaMet && queryFName != null) {
				String val = ((String) row[StudentTableHeaders.FNAME.ordinal()]).toUpperCase();
				criteriaMet &= val.contains(queryFName.toUpperCase());
			}
			if (criteriaMet && queryLName != null) {
				String val = ((String) row[StudentTableHeaders.LNAME.ordinal()]).toUpperCase();
				criteriaMet &= val.contains(queryLName.toUpperCase());
			}
			if (criteriaMet && queryGroup != null) {
				String val = ((String) row[StudentTableHeaders.GROUP.ordinal()]);
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
		@SuppressWarnings("unchecked")
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
				if (MessageBoxes.showConfirmationBox("Are you sure?", "Are you sure that you want to update student ".concat(queryStudent.toString()).concat("?")))
					unsavedChanges |= updateStudent(queryStudent, firstName, lastName, group);
				
			}
		} else if (source == btnDeselect) {
			tblData.clearSelection();
			updateWidgets();
		} else if (source == btnDelete) {
			int selectedIdx = tblData.getSelectedRow();
			if (selectedIdx == -1) {
				MessageBoxes.showInfoBox("No selection!", "Please select a row first!");
				return;
			}
			int idVal = (Integer) tblData.getValueAt(selectedIdx, StudentTableHeaders.ID.ordinal());
			
			UniStudent toDelete = findById(idVal);
			if (toDelete == null) return;
			
			if (MessageBoxes.showConfirmationBox("Are you sure?", "Are you sure that you want to DELETE student ".concat(toDelete.toString()).concat("?"))) {
				unsavedChanges |= deleteStudent(toDelete);
				tblData.deleteRow(selectedIdx);
				tblData.clearSelection();
				updateIdSpinner();
			}
		} else if (source == btnEdit) {
			int selectedIdx = tblData.getSelectedRow();
			if (selectedIdx == -1) {
				MessageBoxes.showInfoBox("No selection!", "Please select a row first!");
				return;
			}
			
			int idVal = (Integer) tblData.getValueAt(selectedIdx, StudentTableHeaders.ID.ordinal());
			UniStudent toUpdate = findById(idVal);
			
			if (toUpdate != null) {
				Integer studentId = toUpdate.getStudentId();
				String firstName = toUpdate.getFirstName();
				String lastName = toUpdate.getLastName();
				UniGroup group = localGroupAssignments.getOrDefault(toUpdate, null);
				int groupIdx = (group != null && groups != null) ? groups.indexOf(group) : -1;
				
				JSpinner spnId = (JSpinner) formMap.get(StudentTableHeaders.ID); 
				JTextField tfFirstName = (JTextField) formMap.get(StudentTableHeaders.FNAME);
				JTextField tfLastName = (JTextField) formMap.get(StudentTableHeaders.LNAME);
				@SuppressWarnings("unchecked")
				JComboBox<String> cmbGroup = (JComboBox<String>) formMap.get(StudentTableHeaders.GROUP);
				
				spnId.setValue(studentId);
				tfFirstName.setText(firstName);
				tfLastName.setText(lastName);
				cmbGroup.setSelectedIndex(groupIdx > -1 ? groupIdx : cmbGroup.getItemCount() - 1);
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
		students = new LinkedList<UniStudent>();
		groups = new LinkedList<UniGroup>();
		groupAssignments = new HashMap<UniStudent, UniGroup>();
		localGroupAssignments = new HashMap<UniStudent, UniGroup>();
		
		
		// Skopiuj listy i mapy z bazy do lokalnych
		
		
		Future<?> futStudents = InternalData.EXECUTOR.submit(() -> {
			UniDB dbLocal = InternalData.DATABASE;
			List<UniStudent> dbStudentList = null;
			
			synchronized (dbLocal) {
				dbStudentList = dbLocal.getStudentList();
			}
			
			synchronized (dbStudentList) {
				dbStudentList.forEach((student) -> {
					synchronized (student) {
						UniStudent studentCopy = student.deepCopy();
						synchronized (students) {
							students.add(studentCopy);
						}
						synchronized (copyToOriginal) {
							copyToOriginal.put(studentCopy, student);
						}
						synchronized (originalToCopy) {
							originalToCopy.put(student, studentCopy);
						}
					}
				});
			}
		});
		
		UniDB db = InternalData.DATABASE;
		List<UniGroup> dbGroupList = null;
		
		synchronized (db) {
			dbGroupList = db.getGroupList();
		}
		
		synchronized (dbGroupList) {
			groups.addAll(dbGroupList);
		}
		
		try {
			futStudents.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		

		Future<?> futAssignments = InternalData.EXECUTOR.submit(() -> {
			UniDB dbLocal = InternalData.DATABASE;
			Map<UniStudent, UniGroup> dbGroupAssignments = null;
			
			synchronized (dbLocal) {
				dbGroupAssignments = dbLocal.getStudentGroupMap();
			}
			
			synchronized (dbGroupAssignments) {
				dbGroupAssignments.entrySet().forEach((entry) -> {
					UniStudent student = entry.getKey();
					synchronized (student) {
						UniStudent studentCopy = originalToCopy.get(student);
						synchronized (studentCopy) {
							synchronized (groupAssignments) {
								groupAssignments.put(student, entry.getValue());
							}
							synchronized (localGroupAssignments) {
								localGroupAssignments.put(studentCopy, entry.getValue());
							}
						}
					}
				});
			}
		});
		
		try {
			futAssignments.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void merge() {
		List<UniStudent> tempStudents = new ArrayList<UniStudent>();
		Map<UniStudent, UniGroup> tempAssigmentMap = new HashMap<>();
		
		for (UniStudent sCopy: students) { // sCopy to albo kopia albo świeżo stworzony student
			UniStudent sOrig = null;
			
			if (copyToOriginal.containsKey(sCopy)) {
				sOrig = copyToOriginal.get(sCopy);
				sOrig.setFirstName(sCopy.getFirstName());
				sOrig.setLastName(sCopy.getLastName());
			}
			
			UniGroup currGroup = localGroupAssignments.getOrDefault(sCopy, null);
			
			if (sOrig == null) {
				tempStudents.add(sCopy);
				if (currGroup != null) tempAssigmentMap.put(sCopy, currGroup);
			} else {
				tempStudents.add(sOrig);
				if (currGroup != null) tempAssigmentMap.put(sOrig, currGroup);
			}	
		}
		students = tempStudents;
		localGroupAssignments = tempAssigmentMap;
	}
	
	@Override
	public void pushToDB() {
		merge();
		
		List<UniStudent> studentsToDB = new ArrayList<UniStudent>(students);
		Map<UniStudent, UniGroup> groupAssignmentsToDB = new HashMap<UniStudent, UniGroup>(localGroupAssignments);
		
		UniDB db = InternalData.DATABASE;
		
		synchronized (db) {
			db.updateStudents(studentsToDB);
			db.updateStudentGroupMap(groupAssignmentsToDB);
		}
		
		unsavedChanges = false;
		
		update();
	}

	@Override
	public boolean hasUnsavedChanges() {
		return unsavedChanges;
	}

	@Override
	public void nullifyChanges() {
		unsavedChanges = false;
		update();
	}
	
}
