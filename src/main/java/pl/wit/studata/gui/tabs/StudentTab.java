/**
 * 
 */
package pl.wit.studata.gui.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import pl.wit.studata.AppData;
import pl.wit.studata.InternalData;
import pl.wit.studata.backend.UniDB;
import pl.wit.studata.backend.models.UniGroup;
import pl.wit.studata.backend.models.UniStudent;
import pl.wit.studata.gui.FormWidget;
import pl.wit.studata.gui.enums.StudentTableHeaders;

/**
 * Klasa opisująca zakładkę "Student"
 * @author Jakub Jaworski
 */
public class StudentTab extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Panel górny. 
	 * (Wyświetlanie danych)
	 */
	private JPanel pnlTop = null;
	
	/**
	 * Panel dolny.
	 * (Wprowadzanie danych)
	 */
	private JPanel pnlBot = null;

	/**
	 * Tabela na dane o studentach
	 */
	private JTable tblData = null;
	
	/**
	 * Widok z suwakiem, który zwierać będzie tabelę.
	 * @see tblData
	 */
	private JScrollPane scrlTblData = null;
	
	/**
	 * Mapa pola formularza do komponentów formularzowych
	 */
	private Map<StudentTableHeaders, Component> formMap = null;
	
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
		
		Object[] headers = Arrays.asList(StudentTableHeaders.values())
				.stream()
				.map(new Function<StudentTableHeaders, String>() {
					@Override
					public String apply(StudentTableHeaders h) {
						return h.getHeaderName();
					}
				}).toArray();
		
		tblData = new JTable();
		
		DefaultTableModel model = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		for (Object h: headers)
			model.addColumn(h);
		tblData.setModel(model);
		tblData.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		scrlTblData = new JScrollPane(tblData);
		
		pnlTop.add(scrlTblData, BorderLayout.CENTER);
		
		
		add(pnlTop);
		
		// ----------------------
		
		// Panel dolny (wprowadzanie danych)
		pnlBot = new JPanel();
		pnlBot.setBorder(BorderFactory.createTitledBorder("Form"));
		pnlBot.setLayout(new GridBagLayout());
		GridBagConstraints cPnlBot = new GridBagConstraints();
		cPnlBot.insets = new Insets(0, 20, 0, 20);
		
		FormWidget form = new FormWidget();
		
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
				form.addField(label, comp);
				formMap.put(header, comp);
			}
		}
		
		btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(this);
		form.addWidget(btnSubmit);
		
		pnlBot.add(form, cPnlBot);
		
		// Update-delete frame
		JPanel pnlUpdateDelete = new JPanel();
		pnlUpdateDelete.setLayout(new GridLayout(3, 1));
		pnlUpdateDelete.setBorder(BorderFactory.createTitledBorder("Selection Options"));
		pnlUpdateDelete.setPreferredSize(new Dimension(150, 120));
		
		
		btnEdit = new JButton("Update");
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
		
		++cPnlBot.gridy;
		pnlBot.add(pnlUpdateDelete, cPnlBot);

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
		
		DefaultTableModel tbModel = (DefaultTableModel) tblData.getModel();
		
		Object[] rowData = new Object[] {
			s.getStudentId(),
			s.getFirstName(),
			s.getLastName(),
			g != null ? g.getGroupCode() : AppData.NONE_TEXT
		};
		
		tbModel.addRow(rowData);
	}
	
	/**
	 * Usuwa wiersz z tabeli o zadanym indeksie.
	 * @param rowIdx Indeks do usunięcia.
	 */
	private void deleteTableRow(int rowIdx) {
		((DefaultTableModel) tblData.getModel()).removeRow(rowIdx);
	}
	
	/**
	 * Aktualizuje wiersz w tabeli o zadanym indeksie.
	 * @param rowIdx
	 */
	private void updateTableRow(int rowIdx, UniStudent s, UniGroup g) {
		if (s == null) return;
		
		DefaultTableModel tbModel = (DefaultTableModel) tblData.getModel();
		
		Object[] rowData = new Object[] {
				s.getStudentId(),
				s.getFirstName(),
				s.getLastName(),
				g != null ? g.getGroupCode() : AppData.NONE_TEXT
		};
		
		for (int i = 0; i < rowData.length; ++i)
			tbModel.setValueAt(rowData[i], rowIdx, i);
	}
	
	/**
	 * Metoda szukająca wiersza, który reprezentuje danego studenta, po jego ID.
	 * @param s Student.
	 * @return Indeks wiersza w tabeli, który reprezentuje studenta lub -1, jeżeli nic nie znaleziono.
	 */
	private int getRowIdx(UniStudent s) {
		if (s == null)
			return -1;
		
		int result = 0;
		int studentId = s.getStudentId();
		
		for (; result < tblData.getRowCount(); ++result) {
			int found = (Integer) tblData.getValueAt(result, StudentTableHeaders.ID.ordinal());
			if (studentId == found) break;
		}
		
		result = (result == tblData.getRowCount()) ? -1 : result;
		
		return result;
	}
	
	/**
	 * Metoda, która na podstawie pobranych wcześniej danych czyści a następnie zapełnia ponownie tablicę.
	 */
	private void updateTable() {
		if (tblData == null) return;
		
		DefaultTableModel tbModel = (DefaultTableModel) tblData.getModel();
		tbModel.setRowCount(0);
		
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
				unsavedChanges |= updateStudent(queryStudent, firstName, lastName, group);
			}
		} else if (source == btnDeselect) {
			tblData.clearSelection();
		} else if (source == btnDelete) {
			int selectedIdx = tblData.getSelectedRow();
			if (selectedIdx == -1) return;
			int idVal = (Integer) tblData.getModel().getValueAt(selectedIdx, StudentTableHeaders.ID.ordinal());
			UniStudent toDelete = findById(idVal);
			
			// TODO zapytaj się użytkownika czy na pewno!
			unsavedChanges |= deleteStudent(toDelete);
			deleteTableRow(selectedIdx);
			tblData.clearSelection();
			
			updateIdSpinner();
		} else if (source == btnEdit) {
			int selectedIdx = tblData.getSelectedRow();
			if (selectedIdx == -1) return;
			int idVal = (Integer) tblData.getModel().getValueAt(selectedIdx, StudentTableHeaders.ID.ordinal());
			UniStudent toUpdate = findById(idVal);
			
			if (toUpdate != null) {
				Integer studentId = toUpdate.getStudentId();
				String firstName = toUpdate.getFirstName();
				String lastName = toUpdate.getLastName();
				UniGroup group = groupAssignments.getOrDefault(toUpdate, null);
				int groupIdx = group != null && groups != null ? groups.indexOf(group) : 0;
				
				JSpinner spnId = (JSpinner) formMap.get(StudentTableHeaders.ID); 
				JTextField tfFirstName = (JTextField) formMap.get(StudentTableHeaders.FNAME);
				JTextField tfLastName = (JTextField) formMap.get(StudentTableHeaders.LNAME);
				JComboBox<String> cmbGroup = (JComboBox<String>) formMap.get(StudentTableHeaders.GROUP);
				
				spnId.setValue(studentId);
				tfFirstName.setText(firstName);
				tfLastName.setText(lastName);
				cmbGroup.setSelectedIndex(groupIdx);
			}
		}
		
	}
}
