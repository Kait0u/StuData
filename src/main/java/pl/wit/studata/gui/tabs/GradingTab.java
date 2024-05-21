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
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;

import pl.wit.studata.InternalData;
import pl.wit.studata.backend.UniDB;
import pl.wit.studata.backend.models.ClassCriterion;
import pl.wit.studata.backend.models.UniClass;
import pl.wit.studata.backend.models.UniStudent;
import pl.wit.studata.gui.dialogs.MessageBoxes;
import pl.wit.studata.gui.enums.GradingTableHeaders;
import pl.wit.studata.gui.interfaces.IDatabaseInteractor;
import pl.wit.studata.gui.widgets.FormWidget;
import pl.wit.studata.gui.widgets.SearchableComboBox;
import pl.wit.studata.gui.widgets.TableWidget;


/**
 * Klasa opisująca zakładkę "Grades"
 * @author Jakub Jaworski
 */
public class GradingTab extends JPanel implements ActionListener, IDatabaseInteractor {

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
	private Map<GradingTableHeaders, Component> formMap = null;
	
	/**
	 * Mapa pola formularza do komponentów formularzowych (formularz wyszukiwania
	 */
	private Map<GradingTableHeaders, Component> formQueryMap = null;
	
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
	 * Lista do przechowywania zajęć pobranych z bazy danych w danym momencie.
	 */
	private List<UniClass> classes = null;
	
	/**
	 * Mapa ocen studentów (wersja lokalna).
	 */
	private Map<UniStudent, Map<UniClass, Map<ClassCriterion, Integer>>> localStudentGradesMap = null;
	
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
	@SuppressWarnings("serial")
	public GradingTab() {
		super();
		
		setLayout(new GridLayout(2, 1));
		
		// Panel górny (wyświetlanie)
		pnlTop = new JPanel();
		pnlTop.setBorder(BorderFactory.createTitledBorder("Data"));
		pnlTop.setLayout(new BorderLayout());
		
		String[] headers = Arrays.asList(GradingTableHeaders.values())
				.stream()
				.map(new Function<GradingTableHeaders, String>() {
					@Override
					public String apply(GradingTableHeaders h) {
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
		
		FormWidget formCreateUpdate = new FormWidget() {{
			setPreferredSize(new Dimension(300, 200));
		}};
		formMap = new LinkedHashMap<>();
		
		for (GradingTableHeaders header: GradingTableHeaders.values()) {
			String label = header.getHeaderName().concat(": ");
			Component comp = null;
			
			switch (header) {
				case STUDENT:
					comp = new SearchableComboBox<UniStudent>(new LinkedList<UniStudent>());
					break;
				case CLASS:
					comp = new SearchableComboBox<UniClass>(new LinkedList<UniClass>());
					break;
				case CRITERION:
					comp = new SearchableComboBox<ClassCriterion>(new LinkedList<ClassCriterion>());
					break;
				case SCORE:
					comp = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
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
		for (GradingTableHeaders header: GradingTableHeaders.values()) {
			String label = header.getHeaderName().concat(": ");
			Component comp = null;
			
			switch (header) {
				case STUDENT:
					comp = new SearchableComboBox<UniStudent>(new LinkedList<UniStudent>()) {{
						setMinimumSize(new Dimension(250, 25));
					}};
					break;
				case CLASS:
					comp = new SearchableComboBox<UniClass>(new LinkedList<UniClass>());
					break;
				case CRITERION:
					comp = new SearchableComboBox<ClassCriterion>(new LinkedList<ClassCriterion>());
					break;
				case SCORE:
//					comp = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
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
	 * Metoda dodająca wiersz do tabeli na podstawie istniejących obiektów.
	 * @param s Student
	 * @param cl Przedmiot
	 * @param cr Kryterium
	 * @param score Wynik
	 */
	private void addTableRow(UniStudent s, UniClass cl, ClassCriterion cr, int score) {
		if (s == null || cl == null || cr == null) return;
		
		Object[] rowData = new Object[] {
			s,
			cl,
			cr,
			Integer.valueOf(score)
		};
		
		tblData.addDataRow(rowData);
	}
	
	/**
	 * Aktualizuje wiersz w tabeli o zadanym indeksie informacjami z istniejących obiektów.
	 * @param rowIdx Indeks wiersza
	 * @param s Student
	 * @param cl Przedmiot
	 * @param cr Kryterium
	 * @param score Wynik
	 */
	private void updateTableRow(int rowIdx, UniStudent s, UniClass cl, ClassCriterion cr, int score) {
		if (s == null || cl == null || cr == null) return;
		if (rowIdx < 0 || rowIdx > tblData.getRowCount()) return;	
				
		Object[] rowData = new Object[] {
			s,
			cl,
			cr,
			Integer.valueOf(score),
		};
		
		tblData.updateRow(rowIdx, rowData);
	}
	
	/**
	 * Metoda szukająca wiersza, który reprezentuje daną ocenę daną trzema identyfikatorami.
	 * @param s Student
	 * @param cl Przedmiot
	 * @param cr Kryterium
	 * @return Indeks wiersza, lub -1 jeśli nic nie znaleziono.
	 */
	private int getRowIdx(UniStudent s, UniClass cl, ClassCriterion cr) {
		if (s == null || cl == null || cr == null) return -1;
		
		int rowCount = tblData.getRowCount();
		
		for (int idx = 0; idx < rowCount; ++idx) {
			// Wyciągnij
			Object[] row = tblData.getRow(idx);			
			UniStudent foundS = (UniStudent) row[0];
			UniClass foundCl = (UniClass) row[1];
			ClassCriterion foundCr = (ClassCriterion) row[2];

			// Porównaj
			boolean c1 = s == foundS;;
			boolean c2 = cl == foundCl;
			boolean c3 = cr == foundCr;
			
			if (c1 && c2 && c3) return idx;
		}
		
		return -1;
	}
	
	/**
	 * Metoda, która na podstawie pobranych wcześniej danych czyści a następnie zapełnia ponownie tablicę.
	 */
	private void updateTable() {
		if (tblData == null) return;
		
		tblData.clear();
		
		if (localStudentGradesMap != null) {
			for (Map.Entry<UniStudent, Map<UniClass, Map<ClassCriterion, Integer>>> studentMap: localStudentGradesMap.entrySet()) {
				UniStudent s = studentMap.getKey();
				Map<UniClass, Map<ClassCriterion, Integer>> classCritScoreMap = studentMap.getValue();
				for (Map.Entry<UniClass, Map<ClassCriterion, Integer>> classMap: classCritScoreMap.entrySet()) {
					UniClass cl = classMap.getKey();
					Map<ClassCriterion, Integer> critScoreMap = classMap.getValue();
					for (Map.Entry<ClassCriterion, Integer> criterionMap: critScoreMap.entrySet()) {
						ClassCriterion cr = criterionMap.getKey();
						int score = criterionMap.getValue();
						
						addTableRow(s, cl, cr, score);
					}
				}
			}
		} 	
	}
	
	/**
	 * Metoda, która na podstawie pobranych wcześniej danych odpowiednio resetuje a następnie uaktualnia widżety formularza.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private void updateWidgets() {
		SearchableComboBox<UniStudent> scmbStudent = (SearchableComboBox<UniStudent>) formMap.get(GradingTableHeaders.STUDENT);
		SearchableComboBox<UniStudent> scmbStudentQ = (SearchableComboBox<UniStudent>) formQueryMap.get(GradingTableHeaders.STUDENT);
		scmbStudent.removeAllItems();
		scmbStudentQ.removeAllItems();
		
		SearchableComboBox<UniClass> scmbClass = (SearchableComboBox<UniClass>) formMap.get(GradingTableHeaders.CLASS);
		SearchableComboBox<UniClass> scmbClassQ = (SearchableComboBox<UniClass>) formQueryMap.get(GradingTableHeaders.CLASS);
		scmbClass.removeAllItems();
		scmbClassQ.removeAllItems();
		
		SearchableComboBox<ClassCriterion> scmbCrit = (SearchableComboBox<ClassCriterion>) formMap.get(GradingTableHeaders.CRITERION);
		SearchableComboBox<ClassCriterion> scmbCritQ = (SearchableComboBox<ClassCriterion>) formQueryMap.get(GradingTableHeaders.CRITERION);
		scmbCrit.removeAllItems();
		scmbCritQ.removeAllItems();
		scmbCrit.setEnabled(false);
		scmbCritQ.setEnabled(false);

		JSpinner spnScore = (JSpinner) formMap.get(GradingTableHeaders.SCORE);
		
		if (students != null) {
			for (UniStudent s: students) {
				scmbStudent.addItem(s);
				scmbStudentQ.addItem(s);
			}
		}
		
		// scmbStudent
		ActionListener[] scmbStudentActionListeners = scmbStudent.getActionListeners();
		for (ActionListener al: scmbStudentActionListeners) {
			scmbStudent.removeActionListener(al);
		}
		
		scmbStudent.addActionListener((e) -> {
			UniStudent s = null;
			try {
				s = (UniStudent) scmbStudent.getSelectedItem();
			} catch (Exception ex) {}
		});
		
		// scmbStudentQ
		ActionListener[] scmbStudentQActionListeners = scmbStudentQ.getActionListeners();
		for (ActionListener al: scmbStudentQActionListeners) {
			scmbStudentQ.removeActionListener(al);
		}
		
		scmbStudentQ.addActionListener((e) -> {
			UniStudent s = null;
			try {
				s = (UniStudent) scmbStudentQ.getSelectedItem();
			} catch (Exception ex) {}
		});
		
		// ------------------------------
		
		if (classes != null) {
			for (UniClass c: classes) {
				scmbClass.addItem(c);
				scmbClassQ.addItem(c);
			}	
		}
		
		// scmbClass
		ActionListener[] scmbClassActionListeners = scmbClass.getActionListeners();
		for (ActionListener al: scmbClassActionListeners) {
			scmbClass.removeActionListener(al);
		}
		
		scmbClass.addActionListener((e) -> {
			UniClass cl = null;
			try {
				cl = (UniClass) scmbClass.getSelectedItem();
			} catch (Exception ex) {}
			
			if (cl == null) {
				spnScore.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));	
				scmbCrit.removeAllItems();
				scmbCrit.setEnabled(false);
				return;
			}
			
			List<ClassCriterion> criteria = cl.getCriteriaList();
			scmbCrit.removeAllItems();
			for (ClassCriterion cr: criteria) {
				scmbCrit.addItem(cr);
			}
			scmbCrit.setEnabled(true);
		});
		
		// scmbClassQ
		ActionListener[] scmbClassQActionListeners = scmbClassQ.getActionListeners();
		for (ActionListener al: scmbClassQActionListeners) {
			scmbClassQ.removeActionListener(al);
		}
		
		scmbClassQ.addActionListener((e) -> {
			UniClass cl = null;
			try {
				cl = (UniClass) scmbClassQ.getSelectedItem();
			} catch (Exception ex) {}
			
			if (cl == null) {	
				scmbCritQ.removeAllItems();
				scmbCritQ.setEnabled(false);
				return;
			}
			
			List<ClassCriterion> criteria = cl.getCriteriaList();
			scmbCritQ.removeAllItems();
			for (ClassCriterion cr: criteria) {
				scmbCritQ.addItem(cr);
			}
			scmbCritQ.setEnabled(true);
		});
		
		// ------------------------------
		
		// scmbCrit
		
		ActionListener[] scmbCritActionListeners = scmbCrit.getActionListeners();
		for (ActionListener al: scmbCritActionListeners) {
			scmbCrit.removeActionListener(al);
		}
		
		scmbCrit.addActionListener((e) -> {
			ClassCriterion cr = null;
			try {
				cr = (ClassCriterion) scmbCrit.getSelectedItem();
			} catch (Exception ex) {}
			
			if (cr == null) {
				spnScore.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
				return;
			}
			
			int maxValue = cr.getMaxPoints();
			spnScore.setModel(new SpinnerNumberModel(0, 0, maxValue, 1));
		});
	}
	
	private void nestedCopyMap(
			Map<UniStudent, Map<UniClass, Map<ClassCriterion, Integer>>> outMap,
			Map<UniStudent, Map<UniClass, Map<ClassCriterion, Integer>>> inMap) {
		if (inMap == null || outMap == null)
			return;

		Map<UniStudent, Map<UniClass, Map<ClassCriterion, Integer>>> result = new HashMap<>();

		for (Map.Entry<UniStudent, Map<UniClass, Map<ClassCriterion, Integer>>> studentMap : inMap.entrySet()) {
			UniStudent s = studentMap.getKey();
			Map<UniClass, Map<ClassCriterion, Integer>> classCritScoreMap = studentMap.getValue();
			outMap.put(s, new HashMap<>());

			// Creating new inner maps for studentGradesMap
			Map<UniClass, Map<ClassCriterion, Integer>> newClassCritScoreMapStudent = new HashMap<>();

			for (Map.Entry<UniClass, Map<ClassCriterion, Integer>> classMap : classCritScoreMap.entrySet()) {
				UniClass cl = classMap.getKey();
				outMap.get(s).put(cl, new HashMap<>());
				Map<ClassCriterion, Integer> critScoreMap = classMap.getValue();

				// Creating new inner maps for each UniClass in studentGradesMap
				Map<ClassCriterion, Integer> newCritScoreMapStudent = new HashMap<>(critScoreMap);
				
				// Putting the new maps into the outer maps
				newClassCritScoreMapStudent.put(cl, newCritScoreMapStudent);
				outMap.get(s).get(cl).putAll(newCritScoreMapStudent);
			}

			// Putting the new maps into the final maps
			result.put(s, newClassCritScoreMapStudent);
		}
	}

	@Override
	public void pullFromDB() {
		students = new ArrayList<>();
		classes = new ArrayList<>();
		localStudentGradesMap = new HashMap<>();
		
		Thread tStudents = new Thread(() -> {
			UniDB db = InternalData.DATABASE;
			List<UniStudent> dbStudents = null;
			synchronized (db) {
				dbStudents = db.getStudentList();
			}
			students.addAll(dbStudents);
		});
		
		Thread tClasses = new Thread(() -> {
			UniDB db = InternalData.DATABASE;
			List<UniClass> dbClasses = null;
			synchronized (db) {
				dbClasses = db.getClassList();
			}
			classes.addAll(dbClasses);
		});
		
		InternalData.EXECUTOR.execute(tStudents);
		InternalData.EXECUTOR.execute(tClasses);
		
		synchronized (localStudentGradesMap) {
			UniDB db = InternalData.DATABASE;
			Map<UniStudent, Map<UniClass, Map<ClassCriterion, Integer>>> dbMap = null;

			synchronized (db) {
			    dbMap = db.getStudentGradesMap();
			}
			
			synchronized (dbMap) {
				nestedCopyMap(localStudentGradesMap, dbMap);
			}
		}    
		
		try {
			tStudents.join();
			tClasses.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void merge() {
		
	}

	@Override
	public void pushToDB() {
		UniDB db = InternalData.DATABASE;
		Map<UniStudent, Map<UniClass, Map<ClassCriterion, Integer>>> mapToDB = localStudentGradesMap;
		
		synchronized (localStudentGradesMap) {
			synchronized (db) {
				db.updateStudentGradesMap(mapToDB);
			}
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

	@Override
	public void update() {
		pullFromDB();
		updateTable();
		updateWidgets();

	}
	
	private boolean addScore(UniStudent s, UniClass cl, ClassCriterion cr, Integer score) {
		if (s == null || cl == null || cr == null) return false;
		
		
		if (!localStudentGradesMap.containsKey(s)) {
			localStudentGradesMap.put(s, new HashMap<>());
		}
		
		if (!localStudentGradesMap.get(s).containsKey(cl)) {
			localStudentGradesMap.get(s).put(cl, new HashMap<>());
		}
		
		localStudentGradesMap.get(s).get(cl).put(cr, score);
		
		addTableRow(s, cl, cr, score);
		return true;
	}
	
	private boolean updateScore(UniStudent s, UniClass cl, ClassCriterion cr, Integer score) {
		if (s == null || cl == null || cr == null) return false;
		
		localStudentGradesMap.get(s).get(cl).put(cr, score);
		
		int idx = getRowIdx(s, cl, cr);
		updateTableRow(idx, s, cl, cr, score);
		
		return true;
	}
	
	private boolean deleteScore(UniStudent s, UniClass cl, ClassCriterion cr) {
		if (s == null || cl == null || cr == null) return false;
		
		localStudentGradesMap.get(s).get(cl).remove(cr);
		
		int idx = getRowIdx(s, cl, cr);
		tblData.deleteRow(idx);
		tblData.clearSelection();
		
		return true;
	}
	
	private void resetFilterCriteria() {
		@SuppressWarnings("unchecked")
		SearchableComboBox<UniStudent> scmbStudentQ = (SearchableComboBox<UniStudent>) formQueryMap.get(GradingTableHeaders.STUDENT);
		
		@SuppressWarnings("unchecked")
		SearchableComboBox<UniClass> scmbClassQ = (SearchableComboBox<UniClass>) formQueryMap.get(GradingTableHeaders.CLASS);
		
		@SuppressWarnings("unchecked")
		SearchableComboBox<ClassCriterion> scmbCritQ = (SearchableComboBox<ClassCriterion>) formQueryMap.get(GradingTableHeaders.CRITERION);
		
		scmbStudentQ.setSelectedItem(null);
		scmbClassQ.setSelectedItem(null);
		scmbCritQ.setSelectedItem(null);
	}
	
	private void filterTable() {
		updateTable();
		
		@SuppressWarnings("unchecked")
		SearchableComboBox<UniStudent> scmbStudentQ = (SearchableComboBox<UniStudent>) formQueryMap.get(GradingTableHeaders.STUDENT);
		
		@SuppressWarnings("unchecked")
		SearchableComboBox<UniClass> scmbClassQ = (SearchableComboBox<UniClass>) formQueryMap.get(GradingTableHeaders.CLASS);
		
		@SuppressWarnings("unchecked")
		SearchableComboBox<ClassCriterion> scmbCritQ = (SearchableComboBox<ClassCriterion>) formQueryMap.get(GradingTableHeaders.CRITERION);
		
		UniStudent sQuery = null;
		UniClass clQuery = null;
		ClassCriterion crQuery = null;
		
		
		try {
			sQuery = (UniStudent) scmbStudentQ.getSelectedItem();
			clQuery = (UniClass) scmbClassQ.getSelectedItem();
			crQuery = (ClassCriterion) scmbCritQ.getSelectedItem(); 
		} catch (Exception ex) {
			return;
		}
		
		List<Integer> rowsToDelete = new ArrayList<Integer>(tblData.getRowCount());
		
		for (int rowIdx = 0; rowIdx < tblData.getRowCount(); ++rowIdx) {
			boolean criteriaMet = true;
			
			Object[] row = tblData.getRow(rowIdx);
			
			if (criteriaMet && sQuery != null) {
				Object val = row[GradingTableHeaders.STUDENT.ordinal()];
				criteriaMet &= (sQuery == val);
			}
			if (criteriaMet && clQuery != null) {
				Object val = row[GradingTableHeaders.CLASS.ordinal()];
				criteriaMet &= (clQuery == val);
			}
			if (criteriaMet && crQuery != null) {
				Object val = row[GradingTableHeaders.CRITERION.ordinal()];
				criteriaMet &= (crQuery == val);
			}
			
			// Oznacz do usunięcia jeśli koniunkcja niespełniona
			if (!criteriaMet) {
				rowsToDelete.add(rowIdx);
			}
		}
		
		tblData.deleteMultipleRows(rowsToDelete);
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		@SuppressWarnings("unchecked")
		SearchableComboBox<UniStudent> scmbStudent = (SearchableComboBox<UniStudent>) formMap.get(GradingTableHeaders.STUDENT);
		
		@SuppressWarnings("unchecked")
		SearchableComboBox<UniClass> scmbClass = (SearchableComboBox<UniClass>) formMap.get(GradingTableHeaders.CLASS);
		
		@SuppressWarnings("unchecked")
		SearchableComboBox<ClassCriterion> scmbCrit = (SearchableComboBox<ClassCriterion>) formMap.get(GradingTableHeaders.CRITERION);
		
		JSpinner spnScore = (JSpinner) formMap.get(GradingTableHeaders.SCORE);
		
		if (source == btnSubmit) {
			UniStudent s = null;
			UniClass cl = null;
			ClassCriterion cr = null;
			Integer score = null; 
			try {
				s = (UniStudent) scmbStudent.getSelectedItem();
				cl = (UniClass) scmbClass.getSelectedItem();
				cr = (ClassCriterion) scmbCrit.getSelectedItem();
				score = (Integer) spnScore.getValue();
			} catch (Exception ex) {}
			
			if (s == null || cl == null || cr == null || score == null) {
				MessageBoxes.showErrorBox("Error!", "Erroneous data!");
				return;
			}
			
			if (!cr.validateScore(score)) {
				MessageBoxes.showErrorBox("Error!", "Erroneous score!");
				return;
			}
			
			int rowIdx = getRowIdx(s, cl, cr);
			if (rowIdx < 0 ) {
				unsavedChanges |= addScore(s, cl, cr, score);
			} else {
				int oldScore = (Integer) tblData.getRow(rowIdx)[GradingTableHeaders.SCORE.ordinal()];
				StringBuilder sb = new StringBuilder("Are you sure that you want to update this score?\n")
						.append('\n').append("Student: ").append(s.toString())
						.append('\n').append("Class: ").append(cl.toString())
						.append('\n').append("Criterion: ").append(cr.toString())
						.append('\n').append("Score: ").append(oldScore).append(" ---> ").append(score);
				if (MessageBoxes.showConfirmationBox("Are you sure?", sb.toString()))
					unsavedChanges |= updateScore(s, cl, cr, score);
			}
		}  else if (source == btnDeselect) {
			tblData.clearSelection();
		} else if (source == btnDelete) {
			int selectedIdx = tblData.getSelectedRow();
			if (selectedIdx == -1) {
				MessageBoxes.showInfoBox("No selection!", "Please select a row first!");
				return;
			}
			
			Object[] row = tblData.getRow(selectedIdx);
			UniStudent s = (UniStudent) row[GradingTableHeaders.STUDENT.ordinal()];
			UniClass cl = (UniClass) row[GradingTableHeaders.CLASS.ordinal()];
			ClassCriterion cr = (ClassCriterion) row[GradingTableHeaders.CRITERION.ordinal()];
			int score = (Integer) row[GradingTableHeaders.SCORE.ordinal()];
			
			StringBuilder sb = new StringBuilder("Are you sure that you want to DELETE this score?")
					.append('\n').append("Student: ").append(s.toString())
					.append('\n').append("Class: ").append(cl.toString())
					.append('\n').append("Criterion: ").append(cr.toString())
					.append('\n').append("Score: ").append(score);
			if (MessageBoxes.showConfirmationBox("Are you sure?", sb.toString()))
				unsavedChanges |= deleteScore(s, cl, cr);	
		} else if (source == btnEdit) {
			int selectedIdx = tblData.getSelectedRow();
			if (selectedIdx == -1) {
				MessageBoxes.showInfoBox("No selection!", "Please select a row first!");
				return;
			}
			
			Object[] row = tblData.getRow(selectedIdx);
			UniStudent s = (UniStudent) row[GradingTableHeaders.STUDENT.ordinal()];
			UniClass cl = (UniClass) row[GradingTableHeaders.CLASS.ordinal()];
			ClassCriterion cr = (ClassCriterion) row[GradingTableHeaders.CRITERION.ordinal()];
			int score = (Integer) row[GradingTableHeaders.SCORE.ordinal()];
			
			scmbStudent.setSelectedItem(s);
			scmbClass.setSelectedItem(cl);
			scmbCrit.setSelectedItem(cr);
			spnScore.setValue(Integer.valueOf(score));
			
		} else if (source == btnSearch) {
			filterTable();
		} else if (source == btnClearCriteria) {
			resetFilterCriteria();
			updateTable();
		}	
	}
}
